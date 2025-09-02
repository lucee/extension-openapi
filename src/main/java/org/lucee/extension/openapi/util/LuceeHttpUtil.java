package org.lucee.extension.openapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * HTTP utility that uses Java's built-in HTTP functionality instead of Apache HttpClient
 */
public class LuceeHttpUtil {
	
	private final ObjectMapper objectMapper;
	
	public LuceeHttpUtil() {
		this.objectMapper = new ObjectMapper();
	}
	
	public Object makeRequest(String method, String url, Map<String, String> headers, 
							 Map<String, Object> queryParams, Object requestBody) throws Exception {
		
		// Build the URL with query parameters
		String fullUrl = buildUrlWithQueryParams(url, queryParams);
		
		// Create HTTP connection
		URL urlObj = new URL(fullUrl);
		HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
		
		try {
			// Set method
			connection.setRequestMethod(method.toUpperCase());
			
			// Set timeouts
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			
			// Set default headers
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("User-Agent", "Lucee-OpenAPI-Extension/1.0");
			
			// Add custom headers
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			
			// Add request body for applicable methods
			if (requestBody != null && isEntityEnclosingMethod(method)) {
				connection.setDoOutput(true);
				String jsonBody = objectMapper.writeValueAsString(requestBody);
				
				try (OutputStream os = connection.getOutputStream()) {
					byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
					os.write(input, 0, input.length);
				}
			}
			
			// Execute request and get response
			return processResponse(connection);
			
		} finally {
			connection.disconnect();
		}
	}
	
	private String buildUrlWithQueryParams(String url, Map<String, Object> queryParams) throws Exception {
		if (queryParams == null || queryParams.isEmpty()) {
			return url;
		}
		
		StringBuilder urlBuilder = new StringBuilder(url);
		boolean first = !url.contains("?");
		
		for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
			if (first) {
				urlBuilder.append("?");
				first = false;
			} else {
				urlBuilder.append("&");
			}
			urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
					  .append("=")
					  .append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
		}
		
		return urlBuilder.toString();
	}
	
	private boolean isEntityEnclosingMethod(String method) {
		return "POST".equalsIgnoreCase(method) || 
			   "PUT".equalsIgnoreCase(method) || 
			   "PATCH".equalsIgnoreCase(method);
	}
	
	private Object processResponse(HttpURLConnection connection) throws Exception {
		// Get response code and message
		int statusCode = connection.getResponseCode();
		String statusText = connection.getResponseMessage();
		
		// Extract headers
		Map<String, List<String>> responseHeaders = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
			if (entry.getKey() != null) { // Skip null key (status line)
				responseHeaders.put(entry.getKey(), entry.getValue());
			}
		}
		
		// Get response body
		String responseBody = "";
		try {
			InputStream inputStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
			if (inputStream != null) {
				responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			// Handle cases where there's no response body
		}
		
		// Create response object
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", statusCode);
		result.put("statusText", statusText != null ? statusText : "");
		result.put("headers", responseHeaders);
		
		// Parse JSON response if possible
		if (responseBody != null && !responseBody.isEmpty()) {
			try {
				String trimmed = responseBody.trim();
				if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
					result.put("data", objectMapper.readValue(responseBody, Object.class));
				} else {
					result.put("data", responseBody);
				}
			} catch (Exception e) {
				result.put("data", responseBody);
			}
		}
		
		// Check for HTTP errors
		if (statusCode >= 400) {
			result.put("error", "HTTP " + statusCode + ": " + statusText);
		}
		
		return result;
	}
}

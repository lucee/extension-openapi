package org.lucee.extension.openapi.util;

import java.util.Map;

/**
 * HTTP utility for making REST API calls using Java's built-in HTTP functionality
 */
public class HttpUtil {

	private final LuceeHttpUtil httpUtil;

	/**
	 * Default constructor with default timeout settings
	 */
	public HttpUtil() {
		this.httpUtil = new LuceeHttpUtil();
	}

	/**
	 * Make an HTTP request
	 * 
	 * @param method HTTP method (GET, POST, PUT, DELETE, etc.)
	 * @param url The target URL
	 * @param headers Optional request headers
	 * @param queryParams Optional query parameters
	 * @param requestBody Optional request body for entity-enclosing methods
	 * @return Response object with statusCode, statusText, headers, and data
	 * @throws Exception if the request fails
	 */
	public Object makeRequest(String method, String url, Map<String, String> headers, 
							 Map<String, Object> queryParams, Object requestBody) throws Exception {
		return httpUtil.makeRequest(method, url, headers, queryParams, requestBody);
	}

	/**
	 * Make a GET request
	 * 
	 * @param url The target URL
	 * @param headers Optional request headers
	 * @param queryParams Optional query parameters
	 * @return Response object
	 * @throws Exception if the request fails
	 */
	public Object get(String url, Map<String, String> headers, Map<String, Object> queryParams) throws Exception {
		return makeRequest("GET", url, headers, queryParams, null);
	}

	/**
	 * Make a POST request
	 * 
	 * @param url The target URL
	 * @param headers Optional request headers
	 * @param requestBody Request body
	 * @return Response object
	 * @throws Exception if the request fails
	 */
	public Object post(String url, Map<String, String> headers, Object requestBody) throws Exception {
		return makeRequest("POST", url, headers, null, requestBody);
	}

	/**
	 * Make a PUT request
	 * 
	 * @param url The target URL
	 * @param headers Optional request headers
	 * @param requestBody Request body
	 * @return Response object
	 * @throws Exception if the request fails
	 */
	public Object put(String url, Map<String, String> headers, Object requestBody) throws Exception {
		return makeRequest("PUT", url, headers, null, requestBody);
	}

	/**
	 * Make a DELETE request
	 * 
	 * @param url The target URL
	 * @param headers Optional request headers
	 * @return Response object
	 * @throws Exception if the request fails
	 */
	public Object delete(String url, Map<String, String> headers) throws Exception {
		return makeRequest("DELETE", url, headers, null, null);
	}

	/**
	 * No explicit close needed for built-in HTTP client
	 */
	public void close() {
		// No-op - Java's HttpURLConnection is closed automatically
	}
}

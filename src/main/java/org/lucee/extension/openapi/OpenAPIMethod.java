package org.lucee.extension.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;

import org.lucee.extension.openapi.util.HttpUtil;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Represents a single OpenAPI operation as a callable method
 */
public class OpenAPIMethod {
	
	private final String operationId;
	private final String httpMethod;
	private final String path;
	private final Operation operation;
	private final OpenAPI openAPI;
	private final HttpUtil httpUtil;
	
	public OpenAPIMethod(String operationId, String httpMethod, String path, 
						Operation operation, OpenAPI openAPI, HttpUtil httpUtil) {
		this.operationId = operationId;
		this.httpMethod = httpMethod;
		this.path = path;
		this.operation = operation;
		this.openAPI = openAPI;
		this.httpUtil = httpUtil;
	}
	
	public Object invoke(Object pc, Object[] args, String baseUrl) throws Exception {
		// Convert positional args to named args
		Map<String, Object> namedArgs = convertPositionalArgs(args);
		return invokeWithNamedArgs(pc, namedArgs, baseUrl);
	}
	
	public Object invokeWithNamedArgs(Object pc, Object args, String baseUrl) throws Exception {
		// Convert args to Map if needed
		Map<String, Object> argMap;
		if (args instanceof Map) {
			argMap = (Map<String, Object>) args;
		} else {
			argMap = new HashMap<>();
		}
		
		// Build the complete URL
		String url = buildUrl(baseUrl, argMap);
		
		// Extract headers, query params, and body
		Map<String, String> headers = extractHeaders(argMap);
		Map<String, Object> queryParams = extractQueryParams(argMap);
		Object requestBody = extractRequestBody(argMap);
		
		// Make HTTP request
		return httpUtil.makeRequest(httpMethod, url, headers, queryParams, requestBody);
	}
	
	private Map<String, Object> convertPositionalArgs(Object[] args) throws Exception {
		Map<String, Object> namedArgs = new HashMap<>();
		
		if (operation.getParameters() == null) {
			return namedArgs;
		}
		
		List<Parameter> parameters = operation.getParameters();
		for (int i = 0; i < Math.min(args.length, parameters.size()); i++) {
			Parameter param = parameters.get(i);
			namedArgs.put(param.getName(), args[i]);
		}
		
		return namedArgs;
	}
	
	private String buildUrl(String baseUrl, Map<String, Object> args) throws Exception {
		String url = baseUrl + path;
		
		// Replace path parameters
		if (operation.getParameters() != null) {
			for (Parameter param : operation.getParameters()) {
				if ("path".equals(param.getIn()) && args.containsKey(param.getName())) {
					String placeholder = "{" + param.getName() + "}";
					String value = args.get(param.getName()).toString();
					url = url.replace(placeholder, value);
				}
			}
		}
		
		return url;
	}
	
	private Map<String, String> extractHeaders(Map<String, Object> args) throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		
		if (operation.getParameters() != null) {
			for (Parameter param : operation.getParameters()) {
				if ("header".equals(param.getIn()) && args.containsKey(param.getName())) {
					headers.put(param.getName(), args.get(param.getName()).toString());
				}
			}
		}
		
		return headers;
	}
	
	private Map<String, Object> extractQueryParams(Map<String, Object> args) throws Exception {
		Map<String, Object> queryParams = new HashMap<>();
		
		if (operation.getParameters() != null) {
			for (Parameter param : operation.getParameters()) {
				if ("query".equals(param.getIn()) && args.containsKey(param.getName())) {
					queryParams.put(param.getName(), args.get(param.getName()));
				}
			}
		}
		
		return queryParams;
	}
	
	private Object extractRequestBody(Map<String, Object> args) throws Exception {
		if (operation.getRequestBody() == null) {
			return null;
		}
		
		// Look for a 'body' argument or the first non-parameter argument
		if (args.containsKey("body")) {
			return args.get("body");
		}
		
		// Find the first argument that's not a parameter
		if (operation.getParameters() != null) {
			for (Object key : args.keySet()) {
				String keyStr = key.toString();
				boolean isParameter = operation.getParameters().stream()
					.anyMatch(p -> p.getName().equals(keyStr));
				
				if (!isParameter) {
					return args.get(keyStr);
				}
			}
		}
		
		return null;
	}
	
	public Map<String, Object> getInfo() throws Exception {
		Map<String, Object> info = new HashMap<>();
		
		info.put("operationId", operationId);
		info.put("method", httpMethod);
		info.put("path", path);
		info.put("summary", operation.getSummary() != null ? operation.getSummary() : "");
		info.put("description", operation.getDescription() != null ? operation.getDescription() : "");
		
		// Add parameters info
		if (operation.getParameters() != null) {
			List<Map<String, Object>> params = new ArrayList<>();
			for (Parameter param : operation.getParameters()) {
				Map<String, Object> paramInfo = new HashMap<>();
				paramInfo.put("name", param.getName());
				paramInfo.put("in", param.getIn());
				paramInfo.put("required", param.getRequired() != null ? param.getRequired() : false);
				paramInfo.put("description", param.getDescription() != null ? param.getDescription() : "");
				params.add(paramInfo);
			}
			info.put("parameters", params);
		}
		
		return info;
	}
}

package org.lucee.extension.openapi;

import lucee.loader.engine.CFMLEngine;
import lucee.loader.engine.CFMLEngineFactory;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.servers.Server;

import org.lucee.extension.openapi.util.HttpUtil;

import java.util.Map;
import java.util.HashMap;

/**
 * OpenAPI client that uses only Lucee loader interfaces
 */
public class OpenAPIClient {
	
	private final OpenAPI openAPI;
	private final String specUrl;
	private final Object options;
	private final Map<String, OpenAPIMethod> methods;
	private final HttpUtil httpUtil;
	
	public OpenAPIClient(Object pageContext, OpenAPI openAPI, String specUrl, Object options) 
			throws Exception {
		
		this.openAPI = openAPI;
		this.specUrl = specUrl;
		this.options = options;
		this.methods = new HashMap<>();
		this.httpUtil = new HttpUtil();
		
		initializeMethods();
	}
	
	private void initializeMethods() throws Exception {
		if (openAPI.getPaths() == null) return;
		
		// Parse all paths and operations
		for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
			String path = pathEntry.getKey();
			PathItem pathItem = pathEntry.getValue();
			
			// Handle different HTTP methods
			addOperation("GET", path, pathItem.getGet());
			addOperation("POST", path, pathItem.getPost());
			addOperation("PUT", path, pathItem.getPut());
			addOperation("DELETE", path, pathItem.getDelete());
			addOperation("PATCH", path, pathItem.getPatch());
			addOperation("HEAD", path, pathItem.getHead());
			addOperation("OPTIONS", path, pathItem.getOptions());
		}
	}
	
	private void addOperation(String httpMethod, String path, Operation operation) 
			throws Exception {
		if (operation == null) return;
		
		String operationId = operation.getOperationId();
		if (operationId == null || operationId.isEmpty()) {
			// Generate operation ID if not provided
			operationId = generateOperationId(httpMethod, path);
		}
		
		OpenAPIMethod method = new OpenAPIMethod(
			operationId, httpMethod, path, operation, openAPI, httpUtil
		);
		
		methods.put(operationId.toLowerCase(), method);
	}
	
	private String generateOperationId(String httpMethod, String path) {
		// Convert /users/{id} to getUsersById
		String cleanPath = path.replaceAll("\\{([^}]+)\\}", "By$1")
							  .replaceAll("[^a-zA-Z0-9]", "");
		return httpMethod.toLowerCase() + 
			   cleanPath.substring(0, 1).toUpperCase() + 
			   cleanPath.substring(1);
	}
	
	/**
	 * Call a method dynamically
	 */
	public Object callMethod(String methodName, Object[] args) throws Exception {
		OpenAPIMethod method = methods.get(methodName.toLowerCase());
		if (method == null) {
			CFMLEngine engine = CFMLEngineFactory.getInstance();
			throw engine.getExceptionUtil().createApplicationException("Method '" + methodName + "' not found in OpenAPI specification");
		}
		
		try {
			return method.invoke(null, args, getBaseUrl());
		} catch (Exception e) {
			CFMLEngine engine = CFMLEngineFactory.getInstance();
			throw engine.getExceptionUtil().createApplicationException("Error calling OpenAPI method '" + methodName + "': " + e.getMessage());
		}
	}
	
	/**
	 * Call a method with named arguments
	 */
	public Object callMethodWithNamedArgs(String methodName, Object args) throws Exception {
		OpenAPIMethod method = methods.get(methodName.toLowerCase());
		if (method == null) {
			CFMLEngine engine = CFMLEngineFactory.getInstance();
			throw engine.getExceptionUtil().createApplicationException("Method '" + methodName + "' not found in OpenAPI specification");
		}
		
		try {
			return method.invokeWithNamedArgs(null, args, getBaseUrl());
		} catch (Exception e) {
			CFMLEngine engine = CFMLEngineFactory.getInstance();
			throw engine.getExceptionUtil().createApplicationException("Error calling OpenAPI method '" + methodName + "': " + e.getMessage());
		}
	}
	
	private String getBaseUrl() {
		// Get base URL from servers or construct from spec URL
		if (openAPI.getServers() != null && !openAPI.getServers().isEmpty()) {
			Server server = openAPI.getServers().get(0);
			return server.getUrl();
		}
		
		// Fallback: extract base URL from spec URL
		try {
			java.net.URL url = new java.net.URL(specUrl);
			return url.getProtocol() + "://" + url.getHost() + 
				   (url.getPort() != -1 ? ":" + url.getPort() : "");
		} catch (Exception e) {
			return "http://localhost";
		}
	}
	
	/**
	 * Get property value
	 */
	public Object getProperty(String propertyName) {
		switch (propertyName.toLowerCase()) {
			case "_methods":
				Map<String, Object> methodsInfo = new HashMap<>();
				for (Map.Entry<String, OpenAPIMethod> entry : methods.entrySet()) {
					try {
						methodsInfo.put(entry.getKey(), entry.getValue().getInfo());
					} catch (Exception e) {
						// Ignore errors in method info
					}
				}
				return methodsInfo;
			case "_spec":
				return specUrl;
			case "_baseurl":
				return getBaseUrl();
			default:
				return null;
		}
	}
	
	/**
	 * Get available method names
	 */
	public String[] getMethodNames() {
		return methods.keySet().toArray(new String[0]);
	}
	
	/**
	 * Check if method exists
	 */
	public boolean hasMethod(String methodName) {
		return methods.containsKey(methodName.toLowerCase());
	}
	
	public String getDisplayName() {
		return "OpenAPIClient(" + specUrl + ")";
	}
	
	/**
	 * Cleanup resources when the client is no longer needed
	 */
	public void close() {
		try {
			if (httpUtil != null) {
				httpUtil.close();
			}
		} catch (Exception e) {
			// Log but don't throw - cleanup should not fail the application
			System.err.println("Warning: Failed to close HttpUtil resources: " + e.getMessage());
		}
	}
}

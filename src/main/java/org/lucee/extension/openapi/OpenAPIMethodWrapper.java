package org.lucee.extension.openapi;

/**
 * Wrapper class that makes OpenAPI methods callable from CFML
 */
public class OpenAPIMethodWrapper {
	
	private final OpenAPIClient javaClient;
	private final String operationId;
	
	public OpenAPIMethodWrapper(OpenAPIClient javaClient, String operationId) {
		this.javaClient = javaClient;
		this.operationId = operationId;
	}
	
	/**
	 * Called when the method is invoked from CFML
	 * This method signature allows it to be called like a function
	 */
	public Object call(Object... args) throws Exception {
		if (args.length == 0) {
			return javaClient.callMethod(operationId, new Object[0]);
		} else if (args.length == 1 && args[0] instanceof java.util.Map) {
			// Called with named arguments (struct)
			return javaClient.callMethodWithNamedArgs(operationId, args[0]);
		} else {
			// Called with positional arguments
			return javaClient.callMethod(operationId, args);
		}
	}
	
	/**
	 * Allow the wrapper to be called directly
	 */
	public Object invoke(Object... args) throws Exception {
		return call(args);
	}
	
	/**
	 * String representation for debugging
	 */
	@Override
	public String toString() {
		return "OpenAPIMethod[" + operationId + "]";
	}
	
	/**
	 * Get the operation ID
	 */
	public String getOperationId() {
		return operationId;
	}
}

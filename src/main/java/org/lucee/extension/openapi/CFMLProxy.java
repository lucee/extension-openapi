package org.lucee.extension.openapi;

/**
 * A CFML-compatible proxy that delegates method calls to the OpenAPI client
 * This class acts like a CFC by implementing dynamic method calling
 */
public class CFMLProxy {
	
	private final OpenAPIClient javaClient;
	
	public CFMLProxy(OpenAPIClient javaClient) {
		this.javaClient = javaClient;
	}
	
	/**
	 * Handle dynamic method calls from CFML
	 * This method will be called when CFML tries to invoke a method on this object
	 */
	public Object onMissingMethod(String methodName, Object args) throws Exception {
		// Check if this is a property access
		if (methodName.startsWith("get_") || methodName.startsWith("_")) {
			return getProperty(methodName);
		}
		
		// Delegate to the Java client for API method calls
		if (args != null && args.getClass().isArray()) {
			return javaClient.callMethod(methodName, (Object[]) args);
		} else if (args != null) {
			return javaClient.callMethodWithNamedArgs(methodName, args);
		} else {
			return javaClient.callMethod(methodName, new Object[0]);
		}
	}
	
	/**
	 * Get property values
	 */
	public Object getProperty(String propertyName) {
		String cleanName = propertyName.startsWith("get_") ? propertyName.substring(4) : propertyName;
		cleanName = cleanName.startsWith("_") ? cleanName : "_" + cleanName;
		
		return javaClient.getProperty(cleanName);
	}
	
	/**
	 * Expose the _methods property
	 */
	public Object get_methods() {
		return javaClient.getProperty("_methods");
	}
	
	/**
	 * Expose the _spec property  
	 */
	public Object get_spec() {
		return javaClient.getProperty("_spec");
	}
	
	/**
	 * Expose the _baseurl property
	 */
	public Object get_baseurl() {
		return javaClient.getProperty("_baseurl");
	}
	
	/**
	 * Check if a method exists
	 */
	public boolean hasMethod(String methodName) {
		return javaClient.hasMethod(methodName);
	}
	
	/**
	 * Get available method names
	 */
	public String[] getMethodNames() {
		return javaClient.getMethodNames();
	}
	
	/**
	 * String representation
	 */
	@Override
	public String toString() {
		return javaClient.getDisplayName();
	}
	
	/**
	 * Allow calling methods directly through reflection/dynamic dispatch
	 * This should handle CFML's dynamic method calling
	 */
	public Object call(String methodName, Object... args) throws Exception {
		return javaClient.callMethod(methodName, args);
	}
	
	/**
	 * Handle calls with named arguments (struct)
	 */
	public Object callWithNamedArgs(String methodName, Object args) throws Exception {
		return javaClient.callMethodWithNamedArgs(methodName, args);
	}
}

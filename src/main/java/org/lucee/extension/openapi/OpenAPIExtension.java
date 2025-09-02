package org.lucee.extension.openapi;

/**
 * Main OpenAPI extension class for Lucee Server
 * For Lucee 7.x, extensions are primarily handled via OSGi bundles
 * and the createObject configuration in the manifest
 */
public class OpenAPIExtension {
	
	private static final String EXTENSION_ID = "B737ABC4-D43C-40F1-833F832E4E2E4D38";
	
	/**
	 * Extension initialization - called when the extension is loaded
	 */
	public static void init() {
		System.out.println("OpenAPI Extension initialized successfully");
	}
	
	/**
	 * Extension cleanup - called when the extension is unloaded
	 */
	public static void destroy() {
		System.out.println("OpenAPI Extension destroyed");
	}
	
	public static String getId() {
		return EXTENSION_ID;
	}
}

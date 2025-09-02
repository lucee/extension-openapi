package org.lucee.extension.openapi;

import lucee.runtime.PageContext;
import io.swagger.v3.oas.models.OpenAPI;

/**
 * Generates dynamic CFC proxy code for OpenAPI specifications
 */
public class CFCProxyGenerator {
	
	private final OpenAPI openAPI;
	private final String specUrl;
	private final Object options;
	
	public CFCProxyGenerator(OpenAPI openAPI, String specUrl, Object options) {
		this.openAPI = openAPI;
		this.specUrl = specUrl;
		this.options = options;
	}
	
	/**
	 * Generates a CFC proxy and returns an instance of it
	 */
	public Object generateCFCProxy(PageContext pc) throws Exception {
		// Create the underlying Java client
		OpenAPIClient javaClient = new OpenAPIClient(pc, openAPI, specUrl, options);
		
		// Return a dynamic proxy that implements method calling
		return new CFMLProxy(javaClient);
	}
}

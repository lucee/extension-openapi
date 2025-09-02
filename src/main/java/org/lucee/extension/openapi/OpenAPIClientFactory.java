package org.lucee.extension.openapi;

import lucee.loader.engine.CFMLEngine;
import lucee.loader.engine.CFMLEngineFactory;

import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

/**
 * Factory class for creating OpenAPI client objects
 * Integrates with Lucee's extension system via CFML functions
 */
public class OpenAPIClientFactory {
	
	public OpenAPIClientFactory() {
		// Default constructor
	}
	
	/**
	 * Creates an OpenAPI client from a specification URL
	 * Called by the CFML createOpenApiProxy function
	 */
	public Object invoke(Object pc, String specUrl, Object options) throws Exception {
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		
		try {
			return createOpenAPIClient(pc, specUrl, options);
		} catch (Exception e) {
			throw engine.getExceptionUtil().createApplicationException("Failed to create OpenAPI client: " + e.getMessage());
		}
	}
	
	private static Object createOpenAPIClient(Object pc, String specUrl, Object options) throws Exception {
		// Parse the OpenAPI specification
		OpenAPIV3Parser parser = new OpenAPIV3Parser();
		SwaggerParseResult result = parser.readLocation(specUrl, null, null);
		
		if (result.getOpenAPI() == null) {
			throw new Exception("Failed to parse OpenAPI specification from: " + specUrl);
		}
		
		OpenAPI openAPI = result.getOpenAPI();
		
		// Create and return the Java client (acts like a CFC proxy)
		return new OpenAPIClient(pc, openAPI, specUrl, options);
	}
}

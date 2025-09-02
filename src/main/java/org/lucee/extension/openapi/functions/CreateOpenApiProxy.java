package org.lucee.extension.openapi.functions;
import lucee.runtime.PageContext;

import lucee.loader.engine.CFMLEngine;
import lucee.loader.engine.CFMLEngineFactory;
import lucee.runtime.exp.PageException;

import org.lucee.extension.openapi.OpenAPIClientFactory;

/**
 * Lucee function implementation for createOpenApiProxy
 * Creates an OpenAPI client proxy from a specification URL
 */
public class CreateOpenApiProxy extends FunctionSupport {

	@Override
	public Object invoke(PageContext pc, Object[] args) throws PageException {
		if (args.length < 1 || args.length > 2) {
			throw exp.createExpressionException("createOpenApiProxy requires 1 or 2 arguments [specUrl, options], but [" 
				+ args.length + " ] arguments provided");
		}
		try {
			return call(pc, args);
		} catch (Exception e) {
			throw exp.createApplicationException("Failed to create OpenAPI proxy: " + e.getMessage());
		}
	}

	/**
	 * Static method that can be called by Lucee as a function
	 * @param pc PageContext
	 * @param args Function arguments: [specUrl, options]
	 * @return OpenAPI client proxy object
	 * @throws Exception if creation fails
	 */
	public static Object call(Object pc, Object[] args) throws Exception {
		CFMLEngine engine = CFMLEngineFactory.getInstance();
		if (args.length < 1) {
			throw engine.getExceptionUtil().createApplicationException(
				"createOpenApiProxy(specUrl [, options]) requires at least a specUrl argument"
			);
		}
		String specUrl = args[0].toString();
		Object options = args.length > 1 ? args[1] : null;
		OpenAPIClientFactory factory = new OpenAPIClientFactory();
		return factory.invoke(pc, specUrl, options);
	}
}

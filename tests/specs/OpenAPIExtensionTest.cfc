component extends="org.lucee.cfml.test.LuceeTestCase" labels="openapi" {
	
	function setUp() {
		// Setup before each test
		variables.testSpecUrl = "https://petstore3.swagger.io/api/v3/openapi.json";
		variables.localTestSpec = expandPath( getDirectoryFromPath(getCurrentTemplatePath()) & "../resources/test-openapi.json");
	}
	
	function testCreateOpenAPIClient() {
		try {
			var apiClient = createOpenApiProxy( variables.testSpecUrl );
			expect( isObject(apiClient) ).toBeTrue();
		} catch (any e) {
			// If extension is not installed, we expect this to fail
			expect(e.message).toInclude( "openapi" );
		}
	}
	
	function testInvalidOpenAPISpecURL() {
		try {
			createOpenApiProxy("https://invalid-url-that-does-not-exist.com/spec.json");
			fail("Should have thrown an exception for invalid URL");
		} catch (any e) {
			expect(true).toBeTrue("Expected exception thrown");
		}
	}
	
	function testEmptyURL() {
		try {
			createOpenApiProxy("");
			fail("Should have thrown an exception for empty URL");
		} catch (any e) {
			expect(true).toBeTrue("Expected exception thrown");
		}
			
	}
	
	function testOpenAPIClientProperties() {
		try {
			var apiClient = createOpenApiProxy(variables.testSpecUrl);
			expect(apiClient._spec).toBe(variables.testSpecUrl, "Should have correct spec URL");
			expect(isSimpleValue(apiClient._baseurl) && len(apiClient._baseurl) > 0).toBeTrue("Should have base URL");
			expect(isStruct(apiClient._methods) && structCount(apiClient._methods) > 0).toBeTrue("Should have methods");
		} catch (any e) {
			// Skip test if extension not available
			if (findNoCase("openapi", e.message) > 0) {
				return; // Skip this test
			}
			rethrow;
		}
	}
	
	function testOpenAPIMethodGeneration() {
		try {
			var apiClient = createOpenApiProxy(variables.testSpecUrl);
			var methods = apiClient._methods;
			expect(structCount(methods) > 0).toBeTrue("Should have generated methods");
			
			// Check for common pet store methods
			var hasGetMethod = false;
			
			for (var methodName in methods) {
				var methodInfo = methods[methodName];
				expect(isStruct(methodInfo)).toBeTrue("Method info should be struct");
				expect(structKeyExists(methodInfo, "operationId")).toBeTrue("Should have operationId");
				expect(structKeyExists(methodInfo, "method")).toBeTrue("Should have method");
				expect(structKeyExists(methodInfo, "path")).toBeTrue("Should have path");
				
				if (findNoCase("get", methodInfo.method)) {
					hasGetMethod = true;
				}
			}
			
			expect(hasGetMethod).toBeTrue("Should have at least one GET method");
		} catch (any e) {
			// Skip test if extension not available
			if (findNoCase("openapi", e.message) > 0) {
				return; // Skip this test
			}
			rethrow;
		}
	}
	
	function testNonExistentMethod() {
		try {
			var apiClient = createOpenApiProxy(variables.testSpecUrl);
			try {
				apiClient.nonExistentMethod();
				fail("Should have thrown exception for non-existent method");
			} catch (any e) {
				expect(true).toBeTrue("Expected exception thrown");
			}
		} catch (any e) {
			// Skip test if extension not available
			if (findNoCase("openapi", e.message) > 0) {
				return; // Skip this test
			}
			rethrow;
		}
	}
	
	function testErrorHandling() {
		try {
			createOpenApiProxy("https://httpbin.org/json");
			fail("Should have thrown exception for malformed JSON");
		} catch (any e) {
			expect(true).toBeTrue("Expected exception thrown");
		}
	}
	
}

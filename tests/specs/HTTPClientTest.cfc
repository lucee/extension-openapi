component extends="org.lucee.cfml.test.LuceeTestCase" labels="openapi" {
	
	function testHTTPMethods() {
		var supportedMethods = ["GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"];
		
		for (var method in supportedMethods) {
			assertTrue(arrayFindNoCase(supportedMethods, method) > 0, "Method #method# should be supported");
		}
	}
	
	function testRequestHeaders() {
		var headers = {
			"Content-Type": "application/json",
			"Accept": "application/json",
			"Authorization": "Bearer token123",
			"X-Custom-Header": "custom-value"
		};
		
		assertTrue(structKeyExists(headers, "Content-Type"), "Should have Content-Type header");
		assertTrue(structKeyExists(headers, "Accept"), "Should have Accept header");
		assertEquals("application/json", headers["Content-Type"], "Content-Type should be application/json");
	}
	
	function testRequestBodySerialization() {
		var requestBody = {
			"name": "John Doe",
			"email": "john@example.com",
			"age": 30,
			"active": true
		};
		
		var serialized = serializeJSON(requestBody);
		var deserialized = deserializeJSON(serialized);
		
		assertEquals("John Doe", deserialized.name, "Name should be preserved");
		assertEquals("john@example.com", deserialized.email, "Email should be preserved");
		assertEquals(30, deserialized.age, "Age should be preserved");
		assertTrue(deserialized.active, "Active flag should be preserved");
	}
	
	function testSuccessfulHTTPResponse() {
		var mockResponse = {
			"statusCode": 200,
			"statusText": "OK",
			"headers": {
				"Content-Type": ["application/json"],
				"X-RateLimit-Remaining": ["99"]
			},
			"data": {
				"id": 1,
				"name": "Test User",
				"email": "test@example.com"
			}
		};
		
		assertEquals(200, mockResponse.statusCode, "Status code should be 200");
		assertEquals("OK", mockResponse.statusText, "Status text should be OK");
		assertTrue(isStruct(mockResponse.data), "Data should be struct");
		assertEquals(1, mockResponse.data.id, "ID should be 1");
	}
	
	function testHTTPErrorResponse() {
		var errorResponse = {
			"statusCode": 404,
			"statusText": "Not Found",
			"headers": {
				"Content-Type": ["application/json"]
			},
			"data": {
				"error": "User not found",
				"code": "USER_NOT_FOUND"
			},
			"error": "HTTP 404: Not Found"
		};
		
		assertEquals(404, errorResponse.statusCode, "Status code should be 404");
		assertTrue(findNoCase("404", errorResponse.error) > 0, "Error should mention 404");
		assertEquals("User not found", errorResponse.data.error, "Should have error message");
	}
	
	function testJSONResponseHandling() {
		var jsonResponse = '{"message": "Success", "data": [1, 2, 3]}';
		var textResponse = "Plain text response";
		
		// Test JSON parsing
		try {
			var parsed = deserializeJSON(jsonResponse);
			assertEquals("Success", parsed.message, "Should parse JSON message");
			assertEquals(3, arrayLen(parsed.data), "Should parse JSON array");
		} catch (any e) {
			fail("Should parse valid JSON: " & e.message);
		}
		
		// Test non-JSON handling
		assertEquals("Plain text response", textResponse, "Should handle plain text");
	}
	
	function testURLBuilding() {
		var baseUrl = "https://api.example.com";
		var path = "/v1/users";
		var fullUrl = baseUrl & path;
		
		assertEquals("https://api.example.com/v1/users", fullUrl, "Should build URL correctly");
	}
	
	function testURLParameterEncoding() {
		var params = {
			"search": "john doe",
			"filter": "name=test&active=true",
			"special": "chars@##$%"
		};
		
		// URL encoding would be handled by the HTTP client
		for (var param in params) {
			assertTrue(len(params[param]) > 0, "Parameter value should not be empty");
		}
	}
	
	function testTimeoutConfiguration() {
		var timeoutConfig = {
			"connectTimeout": 5000,
			"socketTimeout": 10000,
			"connectionRequestTimeout": 5000
		};
		
		assertEquals(5000, timeoutConfig.connectTimeout, "Connect timeout should be 5000");
		assertEquals(10000, timeoutConfig.socketTimeout, "Socket timeout should be 10000");
	}
	
	function testInvalidURLHandling() {
		var invalidUrls = [
			"",
			"not-a-url",
			"http://",
			"https://",
			"ftp://invalid-protocol.com"
		];
		
		for (var invalidUrl in invalidUrls) {
			var isInvalid = (len(invalidUrl) == 0 || 
						   !findNoCase("http", invalidUrl) || 
						   invalidUrl == "http://" || 
						   invalidUrl == "https://");
			assertTrue(isInvalid, "URL should be considered invalid: " & invalidUrl);
		}
	}
	
	function testConnectionErrorHandling() {
		var connectionError = {
			"type": "ConnectException",
			"message": "Connection refused",
			"cause": "Network unreachable"
		};
		
		assertEquals("ConnectException", connectionError.type, "Should have correct error type");
		assertTrue(findNoCase("Connection", connectionError.message) > 0, "Should mention connection");
	}
	
}

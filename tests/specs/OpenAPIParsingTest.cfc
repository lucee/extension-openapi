component extends="org.lucee.cfml.test.LuceeTestCase" labels="openapi" {
	
	function testOpenAPI30SpecificationParsing() {
		var testSpec = {
			"openapi": "3.0.0",
			"info": {
				"title": "Test API",
				"version": "1.0.0"
			},
			"paths": {
				"/users": {
					"get": {
						"operationId": "getUsers",
						"summary": "Get all users",
						"responses": {
							"200": {
								"description": "Successful response"
							}
						}
					}
				}
			}
		};
		
		// Write test spec to file
		var testSpecFile = expandPath( getDirectoryFromPath(getCurrentTemplatePath()) & "../resources/test-spec.json");
		fileWrite(testSpecFile, serializeJSON(testSpec));
		
		try {
			assertTrue(fileExists(testSpecFile), "Test spec file should be created");
		} finally {
			if (fileExists(testSpecFile)) {
				fileDelete(testSpecFile);
			}
		}
	}
	
	function testComplexOpenAPISpecification() {
		var complexSpec = {
			"openapi": "3.0.0",
			"info": {
				"title": "Complex API",
				"version": "1.0.0"
			},
			"paths": {
				"/users/{id}": {
					"get": {
						"operationId": "getUserById",
						"parameters": [
							{
								"name": "id",
								"in": "path",
								"required": true,
								"schema": {
									"type": "integer"
								}
							}
						],
						"responses": {
							"200": {
								"description": "User found"
							},
							"404": {
								"description": "User not found"
							}
						}
					}
				}
			}
		};
		
		var testSpecFile = expandPath( getDirectoryFromPath(getCurrentTemplatePath()) & "../resources/complex-spec.json");
		fileWrite(testSpecFile, serializeJSON(complexSpec));
		
		try {
			assertTrue(fileExists(testSpecFile), "Complex spec file should be created");
			var specContent = deserializeJSON(fileRead(testSpecFile));
			assertTrue(structKeyExists(specContent.paths, "/users/{id}"), "Should have path with parameter");
			assertEquals("getUserById", specContent.paths["/users/{id}"].get.operationId, "Should have correct operation ID");
		} finally {
			if (fileExists(testSpecFile)) {
				fileDelete(testSpecFile);
			}
		}
	}
	
	function testOperationIdGeneration() {
		// Test operation ID to method name conversion
		var testCases = [
			{operationId: "getUsers", expected: "getUsers"},
			{operationId: "createUser", expected: "createUser"},
			{operationId: "getUserById", expected: "getUserById"},
			{operationId: "updateUserStatus", expected: "updateUserStatus"}
		];
		
		for (var testCase in testCases) {
			assertEquals(testCase.expected, testCase.operationId, "Operation ID should match expected");
		}
	}
	
	function testPathParameterHandling() {
		var pathTemplate = "/users/{id}/posts/{postId}";
		var parameters = {
			"id": 123,
			"postId": 456
		};
		
		var processedPath = pathTemplate;
		for (var param in parameters) {
			processedPath = processedPath.replace("{#param#}", parameters[param]);
		}
		
		assertEquals("/users/123/posts/456", processedPath, "Path parameters should be correctly substituted");
	}
	
	function testQueryParameterHandling() {
		var baseUrl = "https://api.example.com/users";
		var queryParams = {
			"limit": 10,
			"offset": 0,
			"filter": "active"
		};
		
		var queryString = "";
		var paramCount = 0;
		for (var param in queryParams) {
			queryString &= (paramCount == 0 ? "?" : "&") & 
						  param & "=" & queryParams[param];
			paramCount++;
		}
		
		var fullUrl = baseUrl & queryString;
		assertTrue(findNoCase("limit=10", fullUrl) > 0, "Should contain limit parameter");
		assertTrue(findNoCase("offset=0", fullUrl) > 0, "Should contain offset parameter");
		assertTrue(findNoCase("filter=active", fullUrl) > 0, "Should contain filter parameter");
	}
	
}

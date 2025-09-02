# Lucee OpenAPI Extension

A powerful extension that enables `createOpenApiProxy( url )` functionality for Lucee Server, allowing automatic REST client generation from OpenAPI specifications.

https://github.com/OAI/OpenAPI-Specification

https://github.com/swagger-api/swagger-parser/

https://luceeserver.atlassian.net/issues/?jql=labels%20%3D%20%22openAPI%22

## Features

- **Seamless Integration**: Works exactly like `createObject("webservice", wsdl)` but for REST APIs
- **OpenAPI 3.x Support**: Full support for modern OpenAPI specifications
- **Dynamic Method Generation**: Automatically creates callable methods from API operations

### Usage

```cfml
// Create OpenAPI client
apiClient = createOpenApiProxy("https://petstore3.swagger.io/api/v3/openapi.json");

// Call API methods
pet = apiClient.getPetById(petId: 1);
newPet = apiClient.addPet(body: {name: "Fluffy", status: "available"});

// Introspection
dump(apiClient._methods);  // Available methods
dump(apiClient._spec);     // Spec URL
dump(apiClient._baseurl);  // Base URL
```

## Requirements

- Java 11+
- Lucee 7.0+

## Building from Source

```bash
mvn clean package
```

The extension LEX will be created in `target/openapi-extension-1.0.0.lex`

## License

Apache License 2.0

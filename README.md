# Lucee OpenAPI Extension

A powerful extension that enables `createObject("openapi", url)` functionality for Lucee Server, allowing automatic REST client generation from OpenAPI specifications.

## Features

- **Seamless Integration**: Works exactly like `createObject("webservice", wsdl)` but for REST APIs
- **OpenAPI 3.x Support**: Full support for modern OpenAPI specifications
- **Dynamic Method Generation**: Automatically creates callable methods from API operations
- **Apache HttpClient**: Uses the same HTTP stack as Lucee core
- **Type Safety**: Automatic request/response serialization
- **Error Handling**: Comprehensive error reporting and handling

## Quick Start

### Installation

1. Build the extension:
```bash
./build.sh
```

2. Install in Lucee:
```bash
./install.sh /path/to/lucee-server
```

### Usage

```cfml
// Create OpenAPI client
apiClient = createObject("openapi", "https://petstore3.swagger.io/api/v3/openapi.json");

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
- Maven 3.6+
- Lucee 5.3+

## Building from Source

```bash
git clone [repository]
cd openapi-extension
mvn clean package
```

The extension LEX will be created in `target/openapi-extension-1.0.0.lex`

## Architecture

This extension follows the standard Lucee extension architecture:

- **OSGi Bundle**: The main Java code is packaged as an OSGi bundle JAR
- **LEX Package**: The `.lex` file contains the OSGi bundle plus runtime dependencies
- **MANIFEST.mf**: Contains both OSGi bundle metadata and Lucee extension configuration
- **createObject Integration**: Registered via `createObject` configuration in MANIFEST.mf

## License

Apache License 2.0

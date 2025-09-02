package org.lucee.extension.openapi;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for OpenAPI Extension
 */
public class OpenAPIExtensionTest {
    
    @Test
    public void testExtensionId() {
        assertEquals("B737ABC4-D43C-40F1-833F832E4E2E4D38", OpenAPIExtension.getId());
    }
    
    @Test
    public void testExtensionCreation() {
        OpenAPIExtension extension = new OpenAPIExtension();
        assertNotNull(extension);
    }
}

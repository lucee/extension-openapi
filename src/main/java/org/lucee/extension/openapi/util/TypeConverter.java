package org.lucee.extension.openapi.util;

import lucee.runtime.exp.PageException;
import lucee.runtime.type.Struct;
import lucee.runtime.type.Array;

/**
 * Utility for converting between CFML and Java types
 */
public class TypeConverter {
    
    public static Object convertToJavaType(Object cfmlValue, String targetType) throws PageException {
        if (cfmlValue == null) {
            return null;
        }
        
        switch (targetType.toLowerCase()) {
            case "string":
                return cfmlValue.toString();
            case "integer":
            case "int":
                return Integer.parseInt(cfmlValue.toString());
            case "long":
                return Long.parseLong(cfmlValue.toString());
            case "double":
                return Double.parseDouble(cfmlValue.toString());
            case "boolean":
                return Boolean.parseBoolean(cfmlValue.toString());
            default:
                return cfmlValue;
        }
    }
    
    public static Object convertToCFMLType(Object javaValue) {
        if (javaValue == null) {
            return null;
        }
        
        // Most types can be returned as-is for CFML
        return javaValue;
    }
}

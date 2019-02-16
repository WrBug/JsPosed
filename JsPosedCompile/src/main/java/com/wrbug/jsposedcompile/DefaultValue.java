package com.wrbug.jsposedcompile;

import com.squareup.javapoet.TypeName;

public class DefaultValue {

    public static String get(Class returnType) {
        String defaultValue = "0";
        if (returnType.isPrimitive()) {
            if (returnType == void.class) {
                defaultValue = "";
            } else if (returnType == boolean.class) {
                defaultValue = "false";
            }
        } else if (returnType == String.class) {
            defaultValue = "\"\"";
        } else {
            defaultValue = "null";
        }
        return defaultValue;
    }

    public static String get(TypeName returnType) {
        if (returnType == TypeName.VOID) {
            return "";
        }
        if (returnType == TypeName.BOOLEAN) {
            return "false";
        }
        if (returnType.isPrimitive()) {
            return "0";
        }
        if (returnType.toString().equals(String.class.toString())) {
            return "\"\"";
        }
        return "null";
    }
}

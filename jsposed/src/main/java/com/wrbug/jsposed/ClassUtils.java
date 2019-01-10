package com.wrbug.jsposed;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ClassUtils {

    public static Class[] toClass(ClassLoader classLoader, Object[] clzs) {
        if (clzs == null) {
            return null;
        }
        Class[] arr = new Class[clzs.length];
        for (int i = 0; i < clzs.length; i++) {
            Class c = null;
            Object clz = clzs[i];
            if (clz instanceof String) {
                c = checkIsPrimitive(clz);
                if (c == null) {
                    try {
                        c = classLoader.loadClass((String) clz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (clz instanceof Class) {
                c = (Class) clz;
            }
            arr[i] = c;
        }
        return arr;
    }

    public static Class[] toClass(Object[] clzs) {
        if (clzs == null) {
            return null;
        }
        Class[] arr = new Class[clzs.length];
        for (int i = 0; i < clzs.length; i++) {
            Class c = null;
            Object clz = clzs[i];
            if (clz instanceof String) {
                c = checkIsPrimitive(clz);
                if (c == null) {
                    try {
                        c = Class.forName((String) clz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if (clz instanceof Class) {
                c = (Class) clz;
            }
            arr[i] = c;
        }
        return arr;
    }

    private static Class checkIsPrimitive(Object o) {
        if (!(o instanceof String)) {
            return null;
        }
        String type = ((String) o).toLowerCase();
        switch (type) {
            case "int": {
                return int.class;
            }
            case "long": {
                return long.class;
            }
            case "float": {
                return float.class;
            }
            case "double": {
                return double.class;
            }
            case "char": {
                return char.class;
            }
            case "short": {
                return short.class;
            }
            case "byte": {
                return byte.class;
            }
            case "boolean": {
                return boolean.class;
            }
        }
        return null;
    }

    public static Object[] convertObject(Class[] classes, Object[] args) {
        if (classes == null || args == null) {
            return null;
        }
        if (classes.length != args.length) {
            return null;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Number) {
                args[i] = NumberUtils.convertNumber(classes[i], ((Number) args[i]));
            } else if (args[i] instanceof String && isNumberClass(classes[i])) {
                args[i] = NumberUtils.convertNumber(classes[i], ((String) args[i]));
            }
        }
        return args;
    }

    public static boolean isNumberClass(Class clazz) {
        return Number.class.isAssignableFrom(clazz) ||
                clazz == int.class ||
                clazz == long.class ||
                clazz == float.class ||
                clazz == double.class ||
                clazz == byte.class ||
                clazz == short.class;
    }

}

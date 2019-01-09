package com.wrbug.jsposed;

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


    public static Class checkIsPrimitive(Object o) {
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
}

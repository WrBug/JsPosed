package com.wrbug.jsposed;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NumberUtils {
    private static Map<String, Number> sNumberMap = new HashMap<>();

    public static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (str.equals("0")) {
            return true;
        }
        Pattern pattern = Pattern.compile("-?[1-9][0-9]*$");
        return pattern.matcher(str).find();
    }

    public static boolean isDecimal(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (str.equals("0")) {
            return true;
        }
        Pattern pattern = Pattern.compile("-?([0-9]+\\.0*)?[1-9][0-9]*$");
        return pattern.matcher(str).find();
    }

    public static boolean isHex(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("0x[0-9a-fA-F]*$");
        return pattern.matcher(str).find();
    }

    public static Number toNumber(String str) {
        if (sNumberMap.containsKey(str)) {
            return sNumberMap.get(str);
        }
        if (isHex(str)) {
            BigInteger number = new BigInteger(str.substring(2), 16);
            sNumberMap.put(str, number);
            return number;
        } else if (isDecimal(str)) {
            BigDecimal bigDecimal = new BigDecimal(str);
            sNumberMap.put(str, bigDecimal);
            return bigDecimal;
        }
        return 0;
    }

    public static int toInt(String str) {
        return toNumber(str).intValue();
    }

    public static long toLong(String str) {
        return toNumber(str).longValue();
    }

    public static float toFloat(String str) {
        return toNumber(str).floatValue();
    }

    public static double toDouble(String str) {
        return toNumber(str).doubleValue();
    }

    public static Number toNumber(Object data) {
        if (data instanceof Number) {
            return (Number) data;
        } else if (data instanceof String) {
            return toNumber((String) data);
        }
        return 0;
    }

    public static Number convertNumber(Class clazz, String data) {
        Number number = null;
        if (NumberUtils.isHex(data)) {
            number = new BigInteger(data.substring(2), 16);
        } else if (NumberUtils.isDecimal(data)) {
            number = new BigDecimal(data);
        }
        return convertNumber(clazz, number);
    }

    public static Number convertNumber(Class clazz, Number data) {
        if (clazz == null || data == null) {
            return 0;
        }
        if (clazz == Integer.class || clazz == int.class) {
            return data.intValue();
        }
        if (clazz == Long.class || clazz == long.class) {
            return data.longValue();
        }
        if (clazz == Float.class || clazz == float.class) {
            return data.floatValue();
        }
        if (clazz == Double.class || clazz == double.class) {
            return data.doubleValue();
        }
        if (clazz == Byte.class || clazz == byte.class) {
            return data.byteValue();
        }
        if (clazz == Short.class || clazz == short.class) {
            return data.shortValue();
        }
        return 0;
    }
}

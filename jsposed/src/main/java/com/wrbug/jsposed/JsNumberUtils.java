package com.wrbug.jsposed;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 *
 *
 */
public class JsNumberUtils {
    private static Map<String, Number> sNumberMap = new HashMap<>();

    public static boolean isInt(String str) {
        if (!isNumber(str)) {
            return false;
        }
        long value = toLong(str);
        return value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE;
    }

    /**
     * 1234L or 1234l
     *
     * @param str
     * @return
     */
    public static boolean isLong(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (!str.endsWith("L") && !str.endsWith("l")) {
            return false;
        }
        str = str.substring(0, str.length() - 1);
        if (!isNumber(str)) {
            return false;
        }
        double value = toDouble(str);
        return value <= Long.MAX_VALUE && value >= Long.MIN_VALUE;
    }

    /**
     * 1234F or 1234.1f
     *
     * @param str
     * @return
     */
    public static boolean isFloat(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        if (!str.endsWith("F") && !str.endsWith("f")) {
            return false;
        }
        str = str.substring(0, str.length() - 1);
        if (!isDecimal(str)) {
            return false;
        }
        double value = toDouble(str);
        return value <= Float.MAX_VALUE && value >= Float.MIN_VALUE;
    }

    public static boolean isDouble(String str) {
        return isDecimal(str);
    }

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
        } else if (isFloat(str) || isLong(str) || isDecimal(str)) {
            String num = str.replace("F", "").replace("f", "")
                    .replace("L", "").replace("l", "");
            BigDecimal bigDecimal = new BigDecimal(num);
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
        if (JsNumberUtils.isHex(data)) {
            number = new BigInteger(data.substring(2), 16);
        } else if (isFloat(data) || isLong(data) || isDecimal(data)) {
            String num = data.replace("F", "").replace("f", "")
                    .replace("L", "").replace("l", "");
            number = new BigDecimal(num);
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

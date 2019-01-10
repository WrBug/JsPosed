package com.wrbug.jsposed;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class StringUtils {
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
}

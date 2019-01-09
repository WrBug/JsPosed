package com.wrbug.jsposed;

import de.robv.android.xposed.XposedBridge;

public class LogUtils {

    public static void log(Object o) {
        XposedBridge.log(o == null ? "null" : o.toString());
    }
}

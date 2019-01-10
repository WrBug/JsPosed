package com.wrbug.jsposed;

import de.robv.android.xposed.XposedBridge;

public class LogUtils {
    private static final String DEBUG_TAG = "jsPosed.debug-->";

    public static void log(Object o) {
        if (!JsPosedConfig.debug) {
            return;
        }
        XposedBridge.log(DEBUG_TAG + String.valueOf(o));
    }
}

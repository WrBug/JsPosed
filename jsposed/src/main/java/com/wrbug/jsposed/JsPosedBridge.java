package com.wrbug.jsposed;

import org.mozilla.javascript.Function;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsPosedBridge {
    private JsPosedExecutor mJsPosedExecutor;
    private XC_LoadPackage.LoadPackageParam mParam;

    public JsPosedBridge(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        mJsPosedExecutor = jsPosedExecutor;
        mParam = param;

    }


    public void log(String msg) {
        XposedBridge.log(msg);
    }

    public void log(Throwable throwable) {
        XposedBridge.log(throwable);
    }

    public void log(String tag, String msg) {
        XposedBridge.log(tag + "--->" + msg);
    }

    public int getXposedVersion() {
        return XposedBridge.getXposedVersion();
    }


    public Set<XC_MethodHook.Unhook> hookAllMethods(String hookClass, String methodName, Function beforeCall, Function afterCall) {
        return XposedBridge.hookAllMethods(XposedHelpers.findClass(hookClass, mParam.classLoader), methodName, new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall));
    }

    public Set<XC_MethodHook.Unhook> hookAllConstructors(String hookClass, Function beforeCall, Function afterCall) {
        return XposedBridge.hookAllConstructors(XposedHelpers.findClass(hookClass, mParam.classLoader), new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall));
    }
}

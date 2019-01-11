package com.wrbug.jsposed.jscall.xposed;

import com.wrbug.jsposed.CommonMethodHook;
import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.jscall.JavaMethod;

import org.mozilla.javascript.Function;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsPosedBridge extends JavaMethod {



    @Override
    public String getName() {
        return "JsPosedBridge";
    }

    public void log(Object msg) {
        XposedBridge.log(String.valueOf(msg));
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

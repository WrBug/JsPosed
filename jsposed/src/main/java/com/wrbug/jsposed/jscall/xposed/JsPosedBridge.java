package com.wrbug.jsposed.jscall.xposed;

import com.wrbug.jsposed.ClassUtils;
import com.wrbug.jsposed.CommonMethodHook;
import com.wrbug.jsposed.jscall.JavaMethod;

import org.mozilla.javascript.Function;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

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


    /**
     * @param hookClass  support Class or String ,
     *                   eg. "java.lang.String","java.util.Map"
     * @param methodName
     * @param beforeCall XC_MethodHook#beforeHookedMethod callback
     * @param afterCall  XC_MethodHook#afterHookedMethod callback
     * @return
     */
    public Set<XC_MethodHook.Unhook> hookAllMethods(Object hookClass, String methodName, Function beforeCall, Function afterCall) {
        Class clazz = ClassUtils.toClass(hookClass, mParam.classLoader);
        return XposedBridge.hookAllMethods(clazz, methodName, new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall));
    }

    public Set<XC_MethodHook.Unhook> hookAllConstructors(Object hookClass, Function beforeCall, Function afterCall) {
        Class clazz = ClassUtils.toClass(hookClass, mParam.classLoader);
        return XposedBridge.hookAllConstructors(clazz, new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall));
    }
}

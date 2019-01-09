package com.wrbug.jsposed;

import org.mozilla.javascript.Function;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.wrbug.jsposed.LogUtils.log;

public class JsPosedHelpers {
    private JsPosedExecutor mJsPosedExecutor;
    private XC_LoadPackage.LoadPackageParam mParam;

    public JsPosedHelpers(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        mJsPosedExecutor = jsPosedExecutor;
        mParam = param;
    }

    public Class<?> findClass(String className) {
        return findClass(className, mParam.classLoader);
    }

    public Class<?> findClass(String className, ClassLoader classLoader) {
        log("findClass : " + className);
        return XposedHelpers.findClassIfExists(className, classLoader);
    }

    public Object getObjectField(Object o, String fieldName) {
        log("getObjectField : " + o + " " + fieldName);
        return XposedHelpers.getObjectField(o, fieldName);

    }

    public Object callMethod(Object o, String methodName, Object[] args) {
        log("callMethod : " + o + " " + methodName + " " + Arrays.toString(args));
        return XposedHelpers.callMethod(o, methodName, args);
    }

    public Field findField(Class<?> clazz, String fieldName) {
        log("findField : " + clazz + " " + fieldName);
        return XposedHelpers.findFieldIfExists(clazz, fieldName);
    }

    public Field findFirstFieldByExactType(Class<?> clazz, Class<?> type) {
        log("findFirstFieldByExactType : ");
        try {
            return XposedHelpers.findFirstFieldByExactType(clazz, type);
        } catch (Exception e) {

        }
        return null;
    }

    public XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String methodName, String[] argType, Function beforeCall, Function afterCall) {
        log("findAndHookMethod clazz:" + clazz + " " + methodName + " " + Arrays.toString(argType));
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        return XposedHelpers.findAndHookMethod(clazz, methodName, array);
    }

    public XC_MethodHook.Unhook findAndHookMethod(String className, String methodName, String[] argType, Function beforeCall, Function afterCall) {
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        log("findAndHookMethod className:" + className + " " + methodName + " " + Arrays.toString(argType));
        return XposedHelpers.findAndHookMethod(className, mParam.classLoader, methodName, argType, array);
    }
}

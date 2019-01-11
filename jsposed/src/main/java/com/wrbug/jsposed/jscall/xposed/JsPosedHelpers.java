package com.wrbug.jsposed.jscall.xposed;

import com.wrbug.jsposed.ArrayManager;
import com.wrbug.jsposed.ClassUtils;
import com.wrbug.jsposed.CommonMethodHook;
import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.jscall.JavaMethod;

import org.mozilla.javascript.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.wrbug.jsposed.LogUtils.log;

public class JsPosedHelpers extends JavaMethod {

    @Override
    public String getName() {
        return "JsPosedHelpers";
    }

    public Class<?> findClass(String className) {
        return findClass(className, mParam.classLoader);
    }

    public Class<?> findClass(String className, ClassLoader classLoader) {
        return XposedHelpers.findClassIfExists(className, classLoader);
    }

    public Object getObjectField(Object o, String fieldName) {
        return XposedHelpers.getObjectField(o, fieldName);

    }

    public Object newInstance(String className, Object[] args) {
        return newInstance(className, XposedHelpers.getParameterTypes(args), args);
    }

    public Object newInstance(String className, Object[] parameterTypes, Object[] args) {
        Class[] classes = ClassUtils.toClass(mParam.classLoader, parameterTypes);
        try {
            Class<?> aClass = mParam.classLoader.loadClass(className);
            Constructor<?> constructor = XposedHelpers.findConstructorBestMatch(aClass, classes);
            if (constructor == null) {
                return null;
            }
            return constructor.newInstance(ClassUtils.convertObject(classes, args));
        } catch (Exception e) {
        }
        return null;
    }

    public Object callMethod(Object o, String methodName, Object[] args) {
        return XposedHelpers.callMethod(o, methodName, args);
    }

    public Object callMethod(Object o, String methodName, Object[] classType, Object[] args) {
        Class[] classes = ClassUtils.toClass(mParam.classLoader, classType);
        args = ClassUtils.convertObject(classes, args);
        return XposedHelpers.callMethod(o, methodName, classes, args);
    }

    public Method findMethodBestMatch(Class<?> clazz, String methodName, Object[] args) {
        return XposedHelpers.findMethodBestMatch(clazz, methodName, args);
    }

    public Method findMethodBestMatch(Class<?> clazz, String methodName, Object[] parameterTypes, Object[] args) {
        Class[] classes = ClassUtils.toClass(mParam.classLoader, parameterTypes);
        return XposedHelpers.findMethodBestMatch(clazz, methodName, classes, args);
    }

    public Field findField(Class<?> clazz, String fieldName) {
        return XposedHelpers.findFieldIfExists(clazz, fieldName);
    }

    public Field findFirstFieldByExactType(Class<?> clazz, Class<?> type) {
        try {
            return XposedHelpers.findFirstFieldByExactType(clazz, type);
        } catch (Exception e) {

        }
        return null;
    }

    public XC_MethodHook.Unhook findAndHookMethod(Class<?> clazz, String methodName, Object[] argType, Function beforeCall, Function afterCall) {
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        return XposedHelpers.findAndHookMethod(clazz, methodName, array);
    }

    public XC_MethodHook.Unhook findAndHookMethod(String className, String methodName, Object[] argType, Function beforeCall, Function afterCall) {
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        return XposedHelpers.findAndHookMethod(className, mParam.classLoader, methodName, array);
    }

    public XC_MethodHook.Unhook findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object[] argType, Function beforeCall, Function afterCall) {
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        return XposedHelpers.findAndHookMethod(className, classLoader, methodName, array);
    }

    public Method findMethodExact(Class<?> clazz, String methodName, Object[] parameterTypes) {
        return XposedHelpers.findMethodExactIfExists(clazz, methodName, parameterTypes);
    }

    public Method findMethodExact(String className, ClassLoader classLoader, String methodName, Object... parameterTypes) {
        return XposedHelpers.findMethodExactIfExists(className, classLoader, methodName, parameterTypes);
    }

    public Method findMethodBestMatch(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return XposedHelpers.findMethodBestMatch(clazz, methodName, parameterTypes);
    }
}

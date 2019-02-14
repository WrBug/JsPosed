package com.wrbug.jsposed.jscall.xposed;

import com.wrbug.jsposed.ArrayManager;
import com.wrbug.jsposed.ClassUtils;
import com.wrbug.jsposed.CommonMethodHook;
import com.wrbug.jsposedannotation.JavaMethod;

import org.mozilla.javascript.Function;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class JsPosedHelpers extends JavaMethod {

    @Override
    public String getJavaMethodName() {
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

    /**
     * @param clazz     support Class or String ,
     *                  eg. "java.lang.String","java.util.Map"
     * @param fieldName
     * @return
     */
    public Object getStaticObjectField(Object clazz, String fieldName) {
        Class<?> cls = ClassUtils.toClass(clazz);
        if (cls == null) {
            return null;
        }
        return XposedHelpers.getStaticObjectField(cls, fieldName);
    }

    /**
     * @param clazz support Class or String ,
     *              eg. "java.lang.String","java.util.Map"
     * @param args
     * @return
     */
    public Object newInstance(Object clazz, Object[] args) {
        return newInstance(clazz, XposedHelpers.getParameterTypes(args), args);
    }

    /**
     * @param clazz
     * @param parameterTypes support Class or String ,
     *                       eg. "java.lang.String","java.util.Map","int"
     * @param args
     * @return
     */
    public Object newInstance(Object clazz, Object[] parameterTypes, Object[] args) {
        Class[] classes = ClassUtils.toClass(mParam.classLoader, parameterTypes);
        try {
            Class<?> aClass = ClassUtils.toClass(clazz);
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

    public Object callStaticMethod(Object clazz, String methodName, Object[] args) {
        Class<?> cls = ClassUtils.toClass(clazz);
        if (cls == null) {
            return null;
        }
        return XposedHelpers.callStaticMethod(cls, methodName, args);
    }

    public Object callMethod(Object o, String methodName, Object[] classType, Object[] args) {
        Class[] classes = ClassUtils.toClass(mParam.classLoader, classType);
        args = ClassUtils.convertObject(classes, args);
        return XposedHelpers.callMethod(o, methodName, classes, args);
    }

    public Object callStaticMethod(Object clazz, String methodName, Object[] classType, Object[] args) {
        Class<?> cls = ClassUtils.toClass(clazz);
        if (cls == null) {
            return null;
        }
        Class[] classes = ClassUtils.toClass(mParam.classLoader, classType);
        args = ClassUtils.convertObject(classes, args);
        return XposedHelpers.callStaticMethod(cls, methodName, classes, args);
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

    /**
     * @param clazz
     * @param methodName
     * @param argType
     * @param beforeCall XC_MethodHook#beforeHookedMethod callback
     * @param afterCall  XC_MethodHook#afterHookedMethod callback
     * @return
     */
    public XC_MethodHook.Unhook findAndHookMethod(Object clazz, String methodName, Object[] argType, Function beforeCall, Function afterCall) {
        Class cls = ClassUtils.toClass(clazz, mParam.classLoader);
        if (cls == null) {
            return null;
        }
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        return XposedHelpers.findAndHookMethod(cls, methodName, array);
    }


    public XC_MethodHook.Unhook findAndHookMethod(String className, ClassLoader classLoader, String methodName, Object[] argType, Function beforeCall, Function afterCall) {
        Object[] array = ArrayManager.getInstance().addArray(argType).add(new CommonMethodHook(mJsPosedExecutor, beforeCall, afterCall)).toArray();
        return XposedHelpers.findAndHookMethod(className, classLoader, methodName, array);
    }

    public Method findMethodExact(Class<?> clazz, String methodName, Object[] parameterTypes) {
        return XposedHelpers.findMethodExactIfExists(clazz, methodName, parameterTypes);
    }

    public Method findMethodExact(String className, ClassLoader classLoader, String methodName, Object[] parameterTypes) {
        return XposedHelpers.findMethodExactIfExists(className, classLoader, methodName, parameterTypes);
    }

    public Method findMethodBestMatch(Class<?> clazz, String methodName, Class<?>[] parameterTypes) {
        return XposedHelpers.findMethodBestMatch(clazz, methodName, parameterTypes);
    }
}

package com.wrbug.jsposed.jscall.xposed;

import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.wrbug.jsposedannotation.JavaMethod;

import de.robv.android.xposed.XposedHelpers;

public class Env extends JavaMethod {


    @Override
    public String getJavaMethodName() {
        return "Env";
    }

    public String packageName() {
        return mParam.packageName;
    }

    public String processName() {
        return mParam.processName;
    }

    public ClassLoader classLoader() {
        return mParam.classLoader;
    }

    public ApplicationInfo appInfo() {
        return mParam.appInfo;
    }

    public boolean isFirstApplication() {
        return mParam.isFirstApplication;
    }

    public int sdkInit() {
        return Build.VERSION.SDK_INT;
    }

    public Object buildVersion(String fieldName) {
        return XposedHelpers.getStaticObjectField(Build.VERSION.class, fieldName);
    }

    public Object field(String fieldName) {
        return XposedHelpers.getObjectField(mParam, fieldName);
    }
}

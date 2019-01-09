package com.wrbug.jsposed;

import android.content.pm.ApplicationInfo;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Env {
    private JsPosedExecutor mJsPosedExecutor;
    private XC_LoadPackage.LoadPackageParam mParam;

    public Env(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        mJsPosedExecutor = jsPosedExecutor;
        mParam = param;
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

    public Object field(String fieldName) {
        return XposedHelpers.getObjectField(mParam, fieldName);
    }
}

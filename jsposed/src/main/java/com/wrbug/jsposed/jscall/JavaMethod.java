package com.wrbug.jsposed.jscall;

import com.wrbug.jsposed.JsPosedExecutor;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class JavaMethod {
    protected JsPosedExecutor mJsPosedExecutor;
    protected XC_LoadPackage.LoadPackageParam mParam;

    public JavaMethod(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        mJsPosedExecutor = jsPosedExecutor;
        mParam = param;
    }

    public abstract String getName();
}

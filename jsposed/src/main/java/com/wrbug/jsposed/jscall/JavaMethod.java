package com.wrbug.jsposed.jscall;

import com.wrbug.jsposed.JsPosedExecutor;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class JavaMethod {
    protected JsPosedExecutor mJsPosedExecutor;
    protected XC_LoadPackage.LoadPackageParam mParam;

    public void setJsPosedExecutor(JsPosedExecutor jsPosedExecutor) {
        mJsPosedExecutor = jsPosedExecutor;
    }

    public void setParam(XC_LoadPackage.LoadPackageParam param) {
        mParam = param;
    }

    public abstract String getName();
}

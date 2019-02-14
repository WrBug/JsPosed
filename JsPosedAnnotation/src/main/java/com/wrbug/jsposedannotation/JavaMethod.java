package com.wrbug.jsposedannotation;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class JavaMethod {
    protected Executable mJsPosedExecutor;
    protected XC_LoadPackage.LoadPackageParam mParam;

    public void setJsPosedExecutor(Executable jsPosedExecutor) {
        mJsPosedExecutor = jsPosedExecutor;
    }

    public void setParam(XC_LoadPackage.LoadPackageParam param) {
        mParam = param;
    }

    public String getJavaMethodName() {
        return getClass().getSimpleName();
    }


    @Override
    public String toString() {
        return "name=" + getJavaMethodName() + "," + getClass().getName();

    }
}

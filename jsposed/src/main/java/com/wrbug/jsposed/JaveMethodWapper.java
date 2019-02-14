package com.wrbug.jsposed;

import com.wrbug.jsposedannotation.Executable;
import com.wrbug.jsposedannotation.JavaMethod;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JaveMethodWapper extends JavaMethod {
    private Class clazz;
    private Object mBase;

    public JaveMethodWapper(Object base) {
        mBase = base;
        clazz = base.getClass();
    }


}

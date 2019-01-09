package com.wrbug.jsposed;

import org.mozilla.javascript.Function;

import de.robv.android.xposed.XC_MethodHook;

import static com.wrbug.jsposed.LogUtils.log;

public class CommonMethodHook extends XC_MethodHook {
    private JsPosedExecutor mJsPosedExecutor;
    private Function beforeCall;
    private Function afterCall;

    public CommonMethodHook(JsPosedExecutor jsPosedExecutor, Function beforeCall, Function afterCall) {
        mJsPosedExecutor = jsPosedExecutor;
        this.beforeCall = beforeCall;
        this.afterCall = afterCall;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        log("beforeHookedMethod" + param.method);
        mJsPosedExecutor.call(beforeCall, param);
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        log("afterHookedMethod" + param.method);
        mJsPosedExecutor.call(afterCall, param);
        super.afterHookedMethod(param);
    }
}

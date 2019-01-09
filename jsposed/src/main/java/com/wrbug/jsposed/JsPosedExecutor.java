package com.wrbug.jsposed;

import android.text.TextUtils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsPosedExecutor {
    private String js;
    private XC_LoadPackage.LoadPackageParam mParam;
    private volatile static JsPosedExecutor instance;
    private Context mContext;
    private Scriptable scope;


    public static JsPosedExecutor init(XC_LoadPackage.LoadPackageParam param, String jsContent) {
        if (instance == null) {
            synchronized (JsPosedExecutor.class) {
                if (instance == null) {
                    instance = new JsPosedExecutor(param, jsContent);
                }
            }
        }
        instance.checkJsChanged(jsContent);
        return instance;
    }

    private JsPosedExecutor(XC_LoadPackage.LoadPackageParam param, String js) {
        this.mParam = param;
        this.js = js;
        mContext = Context.enter();
        mContext.setOptimizationLevel(-1);
        scope = mContext.initSafeStandardObjects();
        ScriptableObject.putProperty(scope, "JsPosedBridge", Context.javaToJS(new JsPosedBridge(this, param), scope));
        ScriptableObject.putProperty(scope, "JsPosedHelpers", Context.javaToJS(new JsPosedHelpers(this, param), scope));
        ScriptableObject.putProperty(scope, "Env", Context.javaToJS(new Env(this, param), scope));
        run(js);
    }


    private void checkJsChanged(String jsContent) {
        if (TextUtils.equals(js, jsContent)) {
            return;
        }
        js = jsContent;
        run(js);
    }

    public void run(String js) {
        try {
            mContext.evaluateString(scope, js, mParam.packageName, 1, null);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    public void call(Function call, Object... args) {
        try {
            if (call == null) {
                return;
            }
            call.call(mContext, scope, scope, args);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }
}

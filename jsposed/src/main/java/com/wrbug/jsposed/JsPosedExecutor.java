package com.wrbug.jsposed;

import android.text.TextUtils;

import com.wrbug.jsposed.jscall.map.JsMap;
import com.wrbug.jsposed.jscall.view.JsCompoundButton;
import com.wrbug.jsposed.jscall.view.JsContext;
import com.wrbug.jsposed.jscall.view.JsViewGroup;
import com.wrbug.jsposed.jscall.xposed.Env;
import com.wrbug.jsposed.jscall.JavaMethod;
import com.wrbug.jsposed.jscall.xposed.JsPosedBridge;
import com.wrbug.jsposed.jscall.xposed.JsPosedHelpers;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsPosedExecutor {
    private String js;
    private XC_LoadPackage.LoadPackageParam mParam;
    private volatile static JsPosedExecutor instance;
    private Context mContext;
    private Scriptable scope;


    public static JsPosedExecutor init(XC_LoadPackage.LoadPackageParam param, String jsContent) {
        return init(param, jsContent, false);
    }

    public static JsPosedExecutor init(XC_LoadPackage.LoadPackageParam param, String jsContent, boolean debug) {
        JsPosedConfig.debug = debug;
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
        initCallLog();
        this.mParam = param;
        this.js = js;
        mContext = Context.enter();
        mContext.setOptimizationLevel(-1);
        scope = mContext.initSafeStandardObjects();
        addJavaMethod(new JsPosedBridge(this, param));
        addJavaMethod(new JsPosedHelpers(this, param));
        addJavaMethod(new Env(this, param));
        addJavaMethod(new JsCompoundButton(this, param));
        addJavaMethod(new JsViewGroup(this, param));
        addJavaMethod(new JsContext(this, param));
        addJavaMethod(new JsMap(this, param));
        run(js);
    }

    private void initCallLog() {
        if (!JsPosedConfig.debug) {
            return;
        }
        XposedBridge.hookAllConstructors(JavaMethod.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                Class<?> objectClass = param.thisObject.getClass();
                List<String> list = new ArrayList<>();
                Method[] methods = objectClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (!list.contains(method.getName())) {
                        list.add(method.getName());
                    }
                }
                methods = objectClass.getMethods();
                for (Method method : methods) {
                    if (!list.contains(method.getName())) {
                        list.add(method.getName());
                    }
                }
                for (String s : list) {
                    XposedBridge.hookAllMethods(objectClass, s, mXC_methodHook);
                }
            }
        });
    }

    private XC_MethodHook mXC_methodHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            LogUtils.log(param.thisObject.getClass().getName() + "#" + param.method.getName() + " before called,args :" + Arrays.toString(param.args));
            super.beforeHookedMethod(param);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            LogUtils.log(param.thisObject.getClass().getName() + "#" + param.method.getName() + " after called,result :" + param.getResult());
            super.afterHookedMethod(param);
        }
    };

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

    public void addJavaMethod(JavaMethod javaMethod) {
        if (javaMethod == null) {
            return;
        }
        ScriptableObject.putProperty(scope, javaMethod.getName(), Context.javaToJS(javaMethod, scope));

    }
}

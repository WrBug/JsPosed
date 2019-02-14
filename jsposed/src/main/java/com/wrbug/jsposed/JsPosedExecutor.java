package com.wrbug.jsposed;

import android.text.TextUtils;

import com.wrbug.jsposed.jscall.context.JsActivity;
import com.wrbug.jsposed.jscall.map.JsMap;
import com.wrbug.jsposed.jscall.view.JsCompoundButton;
import com.wrbug.jsposed.jscall.view.JsViewGroup;
import com.wrbug.jsposed.jscall.xposed.Env;
import com.wrbug.jsposedannotation.Constant;
import com.wrbug.jsposedannotation.JavaMethod;
import com.wrbug.jsposed.jscall.xposed.JsPosedBridge;
import com.wrbug.jsposed.jscall.xposed.JsPosedHelpers;
import com.wrbug.jsposedannotation.Executable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsPosedExecutor implements Executable {
    private String js;
    private XC_LoadPackage.LoadPackageParam mParam;
    private volatile static JsPosedExecutor instance;
    private Context mContext;
    private Scriptable scope;
    private List<String> javaMethods = new ArrayList<>();

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
        loadJavaClass();
        addJavaMethod(new JsPosedBridge());
        addJavaMethod(new JsPosedHelpers());
        addJavaMethod(new Env());
        addJavaMethod(new JsCompoundButton());
        addJavaMethod(new JsViewGroup());
        addJavaMethod(new JsActivity());
        addJavaMethod(new JsMap());
        run(js);
    }

    private void loadJavaClass() {
        Class<?> buildConfigClass = XposedHelpers.findClassIfExists(mParam.packageName + ".BuildConfig", mParam.classLoader);
        if (buildConfigClass == null) {
            return;
        }
        Field field = null;
        try {
            field = buildConfigClass.getDeclaredField("MODULES_NAME");
        } catch (NoSuchFieldException e) {
        }
        if (field == null) {
            return;
        }
        String modulesName = (String) XposedHelpers.getStaticObjectField(buildConfigClass, "MODULES_NAME");
        XposedBridge.log("modulesName=" + modulesName);
        if (TextUtils.isEmpty(modulesName)) {
            return;
        }
        String[] modulesNames = modulesName.split(",");
        if (modulesNames.length > 0) {
            for (String name : modulesNames) {
                String className = Constant.BUILD_PACKAGE + "." + name + "." + Constant.JAVA_METHOD_ARRAY_CLASS_NAME;
                Class javaMethodMapClass = XposedHelpers.findClassIfExists(className, mParam.classLoader);
                if (javaMethodMapClass == null) {
                    XposedBridge.log("className:" + className + " is null");
                    continue;
                }
                String[] array = (String[]) XposedHelpers.callStaticMethod(javaMethodMapClass, Constant.JAVA_METHOD_ARRAY_GET_METHOD_NAME);
                XposedBridge.log("map:" + Arrays.toString(array));
                if (array == null || array.length == 0) {
                    continue;
                }
                for (String cName : array) {
                    JavaMethod value = null;
                    try {
                        value = (JavaMethod) Class.forName(cName).newInstance();
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                    addJavaMethod(value, false);
                }
            }
        }
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

    @Override
    public void run(String js) {
        try {
            mContext.evaluateString(scope, js, mParam.packageName, 1, null);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    @Override
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
        addJavaMethod(javaMethod, true);
    }

    public void addJavaMethod(JavaMethod javaMethod, boolean addSuperClass) {
        if (javaMethod == null) {
            return;
        }
        if (javaMethods.contains(javaMethod.getJavaMethodName())) {
            return;
        }
        XposedBridge.log("addJavaMethod:" + javaMethod);
        javaMethod.setJsPosedExecutor(this);
        javaMethod.setParam(mParam);
        ScriptableObject.putProperty(scope, javaMethod.getJavaMethodName(), Context.javaToJS(javaMethod, scope));
        javaMethods.add(javaMethod.getJavaMethodName());
        if (!addSuperClass) {
            return;
        }
        Class<?> superclass = javaMethod.getClass().getSuperclass();
        if (superclass != null && JavaMethod.class.isAssignableFrom(superclass) && JavaMethod.class != superclass && !javaMethods.contains(superclass)) {
            try {
                Constructor<?> constructor = XposedHelpers.findConstructorBestMatch(superclass);
                if (constructor != null) {
                    JavaMethod parentInstance = (JavaMethod) constructor.newInstance();
                    addJavaMethod(parentInstance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

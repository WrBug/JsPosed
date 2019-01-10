package com.wrbug.jsposed.jscall.map;

import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.jscall.JavaMethod;

import java.util.Map;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsMap extends JavaMethod {
    public JsMap(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        super(jsPosedExecutor, param);
    }

    @Override
    public String getName() {
        return "JsMap";
    }


    public Object put(Map map, Object key, Object value) {
        return map.put(key, value);
    }

    public Object get(Map map, Object key) {
        return map.get(key);
    }

    public boolean containKey(Map map, Object key) {
        return map.containsKey(key);
    }
}

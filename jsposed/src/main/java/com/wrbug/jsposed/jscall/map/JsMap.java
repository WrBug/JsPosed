package com.wrbug.jsposed.jscall.map;

import com.wrbug.jsposed.jscall.JavaMethod;
import java.util.Map;
public class JsMap extends JavaMethod {

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

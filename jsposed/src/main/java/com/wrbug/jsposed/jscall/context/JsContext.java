package com.wrbug.jsposed.jscall.context;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

import com.wrbug.jsposed.ClassUtils;
import com.wrbug.jsposed.JsPosedReceiver;
import com.wrbug.jsposedannotation.JavaMethod;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class JsContext extends JavaMethod {
    private static Map<String, SoftReference<JsPosedReceiver>> receiverMap = new HashMap<>();

    @Override
    public String getJavaMethodName() {
        return "JsContext";
    }

    public Context getApplicationContext(Context context) {
        return context.getApplicationContext();
    }

    public void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void startActivity(Context context, String targetActivity, Map<String, Object> bundle) {
        Intent intent = new Intent();
        intent.setClassName(context, targetActivity);
        Bundle b = new Bundle();
        for (Map.Entry<String, Object> entry : bundle.entrySet()) {
            String key = entry.getKey();
            Object entryValue = entry.getValue();
            if (entryValue == null) {
                continue;
            }
            if (entryValue instanceof NativeArray) {
                NativeArray arr = (NativeArray) entryValue;
                if (arr.size() == 0 || arr.size() > 2) {
                    continue;
                }
                Object value = arr.get(0);
                if (value == null) {
                    continue;
                }
                if (arr.size() == 1) {
                    if (value instanceof String) {
                        b.putString(key, ((String) value));
                    } else if (value instanceof Number) {
                        b.putDouble(key, ((Number) value).doubleValue());
                    } else if (value instanceof Boolean) {
                        b.putBoolean(key, (Boolean) value);
                    }
                } else {
                    Object type = arr.get(1);
                    Class clazz = ClassUtils.toClass(type);
                    putExtra(b, key, value, clazz);
                }
            } else if (entryValue instanceof String) {
                b.putString(key, ((String) entryValue));
            } else if (entryValue instanceof Number) {
                b.putDouble(key, ((double) entryValue));
            } else if (entryValue instanceof Boolean) {
                b.putBoolean(key, ((boolean) entryValue));
            }

        }
        intent.putExtras(b);
        context.startActivity(intent);
    }

    public void registerReceiver(Context context, String key, String[] intentFilter, Function function) {
        if (intentFilter == null || intentFilter.length == 0) {
            return;
        }
        if (receiverMap.containsKey(key)) {
            return;
        }
        JsPosedReceiver receiver = new JsPosedReceiver(new JsPosedReceiver.Callback(function) {

            @Override
            public void onReceive(Context context, Intent intent) {
                mJsPosedExecutor.call(mFunction, context, intent);
            }
        });
        IntentFilter filter = new IntentFilter();
        for (String s : intentFilter) {
            filter.addAction(s);
        }
        context.registerReceiver(receiver, filter);
        receiverMap.put(key, new SoftReference<>(receiver));
    }

    public void registerReceiver(Context context, String key) {
        if (!receiverMap.containsKey(key)) {
            return;
        }
        SoftReference<JsPosedReceiver> reference = receiverMap.get(key);
        receiverMap.remove(key);
        if (reference == null) {
            return;
        }
        JsPosedReceiver receiver = reference.get();
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            reference.clear();
        }
    }


    public void putExtra(Bundle bundle, String key, Object value, Class clazz) {
        if (clazz == String.class) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Number) {
            Number num = (Number) value;
            if (clazz == int.class || clazz == Integer.class) {
                bundle.putInt(key, num.intValue());
            } else if (clazz == long.class || clazz == Long.class) {
                bundle.putLong(key, num.longValue());
            } else if (clazz == float.class || clazz == Float.class) {
                bundle.putFloat(key, num.floatValue());
            } else if (clazz == double.class || clazz == Double.class) {
                bundle.putDouble(key, num.doubleValue());
            } else if (clazz == short.class || clazz == Short.class) {
                bundle.putShort(key, num.shortValue());
            } else if (clazz == byte.class || clazz == Byte.class) {
                bundle.putByte(key, num.byteValue());
            }
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (clazz == char.class || clazz == Character.class) {
            bundle.putChar(key, (char) value);
        }
    }

}

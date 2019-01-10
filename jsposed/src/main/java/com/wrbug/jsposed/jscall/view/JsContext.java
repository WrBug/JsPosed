package com.wrbug.jsposed.jscall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.JsNumberUtils;
import com.wrbug.jsposed.jscall.JavaMethod;

import java.util.Map;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsContext extends JavaMethod {
    public JsContext(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        super(jsPosedExecutor, param);
    }

    @Override
    public String getName() {
        return "JsContext";
    }

    public Context getApplicationContext(Context context) {
        return context.getApplicationContext();
    }

    public void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public View findViewById(Activity activity, Object id) {
        return activity.findViewById(JsNumberUtils.toNumber(id).intValue());
    }

    public void startActivity(Context context, String targetActivity, Map<String, Object> bundle) {
        Intent intent = new Intent();
        intent.setClassName(context, targetActivity);
        Bundle b = new Bundle();
        for (Map.Entry<String, Object> entry : bundle.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                b.putString(key, ((String) value));
                if (JsNumberUtils.isLong((String) value)) {
                    b.putLong(key, JsNumberUtils.toLong((String) value));
                } else if (JsNumberUtils.isInt((String) value)) {
                    b.putInt(key, JsNumberUtils.toInt((String) value));
                } else if (JsNumberUtils.isFloat((String) value)) {
                    b.putFloat(key, JsNumberUtils.toFloat((String) value));
                } else if (JsNumberUtils.isDouble((String) value)) {
                    b.putDouble(key, JsNumberUtils.toDouble((String) value));
                }
            } else if (value instanceof Boolean) {
                b.putBoolean(key, (Boolean) value);
            } else if (value instanceof Number) {
                b.putDouble(key, ((Number) value).doubleValue());
            }
        }
        intent.putExtras(b);
        context.startActivity(intent);
    }

    public Object getExtra(Activity activity, String key) {
        Intent intent = activity.getIntent();
        if (intent == null) {
            return null;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }

        return extras.get(key);
    }
}

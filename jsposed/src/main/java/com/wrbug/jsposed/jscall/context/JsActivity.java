package com.wrbug.jsposed.jscall.context;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JsActivity extends JsContext {
    @Override
    public String getJavaMethodName() {
        return "JsActivity";
    }

    public View findViewById(Activity activity, long id) {
        return activity.findViewById((int) id);
    }

    public View findViewById(Activity activity, String idName) {
        return activity.findViewById(activity.getResources().getIdentifier(idName, "id", activity.getPackageName()));
    }

    public Object getExtras(Activity activity, String key) {
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

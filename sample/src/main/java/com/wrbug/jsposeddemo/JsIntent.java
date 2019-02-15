package com.wrbug.jsposeddemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.wrbug.jsposed.jclass.build.sample.JsIntent_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(value = Intent.class, name = "JsIntent")
public class JsIntent extends JsIntent_ {
    public JsIntent(String arg0, Uri arg1, Context arg2, Class arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public JsIntent(Context arg0, Class arg1) {
        super(arg0, arg1);
    }

    public JsIntent(String arg0, Uri arg1) {
        super(arg0, arg1);
    }

    public JsIntent() {
    }

    public JsIntent(Intent arg0) {
        super(arg0);
    }

    public JsIntent(String arg0) {
        super(arg0);
    }
}

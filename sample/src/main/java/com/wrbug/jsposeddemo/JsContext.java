package com.wrbug.jsposeddemo;

import android.content.Context;

import com.wrbug.jsposed.jclass.build.sample.JsContext_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(Context.class)
public class JsContext extends JsContext_ {
    public JsContext() {
    }
}

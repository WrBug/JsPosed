package com.wrbug.jsposed.jscall;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.wrbug.jsposed.jclass.build.jsposed.JsBundle_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(Bundle.class)
public class JsBundle extends JsBundle_ {
    public JsBundle(PersistableBundle arg0) {
        super(arg0);
    }

    public JsBundle(Bundle arg0) {
        super(arg0);
    }

    public JsBundle(int arg0) {
        super(arg0);
    }

    public JsBundle(ClassLoader arg0) {
        super(arg0);
    }

    public JsBundle() {
    }
}

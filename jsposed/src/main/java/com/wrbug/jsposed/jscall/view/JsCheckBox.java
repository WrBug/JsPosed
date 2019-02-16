package com.wrbug.jsposed.jscall.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.wrbug.jsposed.jclass.build.jsposed.JsCheckBox_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(CheckBox.class)
public class JsCheckBox extends JsCheckBox_ {
    public JsCheckBox(Context arg0, AttributeSet arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public JsCheckBox(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public JsCheckBox(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public JsCheckBox(Context arg0) {
        super(arg0);
    }

    public JsCheckBox() {
    }

}

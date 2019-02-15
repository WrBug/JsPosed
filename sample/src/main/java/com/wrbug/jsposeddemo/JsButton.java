package com.wrbug.jsposeddemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.wrbug.jsposed.jclass.build.sample.JsButton_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(Button.class)
public class JsButton extends JsButton_ {
    public JsButton(Context arg0, AttributeSet arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public JsButton(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public JsButton(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public JsButton(Context arg0) {
        super(arg0);
    }

    public JsButton() {
    }
}

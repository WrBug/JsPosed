package com.wrbug.jsposed.jscall.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.wrbug.jsposed.jclass.build.jsposed.JsView_;
import com.wrbug.jsposedannotation.JavaClass;


@JavaClass(View.class)
public class JsView extends JsView_ {
    public JsView(Context arg0, AttributeSet arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public JsView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public JsView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public JsView(Context arg0) {
        super(arg0);
    }

    public JsView() {
    }
}

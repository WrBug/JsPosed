package com.wrbug.jsposeddemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wrbug.jsposed.jclass.build.sample.JsTextView_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(TextView.class)
public class JsTextView extends JsTextView_ {
    public JsTextView(Context arg0, AttributeSet arg1, int arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public JsTextView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public JsTextView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public JsTextView(Context arg0) {
        super(arg0);
    }

    public JsTextView() {
    }
}

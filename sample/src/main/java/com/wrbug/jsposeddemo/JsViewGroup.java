package com.wrbug.jsposeddemo;

import android.view.ViewGroup;

import com.wrbug.jsposed.jclass.build.sample.JsViewGroup_;
import com.wrbug.jsposedannotation.JavaClass;

@JavaClass(ViewGroup.class)
public class JsViewGroup extends JsViewGroup_ {
    public JsViewGroup() {
    }
}

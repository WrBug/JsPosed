package com.wrbug.jsposed.jscall.view;

import android.widget.CompoundButton;

import com.wrbug.jsposed.JsPosedExecutor;

import org.mozilla.javascript.Function;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsCompoundButton extends JsTextView {
    public JsCompoundButton(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        super(jsPosedExecutor, param);
    }

    public void toggle(CompoundButton button) {
        button.toggle();
    }

    public boolean isChecked(CompoundButton button) {
        return button.isChecked();
    }

    public void setChecked(CompoundButton button, boolean checked) {
        button.setChecked(checked);
    }

    public void setOnCheckedChangeListener(CompoundButton button, Function function) {
        button.setOnCheckedChangeListener((buttonView, isChecked) -> mJsPosedExecutor.call(function, buttonView, isChecked));
    }


}

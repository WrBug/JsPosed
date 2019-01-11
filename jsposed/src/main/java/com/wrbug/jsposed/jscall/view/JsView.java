package com.wrbug.jsposed.jscall.view;

import android.view.View;

import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.JsNumberUtils;
import com.wrbug.jsposed.jscall.JavaMethod;

import org.mozilla.javascript.Function;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsView extends JavaMethod {

    @Override
    public String getName() {
        return "JsView";
    }

    public void setOnclickListener(View view, final Function function) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJsPosedExecutor.call(function, v);
            }
        });
    }

    public void setOnFocusChangeListener(View view, final Function function) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mJsPosedExecutor.call(function, v, hasFocus);
            }
        });
    }

    public void setOnLongClickListener(View view, final Function function) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mJsPosedExecutor.call(function, v);
                return false;
            }
        });
    }

    public boolean performLongClick(View view) {
        return view.performLongClick();
    }

    public void setVisibility(View view, long visibility) {
        view.setVisibility((int) visibility);
    }

    public boolean isEnabled(View view) {
        return view.isEnabled();
    }

    public void setEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
    }

    public void setFocusable(View view, boolean focusable) {
        view.setFocusable(focusable);
    }

    public void setPadding(View view, Number left, Number top, Number right, Number bottom) {
        view.setPadding(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
    }

}

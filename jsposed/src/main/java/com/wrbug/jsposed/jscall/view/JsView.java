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

    public boolean isEnabled(View view) {
        return view.isEnabled();
    }

    public void setEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
    }


    public boolean isSelected(View view) {
        return view.isSelected();
    }

    public void setSelected(View view, boolean selected) {
        view.setSelected(selected);
    }

    public boolean isFocusable(View view) {
        return view.isFocusable();
    }


    public void setFocusable(View view, boolean focusable) {
        view.setFocusable(focusable);
    }


    public int getVisibility(View view) {
        return view.getVisibility();
    }

    public void setVisibility(View view, long visibility) {
        view.setVisibility((int) visibility);
    }


    public void setTag(View view, Object tag) {
        view.setTag(tag);
    }

    public Object getTag(View view) {
        return view.getTag();
    }

    public void setTag(View view, long key, Object tag) {
        view.setTag((int) key, tag);
    }

    public void getTag(View view, long key) {
        view.getTag((int) key);
    }

    public void setPadding(View view, long left, long top, long right, long bottom) {
        view.setPadding(((int) left), ((int) top), ((int) right), (int) bottom);
    }

    public boolean performClick(View view) {
        return view.performClick();
    }

    public boolean performLongClick(View view) {

        return view.performLongClick();
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
}

package com.wrbug.jsposed.jscall.view;

import android.text.Html;
import android.widget.TextView;

import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.JsNumberUtils;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsTextView extends JsView {
    public JsTextView(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        super(jsPosedExecutor, param);
    }

    public String getText(TextView textView) {
        return textView.getText().toString();
    }

    public void setText(TextView textView, String text, boolean isHtml) {
        textView.setText(isHtml ? Html.fromHtml(text) : text);
    }

    public void setText(TextView textView, String text) {
        setText(textView, text, false);
    }

    public void setText(TextView textView, Object id) {
        textView.setText(JsNumberUtils.toNumber(id).intValue());
    }

    public int length(TextView textView) {
        return textView.length();
    }

    public float getTextSize(TextView textView) {
        return textView.getTextSize();
    }

    public void setTextSize(TextView textView, Object size) {
        textView.setTextSize(JsNumberUtils.toNumber(size).floatValue());
    }

    public void setTextColor(TextView textView, Object color) {
        textView.setTextColor(JsNumberUtils.toNumber(color).intValue());
    }

    public final void setHint(TextView textView, Object resid) {
        textView.setHint(JsNumberUtils.toNumber(resid).intValue());
    }

    public final void setHint(TextView textView, String hint) {
        textView.setHint(hint);
    }

    public String getHint(TextView textView) {
        return textView.getHint().toString();
    }

    public void setInputType(TextView textView, Object type) {
        textView.setInputType(JsNumberUtils.toNumber(type).intValue());
    }
}

package com.wrbug.jsposed.jscall.view;

import android.text.Html;
import android.widget.TextView;

public class JsTextView extends JsView {

    @Override
    public String getName() {
        return "JsTextView";
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

    public void setText(TextView textView, long id) {
        textView.setText(((int) id));
    }

    public int length(TextView textView) {
        return textView.length();
    }

    public float getTextSize(TextView textView) {
        return textView.getTextSize();
    }

    public void setTextSize(TextView textView, float size) {
        textView.setTextSize(size);
    }

    public void setTextColor(TextView textView, long color) {
        textView.setTextColor(((int) color));
    }

    public final void setHint(TextView textView, long resid) {
        textView.setHint((int) resid);
    }

    public final void setHint(TextView textView, String hint) {
        textView.setHint(hint);
    }

    public String getHint(TextView textView) {
        return textView.getHint().toString();
    }

    public void setInputType(TextView textView, long type) {
        textView.setInputType((int) type);
    }
}

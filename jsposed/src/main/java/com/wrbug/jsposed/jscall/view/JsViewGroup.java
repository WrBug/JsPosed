package com.wrbug.jsposed.jscall.view;

import android.view.View;
import android.view.ViewGroup;

import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.JsNumberUtils;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class JsViewGroup extends JsView {
    public JsViewGroup(JsPosedExecutor jsPosedExecutor, XC_LoadPackage.LoadPackageParam param) {
        super(jsPosedExecutor, param);
    }

    @Override
    public String getName() {
        return "JsViewGroup";
    }

    public void addView(ViewGroup viewGroup, View child) {
        viewGroup.addView(child);
    }

    public void addView(ViewGroup viewGroup, View child, long index) {
        viewGroup.addView(child, (int) index);

    }

    public void addView(ViewGroup viewGroup, View child, long width, long height) {
        viewGroup.addView(child, ((int) width), (int) height);
    }

    public void addView(ViewGroup viewGroup, View child, ViewGroup.LayoutParams params) {
        viewGroup.addView(child, params);
    }

    public void addView(ViewGroup viewGroup, View child, long index, ViewGroup.LayoutParams params) {
        viewGroup.addView(child, (int) index, params);
    }

    public void removeView(ViewGroup viewGroup, View view) {
        viewGroup.removeView(view);
    }

    public void removeViewInLayout(ViewGroup viewGroup, View view) {
        viewGroup.removeViewInLayout(view);
    }

    public void removeViewsInLayout(ViewGroup viewGroup, long start, long count) {
        viewGroup.removeViewsInLayout((int) start, ((int) count));
    }

    public void removeViewAt(ViewGroup viewGroup, long index) {
        viewGroup.removeViewAt((int) index);
    }

    public void removeViews(ViewGroup viewGroup, long start, long count) {
        viewGroup.removeViews(((int) start), (int) count);
    }

    public void removeAllViews(ViewGroup viewGroup) {
        viewGroup.removeAllViews();
    }

    public int getChildCount(ViewGroup viewGroup) {
        return viewGroup.getChildCount();
    }

    public View getChildAt(ViewGroup viewGroup, long index) {
        return viewGroup.getChildAt((int) index);

    }
}

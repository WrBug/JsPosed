package com.wrbug.jsposed.jscall.view;

import android.view.View;
import android.view.ViewGroup;

import com.wrbug.jsposed.JsPosedExecutor;
import com.wrbug.jsposed.NumberUtils;

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

    public void addView(ViewGroup viewGroup, View child, Object index) {
        viewGroup.addView(child, NumberUtils.toNumber(index).intValue());

    }

    public void addView(ViewGroup viewGroup, View child, Object width, Object height) {
        viewGroup.addView(child, NumberUtils.toNumber(width).intValue(), NumberUtils.toNumber(height).intValue());
    }

    public void addView(ViewGroup viewGroup, View child, ViewGroup.LayoutParams params) {
        viewGroup.addView(child, params);
    }

    public void addView(ViewGroup viewGroup, View child, Object index, ViewGroup.LayoutParams params) {
        viewGroup.addView(child, NumberUtils.toNumber(index).intValue(), params);
    }

    public void removeView(ViewGroup viewGroup, View view) {
        viewGroup.removeView(view);
    }

    public void removeViewInLayout(ViewGroup viewGroup, View view) {
        viewGroup.removeViewInLayout(view);
    }

    public void removeViewsInLayout(ViewGroup viewGroup, Object start, Object count) {
        viewGroup.removeViewsInLayout(NumberUtils.toNumber(start).intValue(), NumberUtils.toNumber(count).intValue());
    }

    public void removeViewAt(ViewGroup viewGroup, Object index) {
        viewGroup.removeViewAt(NumberUtils.toNumber(index).intValue());
    }

    public void removeViews(ViewGroup viewGroup, Object start, Object count) {
        viewGroup.removeViews(NumberUtils.toNumber(start).intValue(), NumberUtils.toNumber(count).intValue());
    }

    public void removeAllViews(ViewGroup viewGroup) {
        viewGroup.removeAllViews();
    }

    public int getChildCount(ViewGroup viewGroup) {
        return viewGroup.getChildCount();
    }

    public View getChildAt(ViewGroup viewGroup, Object index) {
        return viewGroup.getChildAt(NumberUtils.toNumber(index).intValue());

    }
}

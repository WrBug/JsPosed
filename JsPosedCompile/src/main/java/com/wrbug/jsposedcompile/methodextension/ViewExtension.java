package com.wrbug.jsposedcompile.methodextension;

import android.view.View;

import com.squareup.javapoet.MethodSpec;
import com.wrbug.jsposedannotation.Constant;

import org.mozilla.javascript.Function;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

public class ViewExtension implements MethodExtension {
    private static final String VIEW_CLASS_NAME = "android.view.View";
    private static final String SET_ON_LONG_CLICK_LISTENER_METHOD_NAME = "setOnLongClickListener";
    private static final String SET_ON_FOCUS_CHANGE_LISTENER_METHOD_NAME = "setOnFocusChangeListener";
    private static final String SET_ON_CLICK_LISTENER_METHOD_NAME = "setOnClickListener";
    private static final String[] removeName = new String[]{SET_ON_LONG_CLICK_LISTENER_METHOD_NAME, SET_ON_FOCUS_CHANGE_LISTENER_METHOD_NAME, SET_ON_CLICK_LISTENER_METHOD_NAME};

    @Override
    public boolean isSupport(Class target) {
        return View.class.isAssignableFrom(target);
    }

    @Override
    public void build(List<MethodSpec> list) {
        removeMethod(list);
        list.add(buildOnclickListener());
        list.add(buildOnFocusChangeListener());
        list.add(buildOnLongClickListener());
    }

    private void removeMethod(List<MethodSpec> list) {
        List<MethodSpec> tmp = new ArrayList<>();
        for (MethodSpec spec : list) {
            for (String s : removeName) {
                if (spec.name.equals(s)) {
                    tmp.add(spec);
                }
            }
        }
        list.removeAll(tmp);
    }

    private MethodSpec buildOnLongClickListener() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(SET_ON_LONG_CLICK_LISTENER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Function.class, "function", Modifier.FINAL)
                .returns(void.class);
        String code = Constant.ORIGIN_VALUE_NAME + "." + SET_ON_LONG_CLICK_LISTENER_METHOD_NAME + "(new " + VIEW_CLASS_NAME + ".OnLongClickListener() {\n" +
                "            @Override\n" +
                "            public boolean onLongClick(" + VIEW_CLASS_NAME + " v) {\n" +
                "                mJsPosedExecutor.call(function, v);\n" +
                "                return false;\n" +
                "            }\n" +
                "        });";
        builder.addCode(code);
        return builder.build();
    }

    private MethodSpec buildOnFocusChangeListener() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(SET_ON_FOCUS_CHANGE_LISTENER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Function.class, "function", Modifier.FINAL)
                .returns(void.class);
        String code = Constant.ORIGIN_VALUE_NAME + "." + SET_ON_FOCUS_CHANGE_LISTENER_METHOD_NAME + "(new " + VIEW_CLASS_NAME + ".OnFocusChangeListener() {\n" +
                "            @Override\n" +
                "            public void onFocusChange(" + VIEW_CLASS_NAME + " v, boolean hasFocus) {\n" +
                "                mJsPosedExecutor.call(function, v, hasFocus);\n" +
                "            }\n" +
                "        });";
        builder.addCode(code);
        return builder.build();
    }

    private MethodSpec buildOnclickListener() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(SET_ON_CLICK_LISTENER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Function.class, "function", Modifier.FINAL)
                .returns(void.class);
        String code = Constant.ORIGIN_VALUE_NAME + "." + SET_ON_CLICK_LISTENER_METHOD_NAME + "(new " + VIEW_CLASS_NAME + ".OnClickListener() {\n" +
                "            @Override\n" +
                "            public void onClick(" + VIEW_CLASS_NAME + " v) {\n" +
                "                mJsPosedExecutor.call(function, v);\n" +
                "            }\n" +
                "        });";
        builder.addCode(code);
        return builder.build();
    }
}

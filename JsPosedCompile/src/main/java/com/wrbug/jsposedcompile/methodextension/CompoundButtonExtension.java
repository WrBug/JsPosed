package com.wrbug.jsposedcompile.methodextension;

import android.widget.CompoundButton;

import com.squareup.javapoet.MethodSpec;
import com.wrbug.jsposedannotation.Constant;

import org.mozilla.javascript.Function;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

public class CompoundButtonExtension implements MethodExtension {
    private static final String COMPOUND_BUTTON_CLASS_NAME = "android.widget.CompoundButton";
    private static final String SET_ON_LONG_CHECKED_CHANGE_LISTENER_METHOD_NAME = "setOnCheckedChangeListener";
    private static final String[] removeName = new String[]{SET_ON_LONG_CHECKED_CHANGE_LISTENER_METHOD_NAME};

    @Override
    public boolean isSupport(Class target) {
        return CompoundButton.class.isAssignableFrom(target);
    }

    @Override
    public void build(List<MethodSpec> list) {
        removeMethod(list);
        list.add(buildOnCheckedChangeListener());
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

    private MethodSpec buildOnCheckedChangeListener() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(SET_ON_LONG_CHECKED_CHANGE_LISTENER_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Function.class, "function", Modifier.FINAL)
                .returns(void.class);
        String code = Constant.ORIGIN_VALUE_NAME + "." + SET_ON_LONG_CHECKED_CHANGE_LISTENER_METHOD_NAME + "(new " + COMPOUND_BUTTON_CLASS_NAME + ".OnCheckedChangeListener() {\n" +
                "            @Override\n" +
                "            public void onCheckedChanged(" + COMPOUND_BUTTON_CLASS_NAME + " buttonView, boolean isChecked) {\n" +
                "                mJsPosedExecutor.call(function, buttonView, isChecked);\n" +
                "            }\n" +
                "        });";
        builder.addCode(code);
        return builder.build();
    }
}

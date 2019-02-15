package com.wrbug.jsposedcompile.methodextension;

import com.squareup.javapoet.MethodSpec;

import org.mozilla.javascript.Function;

import java.util.List;

import javax.lang.model.element.Modifier;

import de.robv.android.xposed.XposedHelpers;

public class XposedHelpersExtension implements MethodExtension {
    private static final String XPOSED_HELPERS_CLASS_NAME = "de.robv.android.xposed.XposedHelpers";

    @Override
    public boolean isSupport(Class target) {
        return XposedHelpers.class.isAssignableFrom(target);
    }

    @Override
    public void build(List<MethodSpec> list) {
//        list.add(buildFindClassMethod());
    }


    private MethodSpec buildFindClassMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("findClass")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(getClass().getSimpleName() + " 自动生成")
                .addParameter(String.class, "className", Modifier.FINAL)
                .addCode("return findClassIfExists(className, mParam.classLoader);")
                .returns(Class.class);
        return builder.build();
    }
}

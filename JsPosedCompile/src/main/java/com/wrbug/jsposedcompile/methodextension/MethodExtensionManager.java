package com.wrbug.jsposedcompile.methodextension;

import com.squareup.javapoet.MethodSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class MethodExtensionManager {
    private List<MethodExtension> mMethodExtensionList;
    private Messager mMessager;

    public MethodExtensionManager(Messager messager) {
        mMessager = messager;
        mMethodExtensionList = new ArrayList<>();
        mMethodExtensionList.add(new ViewExtension());
        mMethodExtensionList.add(new CompoundButtonExtension());
        mMethodExtensionList.add(new XposedHelpersExtension());
        mMethodExtensionList.add(new RealValueWrapperExtension(messager));
    }

    public List<MethodExtension> getExtension(Class target) {
        List<MethodExtension> list = new ArrayList<>();
        for (MethodExtension methodExtension : mMethodExtensionList) {
            if (methodExtension.isSupport(target)) {
//                mMessager.printMessage(Diagnostic.Kind.NOTE, target.getName() + " support " + methodExtension);
                list.add(methodExtension);
            }
        }
        return list;
    }

    public void build(List<MethodSpec> list, Class target) {
        List<MethodExtension> extension = getExtension(target);
        for (MethodExtension methodExtension : extension) {
            methodExtension.build(list);
        }
    }
}

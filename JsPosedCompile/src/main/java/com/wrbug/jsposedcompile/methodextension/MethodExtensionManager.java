package com.wrbug.jsposedcompile.methodextension;

import com.squareup.javapoet.MethodSpec;

import java.util.ArrayList;
import java.util.List;

public class MethodExtensionManager {
    private List<MethodExtension> mMethodExtensionList;

    public MethodExtensionManager() {
        mMethodExtensionList = new ArrayList<>();
        mMethodExtensionList.add(new ViewExtension());
        mMethodExtensionList.add(new CompoundButtonExtension());
        mMethodExtensionList.add(new XposedHelpersExtension());
    }

    public List<MethodExtension> getExtension(Class target) {
        List<MethodExtension> list = new ArrayList<>();
        for (MethodExtension methodExtension : mMethodExtensionList) {
            if (methodExtension.isSupport(target)) {
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

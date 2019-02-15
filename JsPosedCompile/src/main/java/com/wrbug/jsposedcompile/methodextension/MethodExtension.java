package com.wrbug.jsposedcompile.methodextension;

import com.squareup.javapoet.MethodSpec;

import java.util.List;

public interface MethodExtension {
    boolean isSupport(Class target);

    void build(List<MethodSpec> list);
}

package com.wrbug.jsposedcompile.methodextension;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.wrbug.jsposedannotation.RealValueWrapper;
import com.wrbug.jsposedcompile.DefaultValue;
import com.wrbug.jsposedcompile.JavaClassListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

public class RealValueWrapperExtension implements MethodExtension {
    private Messager mMessager;
    private static final String RealValueWrapperClassName = RealValueWrapper.class.getName();

    public RealValueWrapperExtension(Messager messager) {
        mMessager = messager;
    }

    @Override
    public boolean isSupport(Class target) {
        return true;
    }

    @Override
    public void build(List<MethodSpec> list) {
        try {
            Map<String, String> map = JavaClassListUtils.getList();
            List<MethodSpec> newMethod = new ArrayList<>();
            List<String> existMethod = new ArrayList<>();
            for (MethodSpec methodSpec : list) {
                if (methodSpec.isConstructor()) {
                    continue;
                }
                boolean changed = false;
                MethodSpec.Builder builder = toBuilder(methodSpec);
                StringBuilder defaultValueCode = new StringBuilder();
                StringBuilder code = new StringBuilder();
                if (!methodSpec.returnType.toString().equals(void.class.getName())) {
                    code.append("return ");
                }
                code.append(methodSpec.name).append("(");
                for (int i = 0; i < methodSpec.parameters.size(); i++) {
                    ParameterSpec parameter = methodSpec.parameters.get(i);
                    String key = parameter.type.toString();
                    if (map.containsKey(key)) {
                        Modifier[] modifiers = new Modifier[parameter.modifiers.size()];
                        parameter.modifiers.toArray(modifiers);
                        parameter = ParameterSpec.builder(RealValueWrapper.class, parameter.name, modifiers).build();
                        code.append("(").append(key).append(")").append(parameter.name).append(".").append("getRealValue(),");
                        defaultValueCode.append(getDefaultValueCode(methodSpec.returnType, key, parameter.name));
                        changed = true;
                    } else {
                        code.append(parameter.name).append(",");
                    }
                    builder.addParameter(parameter);
                }
                if (changed) {
                    code.deleteCharAt(code.length() - 1);
                    code.append(");\n");
                    builder.addCode(defaultValueCode.toString() + code.toString());
                    MethodSpec spec = builder.build();
                    String methodSpecString = getMethodSpecString(spec);
                    if (existMethod.contains(methodSpecString)) {
                        mMessager.printMessage(Diagnostic.Kind.NOTE, "忽略" + spec.name + spec.parameters);
                        continue;
                    }
                    mMessager.printMessage(Diagnostic.Kind.NOTE, "add methodSpecString: " + methodSpecString);
                    existMethod.add(methodSpecString);
                    newMethod.add(spec);
                }
            }
            mMessager.printMessage(Diagnostic.Kind.NOTE, "newMethod size=" + newMethod.size());
            list.addAll(newMethod);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    private String getMethodSpecString(MethodSpec methodSpec) {
        List<TypeName> typeNames = new ArrayList<>();
        for (ParameterSpec parameter : methodSpec.parameters) {
            typeNames.add(parameter.type);
        }
        return methodSpec.name + typeNames + methodSpec.returnType;
    }

    private String getDefaultValueCode(TypeName returnType, String instanceOfClassName, String name) {
        StringBuilder defaultCode = new StringBuilder();
        String s = DefaultValue.get(returnType);
        defaultCode.append("if(!(").append(name).append(".getRealValue() instanceof ").append(instanceOfClassName).append(")){\n return ").append(s).append(";\n}\n");
        return defaultCode.toString();
    }

    private MethodSpec.Builder toBuilder(MethodSpec methodSpec) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(methodSpec.name);
        CodeBlock defaultValue = methodSpec.defaultValue;
        builder.addJavadoc("RealValueWrapperExtension 自动生成，方便用于js调用")
                .addAnnotations(methodSpec.annotations)
                .addModifiers(methodSpec.modifiers)
                .addTypeVariables(methodSpec.typeVariables)
                .returns(methodSpec.returnType)
                .addExceptions(methodSpec.exceptions)
                .varargs(methodSpec.varargs);
        if (defaultValue != null) {
            builder.defaultValue(defaultValue);
        }
        return builder;
    }
}

package com.wrbug.jsposedcompile;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.jsposedannotation.JavaClass;
import com.wrbug.jsposedannotation.JavaMethod;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class JsPosedProcessor extends AbstractProcessor {
    private static final String BUILD_PACKAGE = "com.wrbug.jsposed.jclass.build";
    private Messager mMessager;
    private Elements mElementUtils;
    private Filer mFiler;
    private static final String ORIGIN_VALUE_NAME = "value0";
    private Map<String, String> javaMethodMap = new HashMap<>();
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 生成文件所需
        javaMethodMap.clear();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
        moduleName = processingEnv.getOptions().get("moduleName");
        if (moduleName != null) {
            moduleName = moduleName.replace("-", "_").toLowerCase();
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportAnnotations = new HashSet<>();
        supportAnnotations.add(JavaClass.class.getCanonicalName());
        return supportAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        Set<? extends Element> javaClassElements = roundEnvironment.getElementsAnnotatedWith(JavaClass.class);
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process routeElements size " + javaClassElements.size());
        for (Element element : javaClassElements) {
            JavaClass javaClass = element.getAnnotation(JavaClass.class);
            Class clazz = null;
            try {
                clazz = javaClass.value();
            } catch (MirroredTypeException e) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, e.getTypeMirror().toString());
                try {
                    clazz = Class.forName(e.getTypeMirror().toString());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

            if (clazz == null || clazz.isInterface()) {
                continue;
            }
            List<MethodSpec> list = new ArrayList<>();
            buildDefaultMethod(list, javaClass, element, clazz);
            buildConstructorMethod(list, element, clazz);
            Method[] declaredMethods = clazz.getMethods();
            for (Method declaredMethod : declaredMethods) {
                if (!java.lang.reflect.Modifier.isPublic(declaredMethod.getModifiers())
                        || java.lang.reflect.Modifier.isFinal(declaredMethod.getModifiers())
                        || java.lang.reflect.Modifier.isAbstract(declaredMethod.getModifiers())
                        || (declaredMethod.getModifiers() & 4096) != 0) {
                    continue;
                }
                boolean staticMethod = java.lang.reflect.Modifier.isStatic(declaredMethod.getModifiers());
                StringBuilder code = new StringBuilder();
                MethodSpec.Builder builder = MethodSpec.methodBuilder(declaredMethod.getName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(declaredMethod.getReturnType());

                if (declaredMethod.getReturnType() != void.class) {
                    code.append("return ");
                }
                Type[] genericExceptionTypes = declaredMethod.getGenericExceptionTypes();
                if (genericExceptionTypes != null && genericExceptionTypes.length > 0) {
                    for (Type exceptionType : genericExceptionTypes) {
                        builder.addException(exceptionType);
                    }
                }
                if (staticMethod) {
                    builder.addModifiers(Modifier.STATIC);
                }
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                int a = 0;
                StringBuilder params = new StringBuilder();
                for (Class<?> parameterType : parameterTypes) {
                    String name = "arg" + a++;
                    builder.addParameter(parameterType, name);
                    params.append(name).append(",");
                }
                if (params.length() > 0) {
                    params.deleteCharAt(params.length() - 1);
                }
                code.append(staticMethod ? clazz.getName() : ORIGIN_VALUE_NAME).append(".").append(declaredMethod.getName()).append("(").append(params).append(");\n");
                builder.addCode(code.toString());
                list.add(builder.build());
            }
            buildJavaFile(element.getSimpleName().toString(), clazz, list);
            String jsName = javaClass.name();
            if (jsName.isEmpty()) {
                jsName = element.getSimpleName().toString();
            }
            javaMethodMap.put(jsName, element.toString());
        }
        buildMap();
        mMessager.printMessage(Diagnostic.Kind.NOTE, javaMethodMap.toString());
        return true;
    }


    private void buildConstructorMethod(List<MethodSpec> list, Element element, Class clazz) {
        Constructor[] constructors = clazz.getConstructors();
        boolean existDefaultMethod = false;
        for (Constructor constructor : constructors) {
            if (java.lang.reflect.Modifier.isPublic(constructor.getModifiers())) {
                buildStaticConstructorMethod(list, element, constructor);
                MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC);
                Class[] parameterTypes = constructor.getParameterTypes();
                StringBuilder code = new StringBuilder();
                code.append(ORIGIN_VALUE_NAME).append(" = new ").append(clazz.getSimpleName()).append("( ");
                if (parameterTypes.length == 0) {
                    existDefaultMethod = true;
                } else {
                    int i = 0;
                    for (Class parameterType : parameterTypes) {
                        String params = "arg" + (i++);
                        builder.addParameter(parameterType, params);
                        code.append(params).append(" ,");
                    }
                    if (i > 0) {
                        code.deleteCharAt(code.length() - 1);
                    }
                }
                code.append(");\n");
                builder.addCode(code.toString());
                list.add(builder.build());
            }
        }
        if (!existDefaultMethod) {
            list.add(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC).build());
        }
    }

    private void buildStaticConstructorMethod(List<MethodSpec> list, Element element, Constructor constructor) {
        StringBuilder code = new StringBuilder();
        code.append("return new ").append(element.getSimpleName()).append("_").append("( ");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("newInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Object.class);
        Class[] parameterTypes = constructor.getParameterTypes();
        int i = 0;
        for (Class parameterType : parameterTypes) {
            String params = "arg" + (i++);
            builder.addParameter(parameterType, params);
            code.append(params).append(" ,");
        }
        if (i > 0) {
            code.deleteCharAt(code.length() - 1);
        }
        code.append(");\n");
        builder.addCode(code.toString());
        list.add(builder.build());
    }

    private void buildDefaultMethod(List<MethodSpec> list, JavaClass javaClass, Element element, Class clazz) {
        String jsName = javaClass.name();
        if (jsName.isEmpty()) {
            jsName = element.getSimpleName().toString();
        }
        MethodSpec.Builder getNameMethod = MethodSpec.methodBuilder("getJavaMethodName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode("return \"" + jsName + "\";\n")
                .returns(String.class);
        MethodSpec.Builder getRealValueMethod = MethodSpec.methodBuilder("getRealValue")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("获取原始值")
                .addCode("return " + ORIGIN_VALUE_NAME + ";\n")
                .returns(clazz);
        list.add(getNameMethod.build());
        list.add(getRealValueMethod.build());
    }

    private void buildMap() {
        if (javaMethodMap.isEmpty()) {
            return;
        }
        final String mapName = "map";
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getJavaMethodMap")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode("return map;\n")
                .returns(Map.class);
        StringBuilder staticCode = new StringBuilder();
        staticCode.append("map=new HashMap();").append("\n");
        for (Map.Entry<String, String> entry : javaMethodMap.entrySet()) {
            staticCode.append("map.put(\"").append(entry.getKey()).append("\", new ").append(entry.getValue()).append("());").append("\n ");
        }
        TypeSpec typeSpec = TypeSpec.classBuilder("JavaMethodMap")
                .addMethods(Arrays.asList(builder.build()))
                .addField(HashMap.class, mapName, Modifier.STATIC)
                .addStaticBlock(CodeBlock.of(staticCode.toString()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();
        buildFile(typeSpec);
    }

    private void buildJavaFile(String fileName, Class clazz, List<MethodSpec> list) {
        // 生成java文件，包含导航器Map
        TypeSpec typeSpec = TypeSpec.classBuilder(fileName + "_")
                .superclass(JavaMethod.class)
                .addMethods(list)
                .addField(clazz, ORIGIN_VALUE_NAME)
                .addModifiers(Modifier.PUBLIC).build();
        buildFile(typeSpec);
    }

    private void buildFile(TypeSpec typeSpec) {
        if (moduleName == null) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, "无法生成文件" + typeSpec.name + "，请在相应模块build.gradle中加入:\n" +
                    "defaultConfig {\n        javaCompileOptions {\n" +
                    "            annotationProcessorOptions {\n" +
                    "                includeCompileClasspath true\n" +
                    "                arguments = [moduleName: project.getName()]\n" +
                    "            }\n" +
                    "        }\n}");
            return;
        }
        // 尝试生成文件
        try {
            JavaFile.builder(BUILD_PACKAGE + "." + moduleName, typeSpec)
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}

package com.wrbug.jsposedcompile;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.jsposedannotation.Constant;
import com.wrbug.jsposedannotation.JavaClass;
import com.wrbug.jsposedannotation.JavaMethod;
import com.wrbug.jsposedcompile.methodextension.MethodExtensionManager;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
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

import static com.wrbug.jsposedannotation.Constant.ORIGIN_VALUE_NAME;

@AutoService(Processor.class)
public class JsPosedProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Elements mElementUtils;
    private Filer mFiler;
    private Map<String, String> javaMethodMap = new HashMap<>();
    private String moduleName;
    private List<String> objectFinalMethodNames;
    private MethodExtensionManager mMethodExtensionManager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        // 生成文件所需
        mMethodExtensionManager = new MethodExtensionManager();
        javaMethodMap.clear();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
        moduleName = processingEnv.getOptions().get("moduleName");
        if (moduleName != null) {
            moduleName = moduleName.replace("-", "_").toLowerCase();
        }
        initObjectFinalMethod();
    }


    private void initObjectFinalMethod() {
        objectFinalMethodNames = new ArrayList<>();
        Method[] declaredMethods = Object.class.getDeclaredMethods();
        List<Method> tmp = new ArrayList<>(Arrays.asList(declaredMethods));
        Method[] methods = Object.class.getMethods();
        tmp.addAll(Arrays.asList(methods));
        for (Method method : tmp) {
            if (java.lang.reflect.Modifier.isFinal(method.getModifiers())) {
                objectFinalMethodNames.add(getMethodName(method));
            }
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
        for (Element element : javaClassElements) {
            JavaClass javaClass = element.getAnnotation(JavaClass.class);
            Class targetClass = null;
            try {
                targetClass = javaClass.value();
            } catch (MirroredTypeException e) {
                mMessager.printMessage(Diagnostic.Kind.NOTE, e.getTypeMirror().toString());
                try {
                    targetClass = Class.forName(e.getTypeMirror().toString());
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
            if (targetClass == null || targetClass.isInterface()) {
                continue;
            }
            List<MethodSpec> list = new ArrayList<>();
            buildDefaultMethod(list, javaClass, element, targetClass);
            buildWrapperMethod(list, element, targetClass);
            buildConstructorMethod(list, element, targetClass);
            Map<String, List<Method>> methodMap = getMethod(targetClass);
            for (Map.Entry<String, List<Method>> entry : methodMap.entrySet()) {
                String methodName = entry.getKey();
                Map<String, MethodSpec> existMethodMap = new HashMap<>();
                List<Method> methodList = entry.getValue();
                for (Method declaredMethod : methodList) {
                    if (!java.lang.reflect.Modifier.isPublic(declaredMethod.getModifiers())
                            || (declaredMethod.getModifiers() & 4096) != 0) {
                        continue;
                    }
                    String existMethodMapKey = declaredMethod.getName() + Arrays.toString(declaredMethod.getParameterTypes());
                    MethodSpec methodSpec = existMethodMap.get(existMethodMapKey);
                    if (methodSpec != null) {
                        list.remove(methodSpec);
                    }
                    boolean staticMethod = java.lang.reflect.Modifier.isStatic(declaredMethod.getModifiers());
                    StringBuilder code = new StringBuilder();
                    MethodSpec.Builder builder = MethodSpec.methodBuilder(declaredMethod.getName())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(declaredMethod.getReturnType());
                    if (!staticMethod) {
                        if (declaredMethod.getReturnType().isPrimitive()) {
                            String defaultValue = "0";
                            if (declaredMethod.getReturnType() == void.class) {
                                defaultValue = "";
                            } else if (declaredMethod.getReturnType() == boolean.class) {
                                defaultValue = "false";
                            }
                            code.append("if(").append(ORIGIN_VALUE_NAME).append("==null){return ").append(defaultValue).append(";}");
                        } else if (declaredMethod.getReturnType() == String.class) {
                            code.append("if(").append(ORIGIN_VALUE_NAME).append("==null){return \"\";}");
                        } else {
                            code.append("if(").append(ORIGIN_VALUE_NAME).append("==null){return null;}");
                        }
                    }
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
                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        String name = "arg" + a++;
                        Class tmp = parameterType;
                        if (parameterType == int.class) {
                            tmp = long.class;
                            parameterTypes[i] = tmp;
                        }
                        builder.addParameter(tmp, name);
                        if (parameterType.isPrimitive()) {
                            params.append("(").append(parameterType.getSimpleName()).append(")");
                        }
                        params.append(name).append(",");
                    }
                    existMethodMapKey = declaredMethod.getName() + Arrays.toString(parameterTypes);
                    MethodSpec spec = existMethodMap.get(existMethodMapKey);
                    if (spec != null) {
                        continue;
                    }
                    if (params.length() > 0) {
                        params.deleteCharAt(params.length() - 1);
                    }
                    code.append(staticMethod ? targetClass.getName() : ORIGIN_VALUE_NAME).append(".").append(declaredMethod.getName()).append("(").append(params).append(");\n");
                    builder.addCode(code.toString());
                    MethodSpec build = builder.build();
                    list.add(build);
                    existMethodMap.put(existMethodMapKey, build);
                }
            }

            mMethodExtensionManager.build(list, targetClass);
            buildJavaFile(element.getSimpleName().toString(), targetClass, list);
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


    private Map<String, List<Method>> getMethod(Class clazz) {
        List<String> names = new ArrayList<>();
        Class currentClass = clazz;
        Map<String, List<Method>> map = new HashMap<>();
        while (currentClass != Object.class) {
            Method[] declaredMethods = currentClass.getDeclaredMethods();
            List<Method> tmp = new ArrayList<>(Arrays.asList(declaredMethods));
            Method[] methods = currentClass.getMethods();
            tmp.addAll(Arrays.asList(methods));
            for (Method method : tmp) {
                String name = getMethodName(method);
                if (objectFinalMethodNames.contains(name)) {
                    continue;
                }
                if (names.contains(name)) {
                    continue;
                }
                names.add(name);
                List<Method> list = map.get(method.getName());
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(method);
                map.put(method.getName(), list);
            }
            currentClass = currentClass.getSuperclass();
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, names.toString());
        return map;
    }

    private String getMethodName(Method method) {
        String modifier = java.lang.reflect.Modifier.isPublic(method.getModifiers()) + "_" + java.lang.reflect.Modifier.isStatic(method.getModifiers());
        return (method.getName() + "_" + modifier + "_" + method.getReturnType() + "_" + Arrays.toString(method.getParameterTypes()));
    }

    private void buildConstructorMethod(List<MethodSpec> list, Element element, Class clazz) {
        if (java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return;
        }
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

    private void buildWrapperMethod(List<MethodSpec> list, Element element, Class clazz) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("wrapperInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(clazz, "arg0")
                .addJavadoc("包装value")
                .returns(Object.class);
        StringBuilder code = new StringBuilder();
        code.append(element).append(" instance=new ").append(element).append("();\n ");
        code.append("instance.").append(ORIGIN_VALUE_NAME).append(" = arg0;\n ");
        code.append("return instance;\n ");
        builder.addCode(code.toString());
        list.add(builder.build());
    }


    private void buildStaticConstructorMethod(List<MethodSpec> list, Element element, Constructor constructor) {
        StringBuilder code = new StringBuilder();
        code.append("return new ").append(element).append("( ");
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
        final String arrName = Constant.JAVA_METHOD_ARRAY_FIELD_NAME;
        MethodSpec.Builder builder = MethodSpec.methodBuilder(Constant.JAVA_METHOD_ARRAY_GET_METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode("return " + arrName + ";\n")
                .returns(String[].class);
        StringBuilder staticCode = new StringBuilder();
        staticCode.append(arrName + "=new String[").append(javaMethodMap.size()).append("]; \n ");
        int i = 0;
        for (Map.Entry<String, String> entry : javaMethodMap.entrySet()) {
            staticCode.append(arrName).append("[").append(i++).append("]=\"").append(entry.getValue()).append("\";\n");
        }
        TypeSpec typeSpec = TypeSpec.classBuilder(Constant.JAVA_METHOD_ARRAY_CLASS_NAME)
                .addMethods(Arrays.asList(builder.build()))
                .addField(String[].class, arrName, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .addStaticBlock(CodeBlock.of(staticCode.toString()))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();
        buildFile(typeSpec);
    }

    private void buildJavaFile(String fileName, Class clazz, List<MethodSpec> list) {
        // 生成java文件，包含导航器Map
        TypeSpec typeSpec = TypeSpec.classBuilder(fileName + "_")
                .superclass(JavaMethod.class)
                .addMethods(list)
                .addField(clazz, ORIGIN_VALUE_NAME, Modifier.PROTECTED)
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
            JavaFile.builder(Constant.BUILD_PACKAGE + "." + moduleName, typeSpec)
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}

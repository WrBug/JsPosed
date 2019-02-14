package com.wrbug.jsposeddemo;

import com.wrbug.jsposedannotation.JavaMethod;

public class Test extends JavaMethod {

    @Override
    public String getJavaMethodName() {
        return "Test";
    }

    public void test(String str) {
        System.out.println(str);
    }


    public int test(int num) {
        return num * num;
    }
}

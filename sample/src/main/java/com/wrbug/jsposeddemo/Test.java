package com.wrbug.jsposeddemo;

import com.wrbug.jsposed.jscall.JavaMethod;

public class Test extends JavaMethod {

    @Override
    public String getName() {
        return "Test";
    }

    public void test(String str) {
        System.out.println(str);
    }


    public int test(int num) {
        return num * num;
    }
}

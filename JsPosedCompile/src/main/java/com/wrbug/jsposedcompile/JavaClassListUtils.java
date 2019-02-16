package com.wrbug.jsposedcompile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaClassListUtils {
    private static final String fileName = "javaclass.txt";

    static {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveJavaClass(Class target, String className) {
        Map<String, String> map = getList();
        if (map.containsKey(target.getName())) {
            return;
        }
        map.put(target.getName(), className);
        writeList(map);
    }

    private static void writeList(Map<String, String> map) {
        String result = new Gson().toJson(map);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName));
            fileOutputStream.write(result.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Map<String, String> getList() {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(fileName));
            int size = fileInputStream.available();
            if (size <= 0) {
                return new HashMap<>();
            }
            byte[] data = new byte[size];
            fileInputStream.read(data);
            fileInputStream.close();
            String result = new String(data);
            Map<String, String> map = new Gson().fromJson(result, new TypeToken<Map<String, String>>() {
            }.getType());
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }


    private static String join(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

}

package com.wrbug.jsposed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayManager {
    private List mList = new ArrayList();

    public static ArrayManager getInstance() {
        ArrayManager manager = new ArrayManager();
        return manager;
    }

    public ArrayManager addList(List list) {
        mList.addAll(list);
        return this;
    }

    public ArrayManager addArray(Object[] data) {
        mList.addAll(Arrays.asList(data));
        return this;
    }

    public ArrayManager add(Object data) {
        mList.add(data);
        return this;
    }

    public Object[] toArray() {
        return mList.toArray();
    }
}

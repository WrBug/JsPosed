package com.wrbug.jsposedannotation;

import org.mozilla.javascript.Function;

public interface Executable {
    void run(String js);

    void call(Function call, Object... args);
}

package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface Callback {
    void call();
}

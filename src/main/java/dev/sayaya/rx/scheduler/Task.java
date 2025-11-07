package dev.sayaya.rx.scheduler;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface Task<T> {
    void exec(T param);
}

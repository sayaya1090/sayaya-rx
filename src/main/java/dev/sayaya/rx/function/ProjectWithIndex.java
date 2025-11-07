package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface ProjectWithIndex<T, R> {
    R map(T value, int index);
}

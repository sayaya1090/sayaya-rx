package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface AccumulatorWithIndex<T> {
    T accumulate(T acc, T value, int index);
}

package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface Reducer<T1, T2, O> {
    O reduce(T1 t1, T2 t2);
}

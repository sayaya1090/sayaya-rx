package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface PredicateWithIndex<T> {
    boolean test(T value, int index);
}

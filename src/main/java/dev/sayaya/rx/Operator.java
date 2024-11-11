package dev.sayaya.rx;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;

public class Operator {
    @JsMethod(namespace="rxjs", name="map")
    public static native <A, B> Operator map(Function<A, B> func);
    @JsFunction
    public interface Function<T, R> {
        R apply(T t);
    }
}

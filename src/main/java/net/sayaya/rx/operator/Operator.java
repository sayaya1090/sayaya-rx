package net.sayaya.rx.operator;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

public class Operator {
    @JsMethod(namespace="rxjs", name="map")
    public static native <A, B> Operator map(Function<A, B> func);
    @JsFunction
    public interface Function<T, R> {
        R apply(T t);
    }
}

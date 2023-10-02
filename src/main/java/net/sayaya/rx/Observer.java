package net.sayaya.rx;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace="rxjs", name="Observer")
public interface Observer<T> {
    void next(T value);
    void error(Throwable error);
    void complete();

    @FunctionalInterface
    interface ObserverDefault<T> extends Observer<T> {
        default void error(Throwable error) {}
        default void complete() {}
    }
}

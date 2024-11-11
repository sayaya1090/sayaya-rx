package dev.sayaya.rx;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="Subscriber")
public final class Subscriber<T> {
    public native void next(T value);
    public native void complete();
    public native void error(Object e);
}

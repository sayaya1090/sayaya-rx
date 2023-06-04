package net.sayaya.rx;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="Subject")
public class Subject<T> extends Observable<T> implements Observer<T> {
    @Override
    public native void next(T value);
    @Override
    public native void error(Throwable error);
    @Override
    public native void complete();
    public native Observable<T> asObservable();
}

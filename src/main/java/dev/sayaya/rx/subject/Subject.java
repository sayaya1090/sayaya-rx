package dev.sayaya.rx.subject;

import dev.sayaya.rx.Observable;
import dev.sayaya.rx.Observer;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="Subject")
public class Subject<T> extends Observable<T> implements Observer<T> {
    public Subject(){}
    @Override public native void next(T value);
    @Override public native void error(Object error);
    @Override public native void complete();
    public native Observable<T> asObservable();

    @JsOverlay
    public static <T> Subject<T> subject(Class<T> clazz) {
        return new Subject<>();
    }
}

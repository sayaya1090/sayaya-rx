package dev.sayaya.rx.subject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import dev.sayaya.rx.Observable;

@JsType(isNative = true, namespace="rxjs", name="BehaviorSubject")
public class BehaviorSubject<T> extends Subject<T> {
    public BehaviorSubject(T value){}
    @Override public native void next(T value);
    @Override public native void error(Object error);
    @Override public native void complete();
    public native T getValue();
    public native Observable<T> asObservable();

    @JsOverlay
    public static <T> BehaviorSubject<T> behavior(T init) {
        return new BehaviorSubject<>(init);
    }
}

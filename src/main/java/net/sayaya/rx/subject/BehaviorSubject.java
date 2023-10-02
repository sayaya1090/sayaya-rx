package net.sayaya.rx.subject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import net.sayaya.rx.Observable;

import java.util.List;

@JsType(isNative = true, namespace="rxjs", name="BehaviorSubject")
public class BehaviorSubject<T> extends Subject<T> {
    @JsOverlay
    public static <T> BehaviorSubject<T> behavior(T init) {
        return new BehaviorSubject<>(init);
    }
    protected BehaviorSubject(T value){}
    @Override
    public native void next(T value);
    @Override
    public native void error(Throwable error);
    @Override
    public native void complete();
    public native T getValue();
    public native Observable<T> asObservable();
}

package net.sayaya.rx.subject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import net.sayaya.rx.Observable;
import net.sayaya.rx.Observer;

@JsType(isNative = true, namespace="rxjs", name="Subject")
public class Subject<T> extends Observable<T> implements Observer<T> {
    @JsOverlay
    public static <T> Subject<T> subject(Class<T> clazz) {
        return new Subject<>();
    }
    protected Subject(){}
    @Override
    public native void next(T value);
    @Override
    public native void error(Throwable error);
    @Override
    public native void complete();
    public native Observable<T> asObservable();
}

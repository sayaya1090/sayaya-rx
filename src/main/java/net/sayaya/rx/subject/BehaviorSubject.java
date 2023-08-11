package net.sayaya.rx.subject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import net.sayaya.rx.Observable;

import java.util.List;
import java.util.stream.Collectors;

@JsType(isNative = true, namespace="rxjs", name="BehaviorSubject")
public class BehaviorSubject<T> extends Subject<T> {
    @JsOverlay
    public static <T> BehaviorSubject<T> single(T init) {
        return new BehaviorSubject<>(init);
    }
    @JsOverlay
    public static <T> BehaviorListSubject<T> many(List<T> init) {
        return new BehaviorListSubject<>(init.stream().map(BehaviorSubject::single).collect(Collectors.toList()));
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

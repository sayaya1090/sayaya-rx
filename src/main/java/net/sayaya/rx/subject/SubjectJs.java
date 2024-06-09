package net.sayaya.rx.subject;

import jsinterop.annotations.JsType;
import net.sayaya.rx.Observable;
import net.sayaya.rx.Observer;

@JsType(isNative = true, namespace="rxjs", name="Subject")
public class SubjectJs<T> extends Observable<T> implements Observer<T> {
    protected SubjectJs(){}
    @Override public native void next(T value);
    @Override public native void error(Object error);
    @Override public native void complete();
    public native Observable<T> asObservable();
}

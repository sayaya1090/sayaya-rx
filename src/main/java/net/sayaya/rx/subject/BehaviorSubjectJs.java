package net.sayaya.rx.subject;

import jsinterop.annotations.JsType;
import net.sayaya.rx.Observable;

@JsType(isNative = true, namespace="rxjs", name="BehaviorSubject")
public class BehaviorSubjectJs<T> extends SubjectJs<T> {
    protected BehaviorSubjectJs(T value){}
    @Override
    public native void next(T value);
    @Override
    public native void error(Throwable error);
    @Override
    public native void complete();
    public native T getValue();
    public native Observable<T> asObservable();
}

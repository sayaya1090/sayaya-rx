package net.sayaya.rx;

import jsinterop.annotations.*;
import net.sayaya.rx.operator.Operator;

@JsType(isNative = true, namespace="rxjs", name="Observable")
public class Observable<T> {
    @JsMethod(namespace="rxjs", name="of")
    public static native <T> Observable<T> of(T... values);
    @JsMethod(namespace="rxjs", name="from")
    public static native <T> Observable<T> from(Object any);
    protected Observable(){}
    protected Observable(Subscribe<T> subscriber){}
    public native Subscription subscribe(Observer<T> observer);
    @JsOverlay public final Subscription subscribe(Observer.ObserverDefault<T> observer) {return subscribe((Observer<T>) observer); }
    private native <V> Observable<V> pipe(Operator operator);
    @JsOverlay public final <V> Observable<V> map(Operator.Function<T, V> function) {
        return pipe(Operator.map(function));
    }

    @JsFunction
    public interface Subscribe<T> {
        void apply(Subscriber<T> subscriber);
    }
}

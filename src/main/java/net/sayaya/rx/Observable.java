package net.sayaya.rx;

import jsinterop.annotations.*;

@JsType(isNative = true, namespace="rxjs", name="Observable")
public class Observable<T> {
    @JsMethod(namespace="rxjs", name="of")
    static native <T> Observable<T> of(T... values);

    public Observable(){}
    public Observable(Subscribe<T> subscriber){}
    public native Subscription subscribe(Observer<T> observer);
    @JsOverlay public final Subscription subscribe(Observer.ObserverDefault<T> observer) {return subscribe((Observer<T>) observer); }
    private native <V> Observable<V> pipe(Object operator);
    @JsOverlay public final <V> Observable<V> map(Operator.Function<T, V> function) {
        return pipe(Operator.map(function));
    }

    @JsFunction
    public interface Subscribe<T> {
        void apply(Subscriber<T> subscriber);
    }
    @JsFunction
    public interface Consumer<T> {
        @JsOverlay
        default void apply(T data, Object b, Object c) {apply(data);}
        @JsIgnore
        void apply(T data);
    }
}

package dev.sayaya.rx.function;

import dev.sayaya.rx.Observable;
import jsinterop.annotations.JsFunction;

@FunctionalInterface
@JsFunction
public interface OperatorFunction<T, R> {
    Observable<R> call(Observable<T> t);
}

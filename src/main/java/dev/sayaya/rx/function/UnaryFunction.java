package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

/**
 * 하나의 매개변수 T를 받아 또 다른 매개변수 R을 반환하는 함수를 설명하는 함수형 타입 인터페이스입니다.
 @see <a href="https://rxjs.dev/api/index/interface/UnaryFunction">Guide</a>
 */
@FunctionalInterface
@JsFunction
public interface UnaryFunction<T, R> {
    R apply(T t);
}

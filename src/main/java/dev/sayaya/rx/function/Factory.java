package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

/**
 * 인자 없이 값을 만들어 반환하는 함수형 타입입니다. (defer, throwError의 팩토리 등)
 */
@FunctionalInterface
@JsFunction
public interface Factory<R> {
    R create();
}

package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

/** max/min의 비교 함수. a가 크면 양수, 같으면 0, 작으면 음수를 돌려줍니다. */
@FunctionalInterface
@JsFunction
public interface Comparer<T> {
    int compare(T a, T b);
}

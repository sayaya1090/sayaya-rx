package dev.sayaya.rx.function;

import jsinterop.annotations.JsFunction;

/** mergeScan/switchScan의 누적 함수. (누적값, 현재값, 인덱스) → 결과. */
@FunctionalInterface
@JsFunction
public interface IndexedAccumulator<A, T, R> {
    R accumulate(A acc, T value, int index);
}

package dev.sayaya.rx.function;

import dev.sayaya.rx.Observable;
import elemental2.core.JsError;
import jsinterop.annotations.JsFunction;

/** retry 설정의 delay 함수. 반환한 Observable이 값을 내면 재시도합니다. */
@FunctionalInterface
@JsFunction
public interface RetryDelay {
    Observable<?> delay(JsError error, int retryCount);
}

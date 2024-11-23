package dev.sayaya.rx;

import elemental2.core.JsError;
import jsinterop.annotations.*;

import java.util.function.Consumer;

/**
 * Observable의 알림 이벤트 집합에 대해 사용자가 통지를 받을 수 있도록 설정된 콜백 함수 집합을 정의하는
 * 객체 인터페이스입니다.
 @link <a href="https://rxjs.dev/api/index/interface/Observer">Guide</a>
 */
@JsType(isNative = true)
public interface Observer<T> {
    void next(T value);
    void error(JsError error);
    void complete();
    @JsOverlay static <T> Observer<T> next(Consumer<T> consumer) {
        return new Observer<T>() {
            public void next(T value) {
                consumer.accept(value);
            }
            public void error(JsError error) {}
            public void complete() {}
        };
    }
}

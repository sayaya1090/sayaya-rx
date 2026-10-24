package dev.sayaya.rx;

import elemental2.core.JsError;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.*;
import jsinterop.base.Js;

import java.util.function.Consumer;

/**
 * Observable의 알림 이벤트 집합에 대해 사용자가 통지를 받을 수 있도록 설정된 콜백 함수 집합을 정의하는
 * 객체 인터페이스입니다.
 @see <a href="https://rxjs.dev/api/index/interface/Observer">Guide</a>
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
            /**
             * 에러 콜백이 없을 때의 RxJS 기본 동작과 같게, 처리되지 않은 에러로 보고합니다.
             * rxjs.config.onUnhandledError가 있으면 그쪽으로, 없으면 비동기로 throw합니다.
             */
            public void error(JsError error) {
                Object config = Js.asPropertyMap(Js.global().get("rxjs")).get("config");
                Object handler = config == null ? null : Js.asPropertyMap(config).get("onUnhandledError");
                if (handler != null) Js.<dev.sayaya.rx.function.VoidFunction<Object>>uncheckedCast(handler).apply(error);
                else DomGlobal.setTimeout(ignore -> { throw new com.google.gwt.core.client.JavaScriptException(error); }, 0);
            }
            public void complete() {}
        };
    }
}

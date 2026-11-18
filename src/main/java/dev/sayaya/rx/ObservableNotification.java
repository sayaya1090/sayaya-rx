package dev.sayaya.rx;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * materialize/dematerialize가 다루는 알림 객체입니다. kind는 "N"(값), "E"(에러), "C"(완료) 중 하나입니다.
 * @see <a href="https://rxjs.dev/api/index/type-alias/ObservableNotification">Guide</a>
 */
@JsType(isNative = true, namespace = GLOBAL, name = "Object")
public class ObservableNotification<T> {
    public String kind;
    public T value;
    public Object error;

    @JsOverlay public static <T> ObservableNotification<T> next(T value) {
        ObservableNotification<T> n = new ObservableNotification<>();
        n.kind = "N";
        n.value = value;
        return n;
    }
    @JsOverlay public static <T> ObservableNotification<T> error(Object error) {
        ObservableNotification<T> n = new ObservableNotification<>();
        n.kind = "E";
        n.error = error;
        return n;
    }
    @JsOverlay public static <T> ObservableNotification<T> complete() {
        ObservableNotification<T> n = new ObservableNotification<>();
        n.kind = "C";
        return n;
    }
}

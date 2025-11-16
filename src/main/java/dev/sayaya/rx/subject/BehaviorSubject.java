package dev.sayaya.rx.subject;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * 초기 값이 필요하고 구독될 때마다 현재 값을 내보내는 Subject의 변형입니다.
 * @see <a href="https://rxjs.dev/api/index/class/BehaviorSubject">Guide</a>
 */
@JsType(isNative = true, namespace="rxjs", name="BehaviorSubject")
public class BehaviorSubject<T> extends Subject<T> {
    @JsConstructor
    protected BehaviorSubject(T value){}
    public native T getValue();

    @JsOverlay
    public static <T> BehaviorSubject<T> behavior(T init) {
        return new BehaviorSubject<>(init);
    }
}

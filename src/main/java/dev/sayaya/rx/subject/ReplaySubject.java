package dev.sayaya.rx.subject;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * 이 Subject의 변형은 처음 구독할 때 이전 값을 방출하여 새 구독자에게 이전 값을 "재생"하는 것입니다.
 * @link <a href="https://rxjs.dev/api/index/class/ReplaySubject">Guide</a>
 */
@JsType(isNative = true, namespace="rxjs", name="ReplaySubject")
public class ReplaySubject<T> extends Subject<T> {
    @JsOverlay
    public static <T> ReplaySubject<T> replay(Class<T> clazz) {
        return new ReplaySubject<>();
    }
    @JsOverlay
    public static <T> ReplaySubject<T> replayWithBuffer(Class<T> clazz, int buffer) {
        return new ReplaySubject<>(buffer);
    }
    @JsOverlay
    public static <T> ReplaySubject<T> replayWithBufferAndWindowTime(Class<T> clazz, int buffer, int windowTime) {
        return new ReplaySubject<>(buffer, windowTime);
    }
    @JsConstructor
    protected ReplaySubject(){}
    @JsConstructor
    protected ReplaySubject(int buffer){}
    @JsConstructor
    protected ReplaySubject(int buffer, int windowTime){}
}

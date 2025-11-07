package dev.sayaya.rx.subject;

import dev.sayaya.rx.Observable;
import dev.sayaya.rx.Observer;
import elemental2.core.JsError;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * Subject는 Observable의 특수 유형으로, 값을 여러 Observer에 멀티캐스트할 수 있습니다.
 * Subject는 EventEmitters와 같습니다.
 * @link <a href="https://rxjs.dev/api/index/class/Subject">Guide</a>
 */
@JsType(isNative = true, namespace="rxjs", name="Subject")
public class Subject<T> extends Observable<T> implements Observer<T> {
    @JsConstructor
    protected Subject(){}
    @Override public native void next(T value);
    @Override public native void error(JsError error);
    @Override public native void complete();
    public native Observable<T> asObservable();

    @JsOverlay
    public static <T> Subject<T> subject(Class<T> clazz) {
        return new Subject<>();
    }
}

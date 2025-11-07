package dev.sayaya.rx.subject;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import dev.sayaya.rx.Observable;

/**
 * 완료될 때만 값을 방출하는 Subject의 변형입니다. 완료 시 모든 관찰자에게 최신 값을 방출합니다.
 * @link <a href="https://rxjs.dev/api/index/class/AsyncSubject">Guide</a>
 */
@JsType(isNative = true, namespace="rxjs", name="AsyncSubject")
public class AsyncSubject<T> extends Subject<T> {
    @JsConstructor
    protected AsyncSubject(){}
    @JsOverlay
    public static <T> AsyncSubject<T> async(Class<T> clazz) {
        return new AsyncSubject<>();
    }
    @JsOverlay
    public static <T> AsyncSubject<T> await(Promise<T> promise) {
        AsyncSubject<T> subject = new AsyncSubject<>();
        Observable<T> observable = Observable.from(promise);
        observable.subscribe(subject);
        return subject;
    }
    @JsOverlay
    public static <T> AsyncSubject<T> json(Promise<Response> promise) {
        AsyncSubject<T> subject = new AsyncSubject<>();
        Observable<T> observable = Observable.from(promise.then(Response::json));
        observable.subscribe(subject);
        return subject;
    }
}

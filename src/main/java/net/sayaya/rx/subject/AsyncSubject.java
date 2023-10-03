package net.sayaya.rx.subject;

import elemental2.dom.Response;
import elemental2.promise.Promise;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import net.sayaya.rx.Observable;

@JsType(isNative = true, namespace="rxjs", name="AsyncSubject")
public class AsyncSubject<T> extends SubjectJs<T> {
    @JsOverlay
    public static <T> Observable<T> await(Promise<T> promise) {
        AsyncSubject<T> subject = new AsyncSubject<>();
        Observable<T> observable = Observable.from(promise);
        observable.subscribe(subject);
        return subject.asObservable();
    }
    @JsOverlay
    public static <T> Observable<T> json(Promise<Response> promise) {
        AsyncSubject<T> subject = new AsyncSubject<>();
        Observable<T> observable = Observable.from(promise.then(Response::json));
        observable.subscribe(subject);
        return subject.asObservable();
    }
}

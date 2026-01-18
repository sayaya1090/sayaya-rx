package dev.sayaya.rx;

import com.google.gwt.core.client.JavaScriptObject;
import dev.sayaya.rx.function.Callback;
import dev.sayaya.rx.function.OperatorFunction;
import dev.sayaya.rx.scheduler.Scheduler;
import elemental2.core.JsError;
import elemental2.core.JsNumber;
import elemental2.dom.*;
import jsinterop.annotations.*;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import org.jboss.elemento.EventType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.*;

import static dev.sayaya.rx.Observer.next;
import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * Observables are lazy Push collections of multiple values
 * @see <a href="https://rxjs.dev/guide/observable">Guide</a>
 */
@JsType(isNative = true, namespace="rxjs", name="Observable")
public class Observable<T> {
    /**
     * 인수를 관찰 가능한 시퀀스로 변환합니다.
     * @see <a href="https://rxjs.dev/api/index/function/of">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="of")
    public static native <T> Observable<T> of(T... values);

    /**
     * 지정된 범위 내에서 숫자 시퀀스를 방출하는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/range">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="range")
    private static native Observable<JsNumber> range(JsNumber start, JsNumber count);
    @JsOverlay public static Observable<Integer> range(int start) {
        return range(new JsNumber(start),null).map(i->(int)i.valueOf());
    }
    @JsOverlay public static Observable<Integer> range(int start, Integer count) {
        return range(new JsNumber(start), count!=null? new JsNumber(count): null).map(i->(int)i.valueOf());
    }

    /**
     * 배열, 배열과 유사한 객체, Promise, 반복 가능한 객체 또는 Observable과 유사한 객체에서 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/from">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="from")
    public static native <T> Observable<T> from(Object... any);
    /**
     * 주어진 모든 입력 Observable에서 방출되는 값을 동시에 내보내는 출력 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/merge">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="merge")
    public static native <T> Observable<T> merge(Observable<T>... observables);
    /**
     * ObservableInput의 배열을 인자로 받아, 동일한 순서로 값을 담은 배열을 발행하는 Observable을 반환합니다.
     * @see <a href="https://rxjs.dev/api/index/function/forkJoin">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="forkJoin")
    public static native Observable<Object[]> forkJoin(Observable<?>... observables);

    /**
     * 지정된 이벤트 대상에서 발생하는 특정 유형의 이벤트를 방출하는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/fromEvent">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="fromEvent")
    public static native Observable<Event> fromEvent(Node target, String eventName);
    @JsOverlay public static Observable<MouseEvent> fromMouseEvent(Node target, EventType<MouseEvent, Element> evt) {
        return fromEvent(target, evt.name).map(e->(MouseEvent)e);
    }
    @JsOverlay public static Observable<KeyboardEvent> fromKeyboardEvent(Node target, EventType<KeyboardEvent, Element> evt) {
        return fromEvent(target, evt.name).map(e->(KeyboardEvent)e);
    }
    /**
     * 지정된 스케줄러(SchedulerLike)에서 지정된 시간 간격마다 연속적인 숫자를 방출하는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/interval">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="interval")
    public static native Observable<Integer> interval(Integer period);
    @JsMethod(namespace="rxjs", name="interval")
    public static native Observable<Integer> interval();
    @JsMethod(namespace="rxjs", name="timer")
    private static native Observable<JsNumber> timer(JsNumber start, JsNumber delay);
    @JsOverlay public static Observable<Integer> timer(int start, int delay) {
        return timer(new JsNumber(start), new JsNumber(delay)).map(i->(int)i.valueOf());
    }

    @JsType(isNative = true, namespace = GLOBAL, name = "Object")
    // @Builder
    public static class AjaxRequest {
        public String url;
        public Object body;
        public String method;
        public Boolean async;
        public Map<String, String> headers;
        public Double timeout;
        public Boolean crossDomain;
    }
    @JsType(isNative = true, namespace = GLOBAL, name = "Object")
    public static class AjaxResponse<T> {
        public AjaxRequest request;
        public String type;
        public int status;
        public String responseType;
        @JsProperty(name="responseHeaders")
        public Map<String, String> headers;
        public T response;
    }
    @JsMethod(namespace="rxjs.ajax", name="ajax")
    public static native Observable<AjaxResponse<?>> fromRequest(AjaxRequest request);
    @JsOverlay public static <T> Observable<AjaxResponse<T>> fromRequest(AjaxRequest request, Class<T> clazz) {
        return fromRequest(request).map(Js::cast);
    }
    @JsMethod(namespace="rxjs.ajax.ajax", name="getJSON")
    public static native Observable<JavaScriptObject> fetchJson(String url);
    @JsOverlay public static <T> Observable<T> fetchJson(String url, Class<T> clazz) {
        return fetchJson(url).map(Js::cast);
    }

    protected Observable(){}
    public native Subscription subscribe(Observer<T> observer);
    @JsOverlay public final Subscription subscribe(Consumer<T> consumer) {return subscribe(next(consumer)); }
    public native <V> Observable<V> pipe(OperatorFunction<T, V> func);
    @JsOverlay public final <V> Observable<V> map(Function<T, V> func) { return pipe(Operator.map(func)); }
    @JsOverlay public final <V> Observable<V> mergeMap(Function<? super T, ? extends Observable<? extends V>> func) { return pipe(Operator.mergeMap(func)); }
    @JsOverlay public final <V> Observable<V> mergeMap(Observable<? extends V> target) { return pipe(Operator.mergeMapTo(target)); }
    @JsOverlay public final <V> Observable<V> concatMap(Function<? super T, ? extends Observable<? extends V>> func) { return pipe(Operator.concatMap(func)); }
    @JsOverlay public final <V> Observable<V> concatMap(Observable<? extends V> target) { return pipe(Operator.concatMapTo(target)); }
    @SafeVarargs @JsOverlay public final Observable<T> concatWith(Observable<T>... observers) { return pipe(Operator.concatWith(observers)); }
    @JsOverlay public final <V> Observable<V> switchMap(Function<? super T, ? extends Observable<? extends V>> func) { return pipe(Operator.switchMap(func)); }
    @JsOverlay public final <V> Observable<V> switchMap(Observable<? extends V> target) { return pipe(Operator.switchMapTo(target)); }
    @JsOverlay public final Observable<T> tap(Consumer<T> consumer) { return pipe(Operator.tap(consumer::accept)); }
    @JsOverlay public final Observable<T> tap(Observer<T> observer) { return pipe(Operator.tap(observer)); }
    @JsOverlay public final Observable<List<T>> zip(Observable<? extends T> observable) {
        return Operator.zip(this, observable).map(JsArrayLike::asList);
    }
    @JsOverlay public final Observable<Object[]> combineLatest(Observable<? > observable) {
        return Operator.combineLatest(this, observable).map(Js::uncheckedCast);
    }
    @JsOverlay public final Observable<T[]> bufferTime(int bufferTimeSpan) {
        return pipe(Operator.bufferTime(bufferTimeSpan));
    }
    @JsOverlay public final Observable<T[]> bufferTime(int bufferTimeSpan, int bufferCreationInterval) {
        return pipe(Operator.bufferTime(bufferTimeSpan, bufferCreationInterval));
    }
    @JsOverlay public final Observable<T[]> bufferTime(int bufferTimeSpan, int bufferCreationInterval, int maxBufferSize) {
        return pipe(Operator.bufferTime(bufferTimeSpan, bufferCreationInterval, maxBufferSize));
    }
    @JsOverlay public final Observable<Observable<T>> windowTime(int bufferTimeSpan) {
        return pipe(Operator.windowTime(bufferTimeSpan));
    }
    @JsOverlay public final Observable<Observable<T>> windowTime(int bufferTimeSpan, int bufferCreationInterval) {
        return pipe(Operator.windowTime(bufferTimeSpan, bufferCreationInterval));
    }
    @JsOverlay public final Observable<Observable<T>> windowTime(int bufferTimeSpan, int bufferCreationInterval, int maxBufferSize) {
        return pipe(Operator.windowTime(bufferTimeSpan, bufferCreationInterval, maxBufferSize));
    }
    @JsOverlay public final Observable<T> filter(Predicate<T> predicate) { return pipe(Operator.filter(predicate)); }
    @JsOverlay public final Observable<T> take(int count) { return pipe(Operator.take(count)); }
    @JsOverlay public final Observable<T> skip(int count) { return pipe(Operator.skip(count)); }
    @JsOverlay public final Observable<T> retry(int count) { return pipe(Operator.retry(count)); }
    @JsOverlay public final Observable<T> scan(BiFunction<T, T, T> func) { return pipe(Operator.scan(func)); }
    @JsOverlay public final Observable<T> debounce(Function<T, Observable<?>> durationSelector) { return pipe(Operator.debounce(durationSelector::apply)); }
    @JsOverlay public final Observable<T> debounceTime(int milliseconds) { return pipe(Operator.debounceTime(new JsNumber(milliseconds))); }
    @JsOverlay public final Observable<T> throttleTime(int milliseconds) { return pipe(Operator.throttleTime(new JsNumber(milliseconds), Scheduler.async())); }
    @JsOverlay public final Observable<T> distinct() { return pipe(Operator.distinct()); }
    @JsOverlay public final Observable<T> distinct(Function<T, ?> selector) { return pipe(Operator.distinct(selector::apply)); }
    @JsOverlay public final Observable<T> distinctUntilChanged() { return pipe(Operator.distinctUntilChanged()); }
    @JsOverlay public final Observable<T> distinctUntilChanged(BiFunction<T, T, Boolean> comparator) { return pipe(Operator.distinctUntilChanged(comparator::apply)); }
    @JsOverlay public final Observable<T> distinctUntilChanged(Comparator<T> comparator) { return distinctUntilChanged((BiFunction<T, T, Boolean>) (a, b)->comparator.compare(a, b)==0); }
    @JsOverlay @SafeVarargs public final Observable<T> startWith(T... values) { return pipe(Operator.startWith(values)); }
    @JsOverlay public final <E> Observable<E> catchError(BiFunction<JsError, Observable<? super T>, Observable<? super E>> func) { return pipe(Operator.catchError((e, s) -> func.apply(e, s))); }
    @JsOverlay public final <E> Observable<E> catchError(Function<JsError, Observable<? super E>> func) { return pipe(Operator.catchError((e, s) -> func.apply(e))); }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> concatAll(Class<V> clazz) {
        Observable<O> cast = (Observable<O>) this;
        return cast.pipe(Operator.concatAll());
    }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> switchAll(Class<V> clazz) {
        Observable<O> cast = (Observable<O>) this;
        return cast.pipe(Operator.switchAll());
    }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> mergeAll(Class<V> clazz) {
        Observable<O> cast = (Observable<O>) this;
        return cast.pipe(Operator.mergeAll());
    }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> exhaustAll(Class<V> clazz) {
        Observable<O> cast = (Observable<O>) this;
        return cast.pipe(Operator.exhaustAll());
    }
    @JsOverlay public final Observable<T> finalize(Callback callback) {
        return pipe(Operator.finalize(callback));
    }
}
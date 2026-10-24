package dev.sayaya.rx;

import com.google.gwt.core.client.JavaScriptObject;
import dev.sayaya.rx.function.Callback;
import dev.sayaya.rx.function.Factory;
import dev.sayaya.rx.function.OperatorFunction;
import dev.sayaya.rx.function.VoidFunction;
import dev.sayaya.rx.scheduler.Scheduler;
import elemental2.core.JsArray;
import elemental2.core.JsError;
import elemental2.core.JsNumber;
import elemental2.dom.*;
import jsinterop.annotations.*;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;
import org.jboss.elemento.EventType;

import java.util.Comparator;
import java.util.List;
import java.util.function.*;

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
    public static native <T> Observable<T> from(Object input);
    @JsOverlay public static <T> Observable<T> from(List<T> list) {
        return from((Object) list.toArray());
    }
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
    public static native Observable<Event> fromEvent(EventTarget target, String eventName);
    @JsOverlay public static Observable<MouseEvent> fromMouseEvent(EventTarget target, EventType<MouseEvent, Element> evt) {
        return fromEvent(target, evt.name).map(e->(MouseEvent)e);
    }
    @JsOverlay public static Observable<KeyboardEvent> fromKeyboardEvent(EventTarget target, EventType<KeyboardEvent, Element> evt) {
        return fromEvent(target, evt.name).map(e->(KeyboardEvent)e);
    }
    /**
     * 지정된 스케줄러(SchedulerLike)에서 지정된 시간 간격마다 연속적인 숫자를 방출하는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/interval">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="interval")
    private static native Observable<JsNumber> interval(JsNumber period);
    @JsOverlay public static Observable<Integer> interval(int period) {
        return interval(new JsNumber(period)).map(i->(int)i.valueOf());
    }
    @JsOverlay public static Observable<Integer> interval() {
        return interval(new JsNumber(0)).map(i->(int)i.valueOf());
    }
    @JsMethod(namespace="rxjs", name="timer")
    private static native Observable<JsNumber> timer(JsNumber start, JsNumber delay);
    @JsOverlay public static Observable<Integer> timer(int start, int delay) {
        return timer(new JsNumber(start), new JsNumber(delay)).map(i->(int)i.valueOf());
    }
    @JsMethod(namespace="rxjs", name="timer")
    private static native Observable<JsNumber> timer(JsNumber due);
    /**
     * due(ms) 후에 0을 한 번 방출하고 완료합니다.
     * @see <a href="https://rxjs.dev/api/index/function/timer">Guide</a>
     */
    @JsOverlay public static Observable<Integer> timer(int due) {
        return timer(new JsNumber(due)).map(i->(int)i.valueOf());
    }
    /**
     * 입력 Observable들을 순서대로 구독해, 앞의 것이 완료되면 다음 것을 이어 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/concat">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="concat")
    public static native <T> Observable<T> concat(Observable<? extends T>... observables);
    /**
     * 구독할 때마다 factory를 호출해 새 Observable을 만듭니다.
     * @see <a href="https://rxjs.dev/api/index/function/defer">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="defer")
    public static native <T> Observable<T> defer(Factory<Observable<T>> factory);
    /**
     * 아무 값도 방출하지 않고 즉시 완료하는 Observable(rxjs.EMPTY)입니다.
     * @see <a href="https://rxjs.dev/api/index/const/EMPTY">Guide</a>
     */
    @JsProperty(namespace="rxjs", name="EMPTY")
    public static native <T> Observable<T> empty();
    @JsMethod(namespace="rxjs", name="throwError")
    private static native <T> Observable<T> throwErrorWith(Factory<Object> errorFactory);
    /**
     * 구독 즉시 주어진 에러를 방출하는 Observable을 만듭니다.
     * @see <a href="https://rxjs.dev/api/index/function/throwError">Guide</a>
     */
    @JsOverlay public static <T> Observable<T> throwError(JsError error) {
        return throwErrorWith(() -> error);
    }

    @JsType(isNative = true, namespace = GLOBAL, name = "Object")
    // @Builder
    public static class AjaxRequest {
        public String url;
        public Object body;
        public String method;
        public Boolean async;
        public JsPropertyMap<String> headers;
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
        public JsPropertyMap<String> headers;
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
    @JsMethod(name="subscribe")
    private native Subscription subscribeNext(VoidFunction<T> next);
    /**
     * next 콜백만으로 구독합니다. 에러 콜백이 없으므로, 스트림 에러는 RxJS 기본 동작대로
     * 처리되지 않은 에러로 보고됩니다(rxjs config.onUnhandledError 또는 비동기 throw).
     */
    @JsOverlay public final Subscription subscribe(Consumer<T> consumer) { return subscribeNext(consumer::accept); }
    /**
     * next와 error 콜백으로 구독합니다.
     */
    @JsOverlay public final Subscription subscribe(Consumer<T> onNext, Consumer<JsError> onError) {
        return subscribe(onNext, onError, () -> {});
    }
    /**
     * next, error, complete 콜백으로 구독합니다.
     */
    @JsOverlay public final Subscription subscribe(Consumer<T> onNext, Consumer<JsError> onError, Callback onComplete) {
        return subscribe(new Observer<T>() {
            public void next(T value) { onNext.accept(value); }
            public void error(JsError error) { onError.accept(error); }
            public void complete() { onComplete.call(); }
        });
    }
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
    /**
     * 시드 값(seed)부터 누적합니다. 누적 결과의 타입은 소스 값 타입과 달라도 됩니다.
     */
    @JsOverlay public final <A> Observable<A> scan(BiFunction<A, T, A> func, A seed) { return pipe(Operator.<T, A>scan((a, v) -> func.apply(a, v), seed)); }
    @JsOverlay public final Observable<T> debounce(Function<T, Observable<?>> durationSelector) { return pipe(Operator.debounce(durationSelector::apply)); }
    @JsOverlay public final Observable<T> debounceTime(int milliseconds) { return pipe(Operator.debounceTime(new JsNumber(milliseconds))); }
    @JsOverlay public final Observable<T> throttleTime(int milliseconds) { return pipe(Operator.throttleTime(new JsNumber(milliseconds), Scheduler.async())); }
    /**
     * 지정한 스케줄러로 throttleTime을 적용합니다.
     */
    @JsOverlay public final Observable<T> throttleTime(int milliseconds, dev.sayaya.rx.scheduler.AsyncScheduler<T> scheduler) { return pipe(Operator.throttleTime(new JsNumber(milliseconds), scheduler)); }
    /**
     * leading/trailing 설정으로 throttleTime을 적용합니다. (RxJS ThrottleConfig)
     */
    @JsOverlay public final Observable<T> throttleTime(int milliseconds, boolean leading, boolean trailing) {
        return pipe(Operator.throttleTime(new JsNumber(milliseconds), Scheduler.async(), Operator.throttleConfig(leading, trailing)));
    }
    @JsOverlay public final Observable<T> distinct() { return pipe(Operator.distinct()); }
    @JsOverlay public final Observable<T> distinct(Function<T, ?> selector) { return pipe(Operator.distinct(selector::apply)); }
    @JsOverlay public final Observable<T> distinctUntilChanged() { return pipe(Operator.distinctUntilChanged()); }
    @JsOverlay public final Observable<T> distinctUntilChanged(BiFunction<T, T, Boolean> comparator) { return pipe(Operator.distinctUntilChanged(comparator::apply)); }
    @JsOverlay public final Observable<T> distinctUntilChanged(Comparator<T> comparator) { return distinctUntilChanged((BiFunction<T, T, Boolean>) (a, b)->comparator.compare(a, b)==0); }
    @JsOverlay @SafeVarargs public final Observable<T> startWith(T... values) { return pipe(Operator.startWith(values)); }
    @JsOverlay public final <E> Observable<E> catchError(BiFunction<JsError, Observable<T>, Observable<? extends E>> func) { return pipe(Operator.<T, E>catchError((e, s) -> func.apply(e, s))); }
    @JsOverlay public final <E> Observable<E> catchError(Function<JsError, Observable<? extends E>> func) { return pipe(Operator.<T, E>catchError((e, s) -> func.apply(e))); }
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
    @JsOverlay public final Observable<T> takeUntil(Observable<?> notifier) { return pipe(Operator.takeUntil(notifier)); }
    @JsOverlay public final Observable<T> takeWhile(Predicate<T> predicate) { return pipe(Operator.takeWhile(predicate)); }
    @JsOverlay public final Observable<T> first() { return pipe(Operator.first()); }
    @JsOverlay public final Observable<T> first(Predicate<T> predicate) { return pipe(Operator.first(predicate)); }
    @JsOverlay public final Observable<T> last() { return pipe(Operator.last()); }
    @JsOverlay public final Observable<T> last(Predicate<T> predicate) { return pipe(Operator.last(predicate)); }
    @JsOverlay public final Observable<T> reduce(BiFunction<T, T, T> accumulator) { return pipe(Operator.<T>reduce((a, v) -> accumulator.apply(a, v))); }
    @JsOverlay public final <A> Observable<A> reduce(BiFunction<A, T, A> accumulator, A seed) { return pipe(Operator.<T, A>reduce((a, v) -> accumulator.apply(a, v), seed)); }
    @JsOverlay public final Observable<T> delay(int milliseconds) { return pipe(Operator.delay(milliseconds)); }
    @JsOverlay public final Observable<T> share() { return pipe(Operator.share()); }
    @JsOverlay public final Observable<T> shareReplay(int bufferSize) { return pipe(Operator.shareReplay(bufferSize)); }
    @JsOverlay public final Observable<Object[]> withLatestFrom(Observable<?> other) {
        return pipe(Operator.<T>withLatestFrom(other)).map(Js::<Object[]>uncheckedCast);
    }
    @JsOverlay public final <V> Observable<V> exhaustMap(Function<? super T, ? extends Observable<? extends V>> func) { return pipe(Operator.exhaustMap(func)); }
    @JsOverlay public final Observable<List<T>> toArray() {
        return pipe(Operator.<T>toArray()).map(JsArrayLike::asList);
    }
    @JsOverlay public final Observable<List<T>> pairwise() {
        return pipe(Operator.<T>pairwise()).map(JsArrayLike::asList);
    }
    @JsOverlay public final Observable<T> finalize(Callback callback) {
        return pipe(Operator.finalize(callback));
    }
}
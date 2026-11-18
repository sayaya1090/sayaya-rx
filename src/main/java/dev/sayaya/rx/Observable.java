package dev.sayaya.rx;

import com.google.gwt.core.client.JavaScriptObject;
import dev.sayaya.rx.function.Callback;
import dev.sayaya.rx.function.Factory;
import dev.sayaya.rx.function.OperatorFunction;
import dev.sayaya.rx.function.PredicateWithIndex;
import dev.sayaya.rx.function.RetryDelay;
import dev.sayaya.rx.function.UnaryFunction;
import dev.sayaya.rx.function.VoidFunction;
import dev.sayaya.rx.scheduler.AsyncScheduler;
import dev.sayaya.rx.scheduler.Scheduler;
import dev.sayaya.rx.scheduler.TaskScheduler;
import elemental2.core.JsArray;
import elemental2.core.JsError;
import elemental2.core.JsNumber;
import elemental2.dom.*;
import jsinterop.annotations.*;
import jsinterop.base.Js;
import jsinterop.base.JsArrayLike;
import jsinterop.base.JsPropertyMap;
import elemental2.promise.Promise;
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
        return toInt(range(new JsNumber(start), null));
    }
    @JsOverlay public static Observable<Integer> range(int start, Integer count) {
        return toInt(range(new JsNumber(start), count!=null? new JsNumber(count): null));
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
     * 모든 입력 Observable이 완료되면 각자의 마지막 값을 입력 순서대로 담은 List를 한 번 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/forkJoin">Guide</a>
     */
    @JsOverlay public static Observable<List<Object>> forkJoin(Observable<?>... observables) {
        return forkJoinArray(observables).map(JsArrayLike::asList);
    }
    @JsMethod(namespace="rxjs", name="forkJoin")
    private static native Observable<elemental2.core.JsArray<Object>> forkJoinArray(Observable<?>... observables);

    /**
     * 지정된 이벤트 대상에서 발생하는 특정 유형의 이벤트를 방출하는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/fromEvent">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="fromEvent")
    public static native Observable<Event> fromEvent(EventTarget target, String eventName);
    @JsOverlay public static Observable<MouseEvent> fromMouseEvent(EventTarget target, EventType<MouseEvent, ?> evt) {
        return fromEvent(target, evt.name).map(e->(MouseEvent)e);
    }
    @JsOverlay public static Observable<KeyboardEvent> fromKeyboardEvent(EventTarget target, EventType<KeyboardEvent, ?> evt) {
        return fromEvent(target, evt.name).map(e->(KeyboardEvent)e);
    }
    /**
     * 지정된 스케줄러(SchedulerLike)에서 지정된 시간 간격마다 연속적인 숫자를 방출하는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/interval">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="interval")
    private static native Observable<JsNumber> interval(JsNumber period);
    @JsOverlay public static Observable<Integer> interval(int period) {
        return toInt(interval(new JsNumber(period)));
    }
    @JsOverlay public static Observable<Integer> interval() {
        return interval(0);
    }
    @JsMethod(namespace="rxjs", name="timer")
    private static native Observable<JsNumber> timer(JsNumber start, JsNumber delay);
    @JsOverlay public static Observable<Integer> timer(int start, int delay) {
        return toInt(timer(new JsNumber(start), new JsNumber(delay)));
    }
    @JsMethod(namespace="rxjs", name="timer")
    private static native Observable<JsNumber> timer(JsNumber due);
    /**
     * due(ms) 후에 0을 한 번 방출하고 완료합니다.
     * @see <a href="https://rxjs.dev/api/index/function/timer">Guide</a>
     */
    @JsOverlay public static Observable<Integer> timer(int due) {
        return toInt(timer(new JsNumber(due)));
    }
    /** RxJS가 방출하는 JS number를 Java int로 바꿉니다. */
    @JsOverlay private static Observable<Integer> toInt(Observable<JsNumber> numbers) {
        return numbers.map(i->(int)i.valueOf());
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

    // ===== RxJS 7 생성 함수 =====
    /**
     * 아무 값도 방출하지 않고 완료하지도 않는 Observable(rxjs.NEVER)입니다.
     * @see <a href="https://rxjs.dev/api/index/const/NEVER">Guide</a>
     */
    @JsProperty(namespace="rxjs", name="NEVER")
    public static native <T> Observable<T> never();
    /**
     * 가장 먼저 값을 방출한 소스만 미러링합니다.
     * @see <a href="https://rxjs.dev/api/index/function/race">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="race")
    public static native <T> Observable<T> race(Observable<? extends T>... sources);
    @JsMethod(namespace="rxjs", name="iif")
    private static native <T> Observable<T> iifNative(Factory<Boolean> condition, Observable<? extends T> trueResult, Observable<? extends T> falseResult);
    /**
     * 구독 시점에 condition을 평가해 trueResult 또는 falseResult를 구독합니다.
     * @see <a href="https://rxjs.dev/api/index/function/iif">Guide</a>
     */
    @JsOverlay public static <T> Observable<T> iif(BooleanSupplier condition, Observable<? extends T> trueResult, Observable<? extends T> falseResult) {
        return iifNative(condition::getAsBoolean, trueResult, falseResult);
    }
    @JsMethod(namespace="rxjs", name="generate")
    private static native <S> Observable<S> generateNative(JsPropertyMap<Object> options);
    /**
     * initialState부터 condition이 참인 동안 iterate로 다음 상태를 만들며 각 상태를 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/generate">Guide</a>
     */
    @JsOverlay public static <S> Observable<S> generate(S initialState, Predicate<S> condition, UnaryOperator<S> iterate) {
        JsPropertyMap<Object> options = JsPropertyMap.of();
        options.set("initialState", initialState);
        options.set("condition", (UnaryFunction<S, Boolean>) condition::test);
        options.set("iterate", (UnaryFunction<S, S>) iterate::apply);
        return generateNative(options);
    }
    /**
     * connect()를 호출해야 소스를 구독하는 멀티캐스트 Observable을 만듭니다(기본 connector는 Subject).
     * @see <a href="https://rxjs.dev/api/index/function/connectable">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="connectable")
    public static native <T> Connectable<T> connectable(Observable<T> source);
    @JsMethod(namespace="rxjs", name="connectable")
    private static native <T> Connectable<T> connectableNative(Observable<T> source, JsPropertyMap<Object> config);
    /**
     * connector(예: ReplaySubject::replay)로 멀티캐스트할 Subject를 지정합니다.
     */
    @JsOverlay public static <T> Connectable<T> connectable(Observable<T> source, Supplier<? extends Observer<T>> connector, boolean resetOnDisconnect) {
        JsPropertyMap<Object> config = JsPropertyMap.of();
        config.set("connector", (Factory<Object>) connector::get);
        config.set("resetOnDisconnect", resetOnDisconnect);
        return connectableNative(source, config);
    }
    @JsMethod(namespace="rxjs", name="using")
    private static native <T> Observable<T> usingNative(Factory<Subscription> resourceFactory, UnaryFunction<Subscription, Observable<T>> observableFactory);
    /**
     * 구독할 때 자원을 만들고, 스트림이 끝나거나 구독을 해제하면 자원을 해제합니다.
     * @see <a href="https://rxjs.dev/api/index/function/using">Guide</a>
     */
    @JsOverlay public static <T> Observable<T> using(Supplier<Subscription> resourceFactory, Function<Subscription, Observable<T>> observableFactory) {
        return usingNative(resourceFactory::get, observableFactory::apply);
    }
    @JsMethod(namespace="rxjs", name="scheduled")
    private static native <T> Observable<T> scheduledNative(Object input, Object scheduler);
    /**
     * input을 지정한 스케줄러에서 구독·방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/scheduled">Guide</a>
     */
    @JsOverlay public static <T> Observable<T> scheduled(Observable<T> input, AsyncScheduler<?> scheduler) { return scheduledNative(input, scheduler); }
    @JsOverlay public static <T> Observable<T> scheduled(Observable<T> input, TaskScheduler scheduler) { return scheduledNative(input, scheduler); }
    @JsOverlay public static <T> Observable<T> scheduled(List<T> input, AsyncScheduler<?> scheduler) { return scheduledNative(input.toArray(), scheduler); }
    @JsOverlay public static <T> Observable<T> scheduled(List<T> input, TaskScheduler scheduler) { return scheduledNative(input.toArray(), scheduler); }
    @JsMethod(namespace="rxjs", name="fromEventPattern")
    private static native <T> Observable<T> fromEventPatternNative(VoidFunction<VoidFunction<T>> addHandler, VoidFunction<VoidFunction<T>> removeHandler);
    /**
     * addHandler/removeHandler 형태의 이벤트 API를 Observable로 감쌉니다.
     * @see <a href="https://rxjs.dev/api/index/function/fromEventPattern">Guide</a>
     */
    @JsOverlay public static <T> Observable<T> fromEventPattern(Consumer<VoidFunction<T>> addHandler, Consumer<VoidFunction<T>> removeHandler) {
        return fromEventPatternNative(addHandler::accept, removeHandler::accept);
    }
    @JsMethod(namespace="rxjs", name="partition")
    private static native <T> JsArray<Observable<T>> partitionNative(Observable<T> source, PredicateWithIndex<T> predicate);
    /**
     * 소스를 predicate를 만족하는 값(get(0))과 만족하지 않는 값(get(1))의 두 Observable로 나눕니다.
     * @see <a href="https://rxjs.dev/api/index/function/partition">Guide</a>
     */
    @JsOverlay public static <T> List<Observable<T>> partition(Observable<T> source, Predicate<T> predicate) {
        return partitionNative(source, (v, i) -> predicate.test(v)).asList();
    }
    /**
     * 첫 값으로 resolve되는 Promise를 돌려줍니다. 값 없이 완료되면 reject(EmptyError)됩니다.
     * @see <a href="https://rxjs.dev/api/index/function/firstValueFrom">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="firstValueFrom")
    public static native <T> Promise<T> firstValueFrom(Observable<T> source);
    @JsMethod(namespace="rxjs", name="firstValueFrom")
    private static native <T> Promise<T> firstValueFromNative(Observable<T> source, JsPropertyMap<Object> config);
    /** 값 없이 완료되면 defaultValue로 resolve됩니다. */
    @JsOverlay public static <T> Promise<T> firstValueFrom(Observable<T> source, T defaultValue) {
        return firstValueFromNative(source, JsPropertyMap.of("defaultValue", defaultValue));
    }
    /**
     * 완료 시 마지막 값으로 resolve되는 Promise를 돌려줍니다. 값 없이 완료되면 reject(EmptyError)됩니다.
     * @see <a href="https://rxjs.dev/api/index/function/lastValueFrom">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="lastValueFrom")
    public static native <T> Promise<T> lastValueFrom(Observable<T> source);
    @JsMethod(namespace="rxjs", name="lastValueFrom")
    private static native <T> Promise<T> lastValueFromNative(Observable<T> source, JsPropertyMap<Object> config);
    /** 값 없이 완료되면 defaultValue로 resolve됩니다. */
    @JsOverlay public static <T> Promise<T> lastValueFrom(Observable<T> source, T defaultValue) {
        return lastValueFromNative(source, JsPropertyMap.of("defaultValue", defaultValue));
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
    @JsOverlay public final <V> Observable<V> mergeMap(Observable<? extends V> target) { return pipe(Operator.<T, V>mergeMap((Function<? super T, ? extends Observable<? extends V>>) v -> target)); }
    @JsOverlay public final <V> Observable<V> concatMap(Function<? super T, ? extends Observable<? extends V>> func) { return pipe(Operator.concatMap(func)); }
    @JsOverlay public final <V> Observable<V> concatMap(Observable<? extends V> target) { return pipe(Operator.<T, V>concatMap((Function<? super T, ? extends Observable<? extends V>>) v -> target)); }
    @SafeVarargs @JsOverlay public final Observable<T> concatWith(Observable<T>... observers) { return pipe(Operator.concatWith(observers)); }
    @JsOverlay public final <V> Observable<V> switchMap(Function<? super T, ? extends Observable<? extends V>> func) { return pipe(Operator.switchMap(func)); }
    @JsOverlay public final <V> Observable<V> switchMap(Observable<? extends V> target) { return pipe(Operator.<T, V>switchMap((Function<? super T, ? extends Observable<? extends V>>) v -> target)); }
    @JsOverlay public final Observable<T> tap(Consumer<T> consumer) { return pipe(Operator.tap(consumer::accept)); }
    @JsOverlay public final Observable<T> tap(Observer<T> observer) { return pipe(Operator.tap(observer)); }
    @JsOverlay public final Observable<List<T>> zip(Observable<? extends T> observable) {
        return Operator.zip(this, observable).map(JsArrayLike::asList);
    }
    @JsOverlay public final Observable<List<Object>> combineLatest(Observable<?> observable) {
        return Operator.combineLatest(this, observable).map(arr -> Js.<elemental2.core.JsArray<Object>>uncheckedCast(arr).asList());
    }
    @JsOverlay public final Observable<List<T>> bufferTime(int bufferTimeSpan) {
        return pipe(Operator.<T>bufferTime(bufferTimeSpan)).map(JsArrayLike::asList);
    }
    @JsOverlay public final Observable<List<T>> bufferTime(int bufferTimeSpan, int bufferCreationInterval) {
        return pipe(Operator.<T>bufferTime(bufferTimeSpan, bufferCreationInterval)).map(JsArrayLike::asList);
    }
    @JsOverlay public final Observable<List<T>> bufferTime(int bufferTimeSpan, int bufferCreationInterval, int maxBufferSize) {
        return pipe(Operator.<T>bufferTime(bufferTimeSpan, bufferCreationInterval, maxBufferSize)).map(JsArrayLike::asList);
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
    @JsOverlay public final Observable<T> debounceTime(int milliseconds) { return pipe(Operator.debounceTime(milliseconds)); }
    @JsOverlay public final Observable<T> throttleTime(int milliseconds) { return pipe(Operator.throttleTime(milliseconds, Scheduler.async())); }
    /**
     * 지정한 스케줄러로 throttleTime을 적용합니다.
     */
    @JsOverlay public final Observable<T> throttleTime(int milliseconds, dev.sayaya.rx.scheduler.AsyncScheduler<?> scheduler) { return pipe(Operator.throttleTime(milliseconds, scheduler)); }
    /**
     * leading/trailing 설정으로 throttleTime을 적용합니다. (RxJS ThrottleConfig)
     */
    @JsOverlay public final Observable<T> throttleTime(int milliseconds, boolean leading, boolean trailing) {
        return pipe(Operator.throttleTime(milliseconds, Scheduler.async(), Operator.throttleConfig(leading, trailing)));
    }
    @JsOverlay public final Observable<T> distinct() { return pipe(Operator.distinct()); }
    @JsOverlay public final Observable<T> distinct(Function<T, ?> selector) { return pipe(Operator.distinct(selector::apply)); }
    @JsOverlay public final Observable<T> distinctUntilChanged() { return pipe(Operator.distinctUntilChanged()); }
    @JsOverlay public final Observable<T> distinctUntilChanged(BiFunction<T, T, Boolean> comparator) { return pipe(Operator.distinctUntilChanged(comparator::apply)); }
    @JsOverlay public final Observable<T> distinctUntilChanged(Comparator<T> comparator) { return distinctUntilChanged((BiFunction<T, T, Boolean>) (a, b)->comparator.compare(a, b)==0); }
    @JsOverlay @SafeVarargs public final Observable<T> startWith(T... values) { return pipe(Operator.startWith(values)); }
    @JsOverlay public final <E> Observable<E> catchError(BiFunction<JsError, Observable<T>, Observable<? extends E>> func) { return pipe(Operator.<T, E>catchError((e, s) -> func.apply(e, s))); }
    @JsOverlay public final <E> Observable<E> catchError(Function<JsError, Observable<? extends E>> func) { return pipe(Operator.<T, E>catchError((e, s) -> func.apply(e))); }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> concatAll(Class<V> clazz) { return this.<V, O>flatten(Operator.<V, O>concatAll()); }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> switchAll(Class<V> clazz) { return this.<V, O>flatten(Operator.<V, O>switchAll()); }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> mergeAll(Class<V> clazz) { return this.<V, O>flatten(Operator.<V, O>mergeAll()); }
    @JsOverlay public final <V, O extends Observable<V>> Observable<V> exhaustAll(Class<V> clazz) { return this.<V, O>flatten(Operator.<V, O>exhaustAll()); }
    /** Observable&lt;Observable&lt;V&gt;&gt;를 평탄화하는 *All 연산자 공통 처리. 요소 타입을 O로 보는 캐스트를 한 곳에 모읍니다. */
    @JsOverlay private final <V, O extends Observable<V>> Observable<V> flatten(OperatorFunction<O, V> op) {
        return Js.<Observable<O>>uncheckedCast(this).pipe(op);
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
    @JsOverlay public final Observable<List<Object>> withLatestFrom(Observable<?> other) {
        return pipe(Operator.<T>withLatestFrom(other)).map(JsArrayLike::asList);
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

    // ===== RxJS 7 추가 체인 메서드 (https://rxjs.dev/api) =====
    /** 마지막 count개 값만 방출합니다(완료 시). */
    @JsOverlay public final Observable<T> takeLast(int count) { return pipe(Operator.takeLast(count)); }
    /** 마지막 count개 값을 버립니다. */
    @JsOverlay public final Observable<T> skipLast(int count) { return pipe(Operator.skipLast(count)); }
    /** predicate가 참인 동안 값을 건너뜁니다. */
    @JsOverlay public final Observable<T> skipWhile(Predicate<T> predicate) { return pipe(Operator.<T>skipWhile((v, i) -> predicate.test(v))); }
    /** notifier가 값을 낼 때까지 값을 건너뜁니다. */
    @JsOverlay public final Observable<T> skipUntil(Observable<?> notifier) { return pipe(Operator.skipUntil(notifier)); }
    /** index번째 값만 방출합니다. 없으면 ArgumentOutOfRangeError로 에러가 납니다. */
    @JsOverlay public final Observable<T> elementAt(int index) { return pipe(Operator.elementAt(index)); }
    /** index번째 값을 방출하고, 없으면 defaultValue를 방출합니다. */
    @JsOverlay public final Observable<T> elementAt(int index, T defaultValue) { return pipe(Operator.elementAt(index, defaultValue)); }
    /** 값은 모두 버리고 완료·에러만 전달합니다. */
    @JsOverlay public final Observable<T> ignoreElements() { return pipe(Operator.ignoreElements()); }
    /** 값이 정확히 하나일 때 그 값을 방출합니다. 아니면 에러가 납니다. */
    @JsOverlay public final Observable<T> single() { return pipe(Operator.single()); }
    /** predicate를 만족하는 값이 정확히 하나일 때 그 값을 방출합니다. */
    @JsOverlay public final Observable<T> single(Predicate<T> predicate) { return pipe(Operator.<T>single((v, i) -> predicate.test(v))); }
    /** 완료 시 값의 개수를 방출합니다. */
    @JsOverlay public final Observable<Integer> count() { return toInt(pipe(Operator.<T>count())); }
    /** 완료 시 predicate를 만족한 값의 개수를 방출합니다. */
    @JsOverlay public final Observable<Integer> count(Predicate<T> predicate) { return toInt(pipe(Operator.<T>count((v, i) -> predicate.test(v)))); }
    /** 모든 값이 predicate를 만족하는지 방출합니다. */
    @JsOverlay public final Observable<Boolean> every(Predicate<T> predicate) { return pipe(Operator.<T>every((v, i) -> predicate.test(v))); }
    /** predicate를 만족하는 첫 값을 방출합니다. 없으면 null(undefined)을 방출합니다. */
    @JsOverlay public final Observable<T> find(Predicate<T> predicate) { return pipe(Operator.<T>find((v, i) -> predicate.test(v))); }
    /** predicate를 만족하는 첫 값의 인덱스를 방출합니다. 없으면 -1입니다. */
    @JsOverlay public final Observable<Integer> findIndex(Predicate<T> predicate) { return toInt(pipe(Operator.<T>findIndex((v, i) -> predicate.test(v)))); }
    /** 값 없이 완료되었는지 방출합니다. */
    @JsOverlay public final Observable<Boolean> isEmpty() { return pipe(Operator.isEmpty()); }
    /** 값 없이 완료되면 defaultValue를 방출합니다. */
    @JsOverlay public final Observable<T> defaultIfEmpty(T defaultValue) { return pipe(Operator.defaultIfEmpty(defaultValue)); }
    /** 값 없이 완료되면 EmptyError로 에러를 냅니다. */
    @JsOverlay public final Observable<T> throwIfEmpty() { return pipe(Operator.throwIfEmpty()); }
    /** 값 없이 완료되면 errorFactory가 만든 에러를 냅니다. */
    @JsOverlay public final Observable<T> throwIfEmpty(Supplier<? extends JsError> errorFactory) { return pipe(Operator.<T>throwIfEmpty(errorFactory::get)); }
    /** 완료 시 최댓값을 방출합니다(JS 비교: 숫자·문자열에 사용). */
    @JsOverlay public final Observable<T> max() { return pipe(Operator.max()); }
    /** 완료 시 comparator 기준 최댓값을 방출합니다. */
    @JsOverlay public final Observable<T> max(Comparator<T> comparator) { return pipe(Operator.<T>max(comparator::compare)); }
    /** 완료 시 최솟값을 방출합니다(JS 비교: 숫자·문자열에 사용). */
    @JsOverlay public final Observable<T> min() { return pipe(Operator.min()); }
    /** 완료 시 comparator 기준 최솟값을 방출합니다. */
    @JsOverlay public final Observable<T> min(Comparator<T> comparator) { return pipe(Operator.<T>min(comparator::compare)); }
    /** 다른 소스들과 병합합니다. */
    @SafeVarargs @JsOverlay public final Observable<T> mergeWith(Observable<? extends T>... others) { return pipe(Operator.<T>mergeWith(others)); }
    /** 다른 소스들과 최신 값을 결합해 [this, ...others] List로 방출합니다. */
    @JsOverlay public final Observable<List<Object>> combineLatestWith(Observable<?>... others) { return pipe(Operator.<T>combineLatestWith(others)).map(JsArrayLike::asList); }
    /** 다른 소스들과 같은 인덱스의 값을 [this, ...others] List로 묶어 방출합니다. */
    @JsOverlay public final Observable<List<Object>> zipWith(Observable<?>... others) { return pipe(Operator.<T>zipWith(others)).map(JsArrayLike::asList); }
    /** 가장 먼저 값을 낸 소스만 미러링합니다. */
    @SafeVarargs @JsOverlay public final Observable<T> raceWith(Observable<? extends T>... others) { return pipe(Operator.<T>raceWith(others)); }
    /** 완료 직전에 values를 이어 방출합니다. */
    @SafeVarargs @JsOverlay public final Observable<T> endWith(T... values) { return pipe(Operator.endWith(values)); }
    /** 에러가 나거나 완료되면 다음 소스로 이어갑니다(에러는 무시). */
    @SafeVarargs @JsOverlay public final Observable<T> onErrorResumeNextWith(Observable<? extends T>... sources) { return pipe(Operator.<T>onErrorResumeNextWith(sources)); }
    /** 값이 오면 duration(ms) 뒤에 그동안의 최신 값을 방출합니다. */
    @JsOverlay public final Observable<T> auditTime(int duration) { return pipe(Operator.auditTime(duration)); }
    /** 값이 오면 durationSelector가 끝날 때 그동안의 최신 값을 방출합니다. */
    @JsOverlay public final Observable<T> audit(Function<T, Observable<?>> durationSelector) { return pipe(Operator.<T>audit(durationSelector::apply)); }
    /** period(ms)마다 그동안의 최신 값을 방출합니다. */
    @JsOverlay public final Observable<T> sampleTime(int period) { return pipe(Operator.sampleTime(period)); }
    /** notifier가 값을 낼 때마다 그동안의 최신 값을 방출합니다. */
    @JsOverlay public final Observable<T> sample(Observable<?> notifier) { return pipe(Operator.sample(notifier)); }
    /** 값을 방출한 뒤 durationSelector가 끝날 때까지 이후 값을 무시합니다. */
    @JsOverlay public final Observable<T> throttle(Function<T, Observable<?>> durationSelector) { return pipe(Operator.<T>throttle(durationSelector::apply)); }
    /** leading/trailing 설정으로 throttle을 적용합니다. */
    @JsOverlay public final Observable<T> throttle(Function<T, Observable<?>> durationSelector, boolean leading, boolean trailing) {
        return pipe(Operator.<T>throttle(durationSelector::apply, Operator.throttleConfig(leading, trailing)));
    }
    /** each(ms) 안에 값이 오지 않으면 TimeoutError로 에러를 냅니다. */
    @JsOverlay public final Observable<T> timeout(int each) { return pipe(Operator.timeout(each)); }
    /** each(ms) 안에 값이 오지 않으면 fallback으로 전환합니다. */
    @JsOverlay public final Observable<T> timeout(int each, Observable<? extends T> fallback) {
        JsPropertyMap<Object> config = JsPropertyMap.of();
        config.set("each", (double) each);
        config.set("with", (UnaryFunction<Object, Observable<? extends T>>) info -> fallback);
        return pipe(Operator.timeout(config));
    }
    /** 각 값을 delayDurationSelector가 값을 낼 때까지 지연합니다. */
    @JsOverlay public final Observable<T> delayWhen(Function<T, Observable<?>> delayDurationSelector) { return pipe(Operator.<T>delayWhen((v, i) -> delayDurationSelector.apply(v))); }
    /** 각 값을 방출 시각과 함께 Timestamp로 감쌉니다. */
    @JsOverlay public final Observable<Timestamp<T>> timestamp() { return pipe(Operator.timestamp()); }
    /** 각 값을 직전 방출로부터의 경과 시간과 함께 TimeInterval로 감쌉니다. */
    @JsOverlay public final Observable<TimeInterval<T>> timeInterval() { return pipe(Operator.timeInterval()); }
    /** 완료되면 무한히 다시 구독합니다. */
    @JsOverlay public final Observable<T> repeat() { return pipe(Operator.repeat()); }
    /** 소스를 count번 반복합니다. */
    @JsOverlay public final Observable<T> repeat(int count) { return pipe(Operator.repeat(count)); }
    /** 소스를 count번 반복하되 반복 사이에 delayMs만큼 기다립니다. */
    @JsOverlay public final Observable<T> repeat(int count, int delayMs) { return pipe(Operator.repeat(Operator.repeatConfig(count, delayMs))); }
    /** 에러가 나면 delayMs 뒤에 최대 count번 재구독합니다. */
    @JsOverlay public final Observable<T> retry(int count, int delayMs) { return retry(count, delayMs, false); }
    /** resetOnSuccess가 참이면 값이 방출될 때 재시도 횟수를 초기화합니다. */
    @JsOverlay public final Observable<T> retry(int count, int delayMs, boolean resetOnSuccess) { return pipe(Operator.retry(Operator.retryConfig(count, delayMs, resetOnSuccess))); }
    /** 에러가 나면 delay(error, 재시도 횟수)가 값을 낼 때 재구독합니다(최대 count번). */
    @JsOverlay public final Observable<T> retry(int count, BiFunction<JsError, Integer, Observable<?>> delay) {
        RetryDelay fn = (error, retryCount) -> delay.apply(error, retryCount);
        return pipe(Operator.retry(Operator.retryConfig(count, fn, false)));
    }
    /** RxJS ShareConfig의 reset 옵션으로 share를 적용합니다. */
    @JsOverlay public final Observable<T> share(boolean resetOnError, boolean resetOnComplete, boolean resetOnRefCountZero) {
        return pipe(Operator.share(Operator.shareConfig(resetOnError, resetOnComplete, resetOnRefCountZero)));
    }
    /** refCount가 참이면 구독자가 모두 떠날 때 소스 구독을 해제합니다. */
    @JsOverlay public final Observable<T> shareReplay(int bufferSize, boolean refCount) { return shareReplay(bufferSize, 0, refCount); }
    /** windowTime(ms)보다 오래된 값은 재생하지 않습니다(0 이하이면 무제한). */
    @JsOverlay public final Observable<T> shareReplay(int bufferSize, int windowTime, boolean refCount) {
        return pipe(Operator.shareReplay(Operator.shareReplayConfig(bufferSize, windowTime, refCount)));
    }
    /** 소스를 한 번만 구독하는 shared Observable을 selector에 넘겨 그 결과를 구독합니다. */
    @JsOverlay public final <R> Observable<R> connect(Function<Observable<T>, Observable<R>> selector) { return pipe(Operator.<T, R>connect(selector::apply)); }
    /** bufferSize개씩 묶어 List로 방출합니다. */
    @JsOverlay public final Observable<List<T>> bufferCount(int bufferSize) { return pipe(Operator.<T>bufferCount(bufferSize)).map(JsArrayLike::asList); }
    /** startBufferEvery개마다 새 버퍼를 열어 bufferSize개씩 묶습니다. */
    @JsOverlay public final Observable<List<T>> bufferCount(int bufferSize, int startBufferEvery) { return pipe(Operator.<T>bufferCount(bufferSize, startBufferEvery)).map(JsArrayLike::asList); }
    /** closingNotifier가 값을 낼 때마다 그동안의 값을 List로 방출합니다. */
    @JsOverlay public final Observable<List<T>> buffer(Observable<?> closingNotifier) { return pipe(Operator.<T>buffer(closingNotifier)).map(JsArrayLike::asList); }
    /** closingSelector가 값을 낼 때마다 버퍼를 닫고 새로 엽니다. */
    @JsOverlay public final Observable<List<T>> bufferWhen(Supplier<? extends Observable<?>> closingSelector) { return pipe(Operator.<T>bufferWhen(closingSelector::get)).map(JsArrayLike::asList); }
    /** openings가 값을 내면 버퍼를 열고, closingSelector가 값을 내면 닫습니다. */
    @JsOverlay public final <O> Observable<List<T>> bufferToggle(Observable<O> openings, Function<O, Observable<?>> closingSelector) {
        return pipe(Operator.<T, O>bufferToggle(openings, closingSelector::apply)).map(JsArrayLike::asList);
    }
    /** windowSize개씩 나눈 창(Observable)을 방출합니다. */
    @JsOverlay public final Observable<Observable<T>> windowCount(int windowSize) { return pipe(Operator.windowCount(windowSize)); }
    /** startWindowEvery개마다 새 창을 엽니다. */
    @JsOverlay public final Observable<Observable<T>> windowCount(int windowSize, int startWindowEvery) { return pipe(Operator.windowCount(windowSize, startWindowEvery)); }
    /** windowBoundaries가 값을 낼 때마다 창을 닫고 새로 엽니다. */
    @JsOverlay public final Observable<Observable<T>> window(Observable<?> windowBoundaries) { return pipe(Operator.window(windowBoundaries)); }
    /** closingSelector가 값을 낼 때마다 창을 닫고 새로 엽니다. */
    @JsOverlay public final Observable<Observable<T>> windowWhen(Supplier<? extends Observable<?>> closingSelector) { return pipe(Operator.<T>windowWhen(closingSelector::get)); }
    /** openings가 값을 내면 창을 열고, closingSelector가 값을 내면 닫습니다. */
    @JsOverlay public final <O> Observable<Observable<T>> windowToggle(Observable<O> openings, Function<O, Observable<?>> closingSelector) {
        return pipe(Operator.<T, O>windowToggle(openings, closingSelector::apply));
    }
    /** 값 전달을 scheduler에서 하도록 바꿉니다. */
    @JsOverlay public final Observable<T> observeOn(AsyncScheduler<?> scheduler) { return pipe(Operator.observeOn(scheduler)); }
    @JsOverlay public final Observable<T> observeOn(TaskScheduler scheduler) { return pipe(Operator.observeOn(scheduler)); }
    /** 소스 구독을 scheduler에서 하도록 바꿉니다. */
    @JsOverlay public final Observable<T> subscribeOn(AsyncScheduler<?> scheduler) { return pipe(Operator.subscribeOn(scheduler)); }
    @JsOverlay public final Observable<T> subscribeOn(TaskScheduler scheduler) { return pipe(Operator.subscribeOn(scheduler)); }
    /** key별로 값을 나눈 GroupedObservable을 방출합니다. */
    @JsOverlay public final <K> Observable<GroupedObservable<K, T>> groupBy(Function<T, K> key) { return pipe(Operator.<T, K>groupBy(key::apply)); }
    /** key별로 나누되 각 값은 element로 변환해 그룹에 넣습니다. */
    @JsOverlay public final <K, E> Observable<GroupedObservable<K, E>> groupBy(Function<T, K> key, Function<T, E> element) {
        JsPropertyMap<Object> options = JsPropertyMap.of();
        options.set("element", (UnaryFunction<T, E>) element::apply);
        return pipe(Operator.<T, K, E>groupBy(key::apply, options));
    }
    /** 값·에러·완료를 ObservableNotification 값으로 바꿉니다. */
    @JsOverlay public final Observable<ObservableNotification<T>> materialize() { return pipe(Operator.materialize()); }
    /** ObservableNotification 값의 스트림을 원래 값·에러·완료로 되돌립니다. */
    @JsOverlay public final <V> Observable<V> dematerialize() { return Js.<Observable<ObservableNotification<V>>>uncheckedCast(this).pipe(Operator.<V>dematerialize()); }
    /** 각 값을 project 결과로 재귀적으로 펼칩니다. */
    @JsOverlay public final Observable<T> expand(Function<T, Observable<T>> project) { return pipe(Operator.<T>expand((v, i) -> project.apply(v))); }
    /** 동시에 구독하는 내부 Observable 수를 concurrent로 제한합니다. */
    @JsOverlay public final Observable<T> expand(Function<T, Observable<T>> project, int concurrent) { return pipe(Operator.<T>expand((v, i) -> project.apply(v), concurrent)); }
    /** 누적값을 내부 Observable로 계산하고 결과를 병합합니다. */
    @JsOverlay public final <R> Observable<R> mergeScan(BiFunction<R, T, Observable<R>> accumulator, R seed) { return pipe(Operator.<T, R>mergeScan((a, v, i) -> accumulator.apply(a, v), seed)); }
    @JsOverlay public final <R> Observable<R> mergeScan(BiFunction<R, T, Observable<R>> accumulator, R seed, int concurrent) { return pipe(Operator.<T, R>mergeScan((a, v, i) -> accumulator.apply(a, v), seed, concurrent)); }
    /** 누적값을 내부 Observable로 계산하되 새 값이 오면 이전 내부 Observable을 취소합니다. */
    @JsOverlay public final <R> Observable<R> switchScan(BiFunction<R, T, Observable<R>> accumulator, R seed) { return pipe(Operator.<T, R>switchScan((a, v, i) -> accumulator.apply(a, v), seed)); }
    /** 두 시퀀스가 같은 값을 같은 순서로 내는지 방출합니다. */
    @JsOverlay public final Observable<Boolean> sequenceEqual(Observable<T> compareTo) { return pipe(Operator.sequenceEqual(compareTo)); }
    @JsOverlay public final Observable<Boolean> sequenceEqual(Observable<T> compareTo, BiPredicate<T, T> comparator) { return pipe(Operator.<T>sequenceEqual(compareTo, (a, b) -> comparator.test(a, b))); }
    /** Observable&lt;Observable&lt;?&gt;&gt;의 내부 Observable들을 combineLatest로 결합해 List로 방출합니다. */
    @JsOverlay public final Observable<List<Object>> combineLatestAll() { return Js.<Observable<Observable<?>>>uncheckedCast(this).pipe(Operator.combineLatestAll()).map(JsArrayLike::asList); }
    /** Observable&lt;Observable&lt;?&gt;&gt;의 내부 Observable들을 zip으로 묶어 List로 방출합니다. */
    @JsOverlay public final Observable<List<Object>> zipAll() { return Js.<Observable<Observable<?>>>uncheckedCast(this).pipe(Operator.zipAll()).map(JsArrayLike::asList); }
    /** JS 객체 값의 key 속성이 직전과 같으면 버립니다. */
    @JsOverlay public final Observable<T> distinctUntilKeyChanged(String key) { return pipe(Operator.distinctUntilKeyChanged(key)); }
}

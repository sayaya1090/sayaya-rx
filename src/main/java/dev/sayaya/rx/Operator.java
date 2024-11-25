package dev.sayaya.rx;

import dev.sayaya.rx.function.*;
import elemental2.core.JsError;
import jsinterop.annotations.JsMethod;

import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Operator {
    /**
     * 소스 Observable에서 방출된 각 값에 주어진 변환 함수(project function)를 적용하고, 변환된 값을 Observable로 방출합니다.
     * @link <a href="https://rxjs.dev/api/index/function/map">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="map")
    public static native <T, R> OperatorFunction<T, R> map(ProjectWithIndex<T, R> func);
    public static <T, R> OperatorFunction<T, R> map(Function<T, R> func) {
        return map((a, i) -> func.apply(a));
    }

    /**
     * 소스 Observable에서 지정된 조건을 만족하는 항목만 방출하여 항목을 필터링합니다.
     * @link <a href="https://rxjs.dev/api/index/function/filter">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="filter")
    public static native <T> OperatorFunction<T, T> filter(PredicateWithIndex<T> func);
    public static <T> OperatorFunction<T, T> filter(Predicate<T> predicate) {
        return filter((a, i) -> predicate.test(a));
    }

    /**
     * 소스 Observable에서 첫 번째부터 count 개 값만 방출합니다
     * @link <a href="https://rxjs.dev/api/index/function/take">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="take")
    public static native <T> OperatorFunction<T, T> take(int count);

    /**
     * 소스 Observable에서 첫 번째부터 count 개 항목을 건너뛰는 Observable을 반환합니다
     * @link <a href="https://rxjs.dev/api/index/function/skip">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="skip")
    public static native <T> OperatorFunction<T, T> skip(int count);

    /**
     * Returns an Observable that mirrors the source Observable with the exception of an error.
     * @link <a href="https://rxjs.dev/api/index/function/retry">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="retry")
    public static native <T> OperatorFunction<T, T> retry(int count);

    /**
     * 상태를 캡슐화하고 관리하는 데 유용합니다.
     * 초기 상태를 설정한 후(시드 값(두 번째 인자)을 통해서나 소스의 첫 번째 값에서 설정), 소스에서 방출된 각 값에 누산기(또는 "리듀서 함수")를 적용합니다.
     * @link <a href="https://rxjs.dev/api/index/function/scan">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="scan")
    public static native <V> OperatorFunction<V, V> scan(AccumulatorWithIndex<V> accumulator, V seed);
    @JsMethod(namespace="rxjs", name="scan")
    public static native <V> OperatorFunction<V, V> scan(AccumulatorWithIndex<V> accumulator);
    public static <V> OperatorFunction<V, V> scan(BiFunction<V, V, V> func) {
        return scan((a, v, i) -> func.apply(a, v));
    }

    /**
     * 구독 시점에 이 연산자에 제공된 모든 값을 동기적으로 방출한 다음 소스를 구독하고 모든 방출을 구독자에게 미러링하는 관찰 가능 항목을 반환합니다.
     * @link <a href="https://rxjs.dev/api/operators/startWith">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="startWith")
    public static native <V> OperatorFunction<V, V> startWith(V... values);


    /**
     * Observable에서 발생한 오류를 감지하여 새로운 Observable을 반환하거나 오류를 다시 발생시키는 방식으로 처리합니다.
     * @link <a href="https://rxjs.dev/api/operators/catchError">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="catchError")
    public static native <T, R> OperatorFunction<T, R> catchError(Reducer<JsError, Observable<? super T>, Observable<? super R>> selector);

    // public native static <T, R> OperatorFunction<T, R> catchError(Func2<?, Observable<? super T>,
    //            Observable<? extends R>> selector);
    // tab, mergeMap/flatMap, concatMap, switchMap, combineLatest, zip, debounceTime,
    // distinctUntilChanged, pluck, catchError, finalize

    /**
     * "외부" Observable에서 나오는 각 "내부" Observable을 구독하고, Observable이 완료될 때까지 모든 방출된 값을 복사하고 다음 Observable로 넘어갑니다
     * @link <a href="https://rxjs.dev/api/operators/concatAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="concatAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> concatAll();
    /**
     * 도착하면 첫 번째 내부 Observable을 구독하고 도착할 때마다 각 값을 방출하지만 다음 내부 Observable이 도착하면 이전 Observable을 구독 취소하고 새 Observable을 구독합니다.
     * @link <a href="https://rxjs.dev/api/operators/switchAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="switchAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> switchAll();
    /**
     * 내부 Observable이 도착하면 구독한 다음 도착하면 각 값을 방출합니다.
     * @link <a href="https://rxjs.dev/api/operators/mergeAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="mergeAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> mergeAll();
    /**
     * 도착하는 첫 번째 내부 Observable을 구독하고, 도착하는 대로 각 값을 방출하며, 첫 번째 내부 Observable이 완료될 때까지 새로 도착한 모든 내부 Observable을 삭제한 후, 다음 내부 Observable을 기다립니다.
     * @link <a href="https://rxjs.dev/api/operators/exhaustAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="exhaustAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> exhaustAll();
}
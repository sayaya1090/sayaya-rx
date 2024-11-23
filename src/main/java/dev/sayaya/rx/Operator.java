package dev.sayaya.rx;

import dev.sayaya.rx.function.AccumulatorWithIndex;
import dev.sayaya.rx.function.PredicateWithIndex;
import dev.sayaya.rx.function.ProjectWithIndex;
import dev.sayaya.rx.function.UnaryFunction;
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
    public static native <T, R> UnaryFunction<Observable<T>, Observable<R>> map(ProjectWithIndex<T, R> func);
    public static <T, R> UnaryFunction<Observable<T>, Observable<R>> map(Function<T, R> func) {
        return map((a, i) -> func.apply(a));
    }

    /**
     * 소스 Observable에서 지정된 조건을 만족하는 항목만 방출하여 항목을 필터링합니다.
     * @link <a href="https://rxjs.dev/api/index/function/filter">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="filter")
    public static native <T> UnaryFunction<Observable<T>, Observable<T>> filter(PredicateWithIndex<T> func);
    public static <T> UnaryFunction<Observable<T>, Observable<T>> filter(Predicate<T> predicate) {
        return filter((a, i) -> predicate.test(a));
    }

    /**
     * 소스 Observable에서 첫 번째부터 count 개 값만 방출합니다
     * @link <a href="https://rxjs.dev/api/index/function/take">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="take")
    public static native <T> UnaryFunction<Observable<T>, Observable<T>> take(int count);

    /**
     * 소스 Observable에서 첫 번째부터 count 개 항목을 건너뛰는 Observable을 반환합니다
     * @link <a href="https://rxjs.dev/api/index/function/skip">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="skip")
    public static native <T> UnaryFunction<Observable<T>, Observable<T>> skip(int count);

    /**
     * Returns an Observable that mirrors the source Observable with the exception of an error.
     * @link <a href="https://rxjs.dev/api/index/function/retry">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="retry")
    public static native <T> UnaryFunction<Observable<T>, Observable<T>> retry(int count);

    /**
     * 상태를 캡슐화하고 관리하는 데 유용합니다.
     * 초기 상태를 설정한 후(시드 값(두 번째 인자)을 통해서나 소스의 첫 번째 값에서 설정), 소스에서 방출된 각 값에 누산기(또는 "리듀서 함수")를 적용합니다.
     * @link <a href="https://rxjs.dev/api/index/function/scan">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="scan")
    public static native <V> UnaryFunction<Observable<V>, Observable<V>> scan(AccumulatorWithIndex<V> accumulator, V seed);
    @JsMethod(namespace="rxjs", name="scan")
    public static native <V> UnaryFunction<Observable<V>, Observable<V>> scan(AccumulatorWithIndex<V> accumulator);
    public static <V> UnaryFunction<Observable<V>, Observable<V>> scan(BiFunction<V, V, V> func) {
        return scan((a, v, i) -> func.apply(a, v));
    }

    /**
     * 구독 시점에 이 연산자에 제공된 모든 값을 동기적으로 방출한 다음 소스를 구독하고 모든 방출을 구독자에게 미러링하는 관찰 가능 항목을 반환합니다.
     * @link <a href="https://rxjs.dev/api/operators/startWith">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="startWith")
    public static native <V> UnaryFunction<Observable<V>, Observable<V>> startWith(V... values);


    // tab, mergeMap/flatMap, concatMap, switchMap, combineLatest, zip, debounceTime,
    // distinctUntilChanged, pluck, catchError, finalize

}

package dev.sayaya.rx;

import dev.sayaya.rx.function.*;
import dev.sayaya.rx.scheduler.AsyncScheduler;
import elemental2.core.JsArray;
import elemental2.core.JsError;
import elemental2.core.JsNumber;
import jsinterop.annotations.JsMethod;

import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Operator {
    @JsMethod(namespace="rxjs", name="pipe")
    public static native OperatorFunction<?, ?> pipe(OperatorFunction<?, ?>... func);
    /**
     * 소스 Observable에서 방출된 각 값에 주어진 변환 함수(project function)를 적용하고, 변환된 값을 Observable로 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/map">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="map")
    public static native <T, R> OperatorFunction<T, R> map(ProjectWithIndex<T, R> func);
    public static <T, R> OperatorFunction<T, R> map(Function<T, R> func) {
        return map((a, i) -> func.apply(a));
    }
    /**
     * 각 소스 값을 Observable에 투영하여 출력 Observable에 병합합니다.
     * @see <a href="https://rxjs.dev/api/index/function/mergeMap">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="mergeMap")
    public static native <T, R> OperatorFunction<T, R> mergeMap(UnaryFunction<? super T, ? extends Observable<? extends R>> mapper);
    public static <T, R> OperatorFunction<T, R> mergeMap(Function<? super T, ? extends Observable<? extends R>> func) {
        return mergeMap((UnaryFunction<? super T, ? extends Observable<? extends R>>) func::apply);
    }
    @JsMethod(namespace="rxjs", name="mergeMapTo")
    public static native <T, R> OperatorFunction<T, R> mergeMapTo(Observable<? extends R> target);
    /**
     * 각 소스 값을 Observable에 투영하여 직렬화된 방식으로 출력 Observable에 병합한 다음, 다음 Observable을 병합하기 전에 각 Observable이 완료될 때까지 기다립니다.
     * @see <a href="https://rxjs.dev/api/index/function/concatMap">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="concatMap")
    public static native <T, R> OperatorFunction<T, R> concatMap(UnaryFunction<? super T, ? extends Observable<? extends R>> mapper);
    public static <T, R> OperatorFunction<T, R> concatMap(Function<? super T, ? extends Observable<? extends R>> func) {
        return concatMap((UnaryFunction<? super T, ? extends Observable<? extends R>>) func::apply);
    }
    @JsMethod(namespace="rxjs", name="concatMapTo")
    public static native <T, R> OperatorFunction<T, R> concatMapTo(Observable<? extends R> target);

    /**
     * 소스 옵저버블의 모든 값을 먼저 방출한 후, 완료되면 제공된 각 옵저버블 소스에 하나씩 순차적으로 구독하여, 각 옵저버블의 값을 모두 방출하고, 이전 옵저버블이 완료되어야 다음 옵저버블을 구독합니다.
     * @see <a href="https://rxjs.dev/api/index/function/concatWith">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="concatWith")
    public static native <T> OperatorFunction<T, T> concatWith(Observable<T>... observers);

    /**
     * 각 소스 값을 Observable에 투영하여 출력 Observable에 병합하고, 가장 최근에 투영된 Observable의 값만 내보냅니다.
     * @see <a href="https://rxjs.dev/api/index/function/switchMap">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="switchMap")
    public static native <T, R> OperatorFunction<T, R> switchMap(UnaryFunction<? super T, ? extends Observable<? extends R>> mapper);
    public static <T, R> OperatorFunction<T, R> switchMap(Function<? super T, ? extends Observable<? extends R>> func) {
        return switchMap((UnaryFunction<? super T, ? extends Observable<? extends R>>) func::apply);
    }
    @JsMethod(namespace="rxjs", name="switchMapTo")
    public static native <T, R> OperatorFunction<T, R> switchMapTo(Observable<? extends R> target);
    /**
     * 소스 Observable로부터의 부수작업을 수행하는 데 사용됩니다.
     * @see <a href="https://rxjs.dev/api/index/function/tap">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="tap")
    public static native <T> OperatorFunction<T, T> tap(Observer<T> observer);
    @JsMethod(namespace="rxjs", name="tap")
    public static native <T> OperatorFunction<T, T> tap(VoidFunction<T> observer);
    /**
     * 여러 개의 Observable을 결합하여 각 입력 Observable의 값을 순서대로 계산하여 값을 갖는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/zip">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="zip")
    public static native <T> Observable<JsArray<T>> zip(Observable<? extends T>... observables);
    /**
     * 여러 개의 Observable을 결합하여 각 입력 Observable의 최신 값에서 계산된 값을 갖는 Observable을 생성합니다.
     * @see <a href="https://rxjs.dev/api/index/function/combineLatest">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="combineLatest")
    public static native Observable<JsArray<?>> combineLatest(Observable<?>... observables);
    /**
     * 특정 기간 동안 소스 Observable 값을 버퍼링합니다.
     * @see <a href="https://rxjs.dev/api/index/function/bufferTime">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="bufferTime")
    public static native <T> OperatorFunction<T, T[]> bufferTime(int bufferTimeSpan);
    @JsMethod(namespace="rxjs", name="bufferTime")
    public static native <T> OperatorFunction<T, T[]> bufferTime(int bufferTimeSpan, int bufferCreationInterval);
    @JsMethod(namespace="rxjs", name="bufferTime")
    public static native <T> OperatorFunction<T, T[]> bufferTime(int bufferTimeSpan, int bufferCreationInterval, int maxBufferSize);
    /**
     * 소스 Observable 값을 시간에 따라 주기적으로 중첩된 Observable로 분기합니다.
     * @see <a href="https://rxjs.dev/api/index/function/windowTime">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="windowTime")
    public static native <T> OperatorFunction<T, Observable<T>> windowTime(int bufferTimeSpan);
    @JsMethod(namespace="rxjs", name="windowTime")
    public static native <T> OperatorFunction<T, Observable<T>> windowTime(int bufferTimeSpan, int bufferCreationInterval);
    @JsMethod(namespace="rxjs", name="windowTime")
    public static native <T> OperatorFunction<T, Observable<T>> windowTime(int bufferTimeSpan, int bufferCreationInterval, int maxBufferSize);
    /**
     * 소스 Observable에서 지정된 조건을 만족하는 항목만 방출하여 항목을 필터링합니다.
     * @see <a href="https://rxjs.dev/api/index/function/filter">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="filter")
    public static native <T> OperatorFunction<T, T> filter(PredicateWithIndex<T> func);
    public static <T> OperatorFunction<T, T> filter(Predicate<T> predicate) {
        return filter((a, i) -> predicate.test(a));
    }
    /**
     * 소스 Observable에서 첫 번째부터 count 개 값만 방출합니다
     * @see <a href="https://rxjs.dev/api/index/function/take">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="take")
    public static native <T> OperatorFunction<T, T> take(int count);

    /**
     * 소스 Observable에서 첫 번째부터 count 개 항목을 건너뛰는 Observable을 반환합니다
     * @see <a href="https://rxjs.dev/api/index/function/skip">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="skip")
    public static native <T> OperatorFunction<T, T> skip(int count);

    /**
     * Returns an Observable that mirrors the source Observable with the exception of an error.
     * @see <a href="https://rxjs.dev/api/index/function/retry">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="retry")
    public static native <T> OperatorFunction<T, T> retry(int count);
    /**
     * 상태를 캡슐화하고 관리하는 데 유용합니다.
     * 초기 상태를 설정한 후(시드 값(두 번째 인자)을 통해서나 소스의 첫 번째 값에서 설정), 소스에서 방출된 각 값에 누산기(또는 "리듀서 함수")를 적용합니다.
     * @see <a href="https://rxjs.dev/api/index/function/scan">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="scan")
    public static native <V> OperatorFunction<V, V> scan(AccumulatorWithIndex<V> accumulator, V seed);
    @JsMethod(namespace="rxjs", name="scan")
    public static native <V> OperatorFunction<V, V> scan(AccumulatorWithIndex<V> accumulator);
    public static <V> OperatorFunction<V, V> scan(BiFunction<V, V, V> func) {
        return scan((a, v, i) -> func.apply(a, v));
    }
    @JsMethod(namespace="rxjs", name="scan")
    public static native <T, A> OperatorFunction<T, A> scan(Reducer<A, T, A> accumulator, A seed);
    /**
     * 다른 Observable에 의해 결정된 특정 시간 범위가 다른 소스 방출 없이 지난 후에만 소스 Observable에서 알림을 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/debounce">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="debounce")
    public static native <V> OperatorFunction<V, V> debounce(UnaryFunction<V, Observable<?>> durationSelector);
    /**
     * 특정 시간 동안 다른 소스에서 알림을 방출하지 않은 후에만 소스 Observable에서 알림을 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/debounceTime">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="debounceTime")
    public static native <V> OperatorFunction<V, V> debounceTime(JsNumber due);
    /**
     * 특정 시간 동안 다른 소스에서 알림을 방출하지 않은 후에만 소스 Observable에서 알림을 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/throttleTime">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="throttleTime")
    public static native <V> OperatorFunction<V, V> throttleTime(JsNumber due, AsyncScheduler<V> scheduler);
    @JsMethod(namespace="rxjs", name="throttleTime")
    public static native <V> OperatorFunction<V, V> throttleTime(JsNumber due, AsyncScheduler<V> scheduler, jsinterop.base.JsPropertyMap<Object> config);
    /**
     * throttleTime의 설정 객체({leading, trailing})를 만듭니다.
     */
    public static jsinterop.base.JsPropertyMap<Object> throttleConfig(boolean leading, boolean trailing) {
        return jsinterop.base.JsPropertyMap.of("leading", leading, "trailing", trailing);
    }
    /**
     * 이전 항목과 비교하여 구별되는 소스 Observable이 방출하는 모든 항목을 방출하는 Observable을 반환합니다.
     * @see <a href="https://rxjs.dev/api/index/function/distinct">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="distinct")
    public static native <V> OperatorFunction<V, V> distinct();
    @JsMethod(namespace="rxjs", name="distinct")
    public static native <V> OperatorFunction<V, V> distinct(UnaryFunction<V, ?> keySelector);
    /**
     * 결과 Observable은 소스 Observable에서 전달된 값 중에서, 이전에 결과 Observable이 방출했던 마지막 값과 비교하여 서로 다른 값들만 방출합니다.
     * @see <a href="https://rxjs.dev/api/index/function/distinctUntilChanged">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="distinctUntilChanged")
    public static native <V> OperatorFunction<V, V> distinctUntilChanged();
    @JsMethod(namespace="rxjs", name="distinctUntilChanged")
    public static native <V> OperatorFunction<V, V> distinctUntilChanged(Reducer<V, V, Boolean> comparator);
    /**
     * 구독 시점에 이 연산자에 제공된 모든 값을 동기적으로 방출한 다음 소스를 구독하고 모든 방출을 구독자에게 미러링하는 관찰 가능 항목을 반환합니다.
     * @see <a href="https://rxjs.dev/api/operators/startWith">Guide</a>
     */
    @SafeVarargs
    @JsMethod(namespace="rxjs", name="startWith")
    public static native <V> OperatorFunction<V, V> startWith(V... values);
    /**
     * Observable에서 발생한 오류를 감지하여 새로운 Observable을 반환하거나 오류를 다시 발생시키는 방식으로 처리합니다.
     * @see <a href="https://rxjs.dev/api/operators/catchError">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="catchError")
    public static native <T, R> OperatorFunction<T, R> catchError(Reducer<JsError, Observable<T>, Observable<? extends R>> selector);

    /**
     * "외부" Observable에서 나오는 각 "내부" Observable을 구독하고, Observable이 완료될 때까지 모든 방출된 값을 복사하고 다음 Observable로 넘어갑니다
     * @see <a href="https://rxjs.dev/api/operators/concatAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="concatAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> concatAll();
    /**
     * 도착하면 첫 번째 내부 Observable을 구독하고 도착할 때마다 각 값을 방출하지만 다음 내부 Observable이 도착하면 이전 Observable을 구독 취소하고 새 Observable을 구독합니다.
     * @see <a href="https://rxjs.dev/api/operators/switchAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="switchAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> switchAll();
    /**
     * 내부 Observable이 도착하면 구독한 다음 도착하면 각 값을 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/mergeAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="mergeAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> mergeAll();
    /**
     * 도착하는 첫 번째 내부 Observable을 구독하고, 도착하는 대로 각 값을 방출하며, 첫 번째 내부 Observable이 완료될 때까지 새로 도착한 모든 내부 Observable을 삭제한 후, 다음 내부 Observable을 기다립니다.
     * @see <a href="https://rxjs.dev/api/operators/exhaustAll">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="exhaustAll")
    public static native <T, O extends Observable<T>> OperatorFunction<O, T> exhaustAll();
    /**
     * 소스 Observable을 미러링하는 Observable을 반환하지만 소스가 완료 또는 오류로 종료될 때 지정된 함수를 호출합니다.
     * 지정된 함수는 구독자가 명시적으로 구독을 취소할 때도 호출됩니다.
     * @see <a href="https://rxjs.dev/api/operators/finalize">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="finalize")
    public static native <V> OperatorFunction<V, V> finalize(Callback callback);
    /**
     * notifier가 값을 방출할 때까지 소스 값을 방출하고, 그 뒤 완료합니다.
     * @see <a href="https://rxjs.dev/api/operators/takeUntil">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="takeUntil")
    public static native <T> OperatorFunction<T, T> takeUntil(Observable<?> notifier);
    /**
     * 조건을 만족하는 동안만 값을 방출하고, 처음으로 만족하지 않으면 완료합니다.
     * @see <a href="https://rxjs.dev/api/operators/takeWhile">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="takeWhile")
    public static native <T> OperatorFunction<T, T> takeWhile(PredicateWithIndex<T> predicate);
    public static <T> OperatorFunction<T, T> takeWhile(Predicate<T> predicate) {
        return takeWhile((a, i) -> predicate.test(a));
    }
    /**
     * 첫 번째 값(또는 조건을 만족하는 첫 값)만 방출하고 완료합니다.
     * @see <a href="https://rxjs.dev/api/operators/first">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="first")
    public static native <T> OperatorFunction<T, T> first();
    @JsMethod(namespace="rxjs", name="first")
    public static native <T> OperatorFunction<T, T> first(PredicateWithIndex<T> predicate);
    public static <T> OperatorFunction<T, T> first(Predicate<T> predicate) {
        return first((a, i) -> predicate.test(a));
    }
    /**
     * 소스가 완료될 때 마지막 값(또는 조건을 만족하는 마지막 값)을 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/last">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="last")
    public static native <T> OperatorFunction<T, T> last();
    @JsMethod(namespace="rxjs", name="last")
    public static native <T> OperatorFunction<T, T> last(PredicateWithIndex<T> predicate);
    public static <T> OperatorFunction<T, T> last(Predicate<T> predicate) {
        return last((a, i) -> predicate.test(a));
    }
    /**
     * 누산기를 적용해, 소스가 완료될 때 누적 결과 하나만 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/reduce">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="reduce")
    public static native <T> OperatorFunction<T, T> reduce(Reducer<T, T, T> accumulator);
    @JsMethod(namespace="rxjs", name="reduce")
    public static native <T, A> OperatorFunction<T, A> reduce(Reducer<A, T, A> accumulator, A seed);
    /**
     * 각 값을 지정한 시간(ms)만큼 늦춰 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/delay">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="delay")
    public static native <T> OperatorFunction<T, T> delay(int due);
    /**
     * 하나의 구독을 여러 구독자가 공유(멀티캐스트)합니다.
     * @see <a href="https://rxjs.dev/api/operators/share">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="share")
    public static native <T> OperatorFunction<T, T> share();
    /**
     * 구독을 공유하고, 마지막 bufferSize개 값을 늦게 온 구독자에게 재생합니다.
     * @see <a href="https://rxjs.dev/api/operators/shareReplay">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="shareReplay")
    public static native <T> OperatorFunction<T, T> shareReplay(int bufferSize);
    /**
     * 소스 값이 나올 때마다 other의 최신 값과 묶어 [소스값, other값] 배열로 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/withLatestFrom">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="withLatestFrom")
    public static native <T> OperatorFunction<T, JsArray<Object>> withLatestFrom(Observable<?> other);
    /**
     * 각 소스 값을 내부 Observable로 투영하되, 내부 Observable이 진행 중이면 새 소스 값을 무시합니다.
     * @see <a href="https://rxjs.dev/api/operators/exhaustMap">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="exhaustMap")
    public static native <T, R> OperatorFunction<T, R> exhaustMap(UnaryFunction<? super T, ? extends Observable<? extends R>> mapper);
    public static <T, R> OperatorFunction<T, R> exhaustMap(Function<? super T, ? extends Observable<? extends R>> func) {
        return exhaustMap((UnaryFunction<? super T, ? extends Observable<? extends R>>) func::apply);
    }
    /**
     * 소스가 완료될 때 모든 값을 배열 하나로 모아 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/toArray">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="toArray")
    public static native <T> OperatorFunction<T, JsArray<T>> toArray();
    /**
     * 연속된 두 값을 [이전값, 현재값] 배열로 묶어 방출합니다.
     * @see <a href="https://rxjs.dev/api/operators/pairwise">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="pairwise")
    public static native <T> OperatorFunction<T, JsArray<T>> pairwise();
}

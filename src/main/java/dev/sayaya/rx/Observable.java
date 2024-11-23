package dev.sayaya.rx;

import dev.sayaya.rx.function.UnaryFunction;
import elemental2.dom.Event;
import jsinterop.annotations.*;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static dev.sayaya.rx.Observer.next;

/**
 * Observables are lazy Push collections of multiple values
 * @link <a href="https://rxjs.dev/guide/observable">Guide</a>
 */
@JsType(isNative = true, namespace="rxjs", name="Observable")
public class Observable<T> {
    /**
     * 인수를 관찰 가능한 시퀀스로 변환합니다.
     * @link <a href="https://rxjs.dev/api/index/function/of">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="of")
    public static native <T> Observable<T> of(T... values);

    /**
     * 지정된 범위 내에서 숫자 시퀀스를 방출하는 Observable을 생성합니다.
     * @link <a href="https://rxjs.dev/api/index/function/range">Guide</a>
     */
    public static native Observable<Integer> range(int start, Integer count);

    /**
     * 배열, 배열과 유사한 객체, Promise, 반복 가능한 객체 또는 Observable과 유사한 객체에서 Observable을 생성합니다.
     * @link <a href="https://rxjs.dev/api/index/function/from">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="from")
    public static native <T> Observable<T> from(Object... any);

    /**
     * 지정된 이벤트 대상에서 발생하는 특정 유형의 이벤트를 방출하는 Observable을 생성합니다.
     * @link <a href="https://rxjs.dev/api/index/function/fromEvent">Guide</a>
     */
    @JsMethod(namespace="rxjs", name="fromEvent")
    public static native Observable<Event> fromEvent(Object target, String eventName);

    protected Observable(){}
    public native Subscription subscribe(Observer<T> observer);
    @JsOverlay public final Subscription subscribe(Consumer<T> consumer) {return subscribe(next(consumer)); }

    public native <V> Observable<V> pipe(UnaryFunction<Observable<T>, Observable<V>> func);
    @JsOverlay public final <V> Observable<V> map(Function<T, V> func) { return pipe(Operator.map(func)); }
    @JsOverlay public final Observable<T> filter(Predicate<T> predicate) { return pipe(Operator.filter(predicate)); }
    @JsOverlay public final Observable<T> take(int count) { return pipe(Operator.take(count)); }
    @JsOverlay public final Observable<T> skip(int count) { return pipe(Operator.skip(count)); }
    @JsOverlay public final Observable<T> retry(int count) { return pipe(Operator.retry(count)); }
    @JsOverlay public final Observable<T> scan(BiFunction<T, T, T> func) { return pipe(Operator.scan(func)); }
    @JsOverlay @SafeVarargs public final Observable<T> startWith(T... values) { return pipe(Operator.startWith(values)); }
}

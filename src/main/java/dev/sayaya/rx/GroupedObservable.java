package dev.sayaya.rx;

import jsinterop.annotations.JsType;

/**
 * groupBy가 방출하는 그룹. 같은 key로 분류된 값들을 방출하는 Observable입니다.
 * @see <a href="https://rxjs.dev/api/index/interface/GroupedObservable">Guide</a>
 */
@JsType(isNative = true, namespace = "rxjs", name = "Observable")
public class GroupedObservable<K, T> extends Observable<T> {
    /** 이 그룹의 key. */
    public K key;
    protected GroupedObservable() {}
}

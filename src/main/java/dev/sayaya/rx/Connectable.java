package dev.sayaya.rx;

import jsinterop.annotations.JsType;

/**
 * connect()를 호출해야 소스 구독을 시작하는 멀티캐스트 Observable입니다.
 * @see <a href="https://rxjs.dev/api/index/function/connectable">Guide</a>
 */
@JsType(isNative = true, namespace = "rxjs", name = "Observable")
public class Connectable<T> extends Observable<T> {
    protected Connectable() {}
    /** 소스 구독을 시작합니다. 반환한 Subscription을 해제하면 연결을 끊습니다. */
    public native Subscription connect();
}

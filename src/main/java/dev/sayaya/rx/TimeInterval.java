package dev.sayaya.rx;

import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * timeInterval()이 방출하는 값. value와 직전 방출(또는 구독)부터의 경과 시간(ms)을 담습니다.
 * @see <a href="https://rxjs.dev/api/index/interface/TimeInterval">Guide</a>
 */
@JsType(isNative = true, namespace = GLOBAL, name = "Object")
public class TimeInterval<T> {
    public T value;
    public double interval;
}

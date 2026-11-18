package dev.sayaya.rx;

import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * timestamp()가 방출하는 값. value와 방출 시각(ms, epoch 기준)을 담습니다.
 * @see <a href="https://rxjs.dev/api/index/interface/Timestamp">Guide</a>
 */
@JsType(isNative = true, namespace = GLOBAL, name = "Object")
public class Timestamp<T> {
    public T value;
    public double timestamp;
}

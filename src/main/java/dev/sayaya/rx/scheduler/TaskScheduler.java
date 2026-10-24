package dev.sayaya.rx.scheduler;

import dev.sayaya.rx.Subscription;
import dev.sayaya.rx.function.Callback;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface TaskScheduler {
    Subscription schedule(Callback work);
}

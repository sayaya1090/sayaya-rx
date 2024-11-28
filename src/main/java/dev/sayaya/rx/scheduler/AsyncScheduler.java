package dev.sayaya.rx.scheduler;

import dev.sayaya.rx.function.Callback;
import jsinterop.annotations.JsOptional;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface AsyncScheduler<T> {
    void schedule(Task<T> task, int delay, @JsOptional T param);
    @JsOverlay default void schedule(Callback work, int delay) {
        schedule(n->work.call(), delay, null);
    }
    void schedule(T param);
}

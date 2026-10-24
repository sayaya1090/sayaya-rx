package dev.sayaya.rx.scheduler;

import dev.sayaya.rx.Subscription;
import dev.sayaya.rx.function.Callback;
import jsinterop.annotations.JsOptional;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public interface AsyncScheduler<T> {
    Subscription schedule(Task<T> task, int delay, @JsOptional T param);
    @JsOverlay default Subscription schedule(Callback work, int delay) {
        return schedule(n->work.call(), delay, null);
    }
    /**
     * 작업을 예약합니다. 작업 안에서 {@code reschedule.accept(next)}를 호출하면
     * 같은 작업이 같은 delay로 새 state와 함께 다시 예약됩니다.
     * 반환값은 첫 번째 예약의 Subscription입니다.
     */
    @JsOverlay default Subscription schedule(RecursiveTask<T> task, int delay, T state) {
        AsyncScheduler<T> self = this;
        return schedule((Task<T>) s -> task.exec(s, next -> self.schedule(task, delay, next)), delay, state);
    }
}

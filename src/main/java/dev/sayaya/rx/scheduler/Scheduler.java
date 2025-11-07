package dev.sayaya.rx.scheduler;

import jsinterop.annotations.JsProperty;

public class Scheduler {
    /**
     * setTimeout(task, duration)을 사용한 것처럼 작업 일정을 예약합니다.
     * @link <a href="https://rxjs.dev/api/index/const/asyncSchedulerr">Guide</a>
     */
    @JsProperty(namespace = "rxjs", name="asyncScheduler")
    public static native <T> AsyncScheduler<T> async();
    public static <T> AsyncScheduler<T> asyncScheduler(Class<T> clazz) {
        return async();
    }
    public static AsyncScheduler<Void> asyncScheduler() {
        return async();
    }
    /**
     * 비동기적으로 가능한 한 빨리 작업을 수행합니다.
     * @link <a href="https://rxjs.dev/api/index/const/asapScheduler">Guide</a>
     */
    @JsProperty(namespace = "rxjs", name="asapScheduler")
    public static native TaskScheduler asapScheduler();
    /**
     * 즉시 실행하는 대신 모든 다음 작업을 대기열에 넣습니다.
     * @link <a href="https://rxjs.dev/api/index/const/queueScheduler">Guide</a>
     */
    @JsProperty(namespace = "rxjs", name="queueScheduler")
    public static native TaskScheduler queueScheduler();

    @JsProperty(namespace = "rxjs", name="animationFrameScheduler")
    private static native <T> AsyncScheduler<T> animationFrame();
    public static <T> AsyncScheduler<T> animationFrameScheduler(Class<T> clazz) {
        return animationFrame();
    }
}

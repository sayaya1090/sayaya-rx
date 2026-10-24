package dev.sayaya.rx.scheduler;

import java.util.function.Consumer;

/**
 * 실행 중에 같은 작업을 새 state로 다시 예약할 수 있는 작업입니다.
 * RxJS의 {@code this.schedule(state)}(SchedulerAction)에 해당합니다.
 */
@FunctionalInterface
public interface RecursiveTask<T> {
    void exec(T state, Consumer<T> reschedule);
}

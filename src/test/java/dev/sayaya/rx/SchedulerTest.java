package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;

import static dev.sayaya.rx.Helper.*;
import static dev.sayaya.rx.scheduler.Scheduler.*;

public class SchedulerTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        testAsyncScheduler();
        testAsapScheduler();
        testQueueScheduler();
        testSchedulerOrder();
    }

    private static void testAsyncScheduler() {
        var result = new StringBuilder();
        result.append("Start;");
        asyncScheduler().schedule(()->result.append("Async;"), 500);
        asyncScheduler().schedule(()->{
            assertEquals("asyncScheduler() should execute after specified delay like setTimeout", "Start;Async;", result.toString());
        }, 1000);
    }

    private static void testAsapScheduler() {
        var result = new StringBuilder();
        result.append("Start;");
        asapScheduler().schedule(()->{
            result.append("First asap;");
            asapScheduler().schedule(()->{
                result.append("Second asap;");
            });
        });
        result.append("Sync;");
        asyncScheduler().schedule(()->{
            assertEquals("asapScheduler() should execute asynchronously as soon as possible", "Start;Sync;First asap;Second asap;", result.toString());
        }, 100);
    }

    private static void testQueueScheduler() {
        var result = new StringBuilder();
        result.append("Start;");
        queueScheduler().schedule(()->{
            result.append("First queue;");
            queueScheduler().schedule(()->{
                result.append("Second queue;");
            });
        });
        result.append("End;");
        assertEquals("queueScheduler() should execute synchronously in queue order", "Start;First queue;Second queue;End;", result.toString());
    }

    private static void testSchedulerOrder() {
        var result = new StringBuilder();

        // asyncScheduler: setTimeout처럼 동작
        asyncScheduler().schedule(()->result.append("Async;"), 1000);

        // asapScheduler: 가능한 빨리 비동기 실행
        asapScheduler().schedule(()->{
            asapScheduler().schedule(()->{
                result.append("Second asap;");
            });
            result.append("First asap;");
        });

        // queueScheduler: 즉시 실행하지 않고 대기열에 추가
        queueScheduler().schedule(()->{
            queueScheduler().schedule(()->{
                result.append("Second queue;");
            });
            result.append("First queue;");
        });

        result.append("Scheduler Test:");

        // 2초 후 결과 검증
        asyncScheduler().schedule(()->{
            assertEquals("All schedulers should execute in correct order based on their characteristics",
                    "First queue;Second queue;" +
                            "Scheduler Test:" +
                            "First asap;Second asap;" +
                            "Async;",
                    result.toString());
        }, 2000);
    }
}
package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

import static dev.sayaya.rx.Helper.*;
import static dev.sayaya.rx.scheduler.Scheduler.*;

public class SchedulerTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        // Playwright 등 외부 테스트 러너가 "페이지 로직이 끝났는지" 기다릴 수 있도록 플래그 제공
        Js.asPropertyMap(DomGlobal.window).set("__TEST_DONE__", false);

        testAsyncScheduler();
        testAsapScheduler();
        testQueueScheduler();
        testSchedulerOrder();

        // 가장 늦은 assert가 2000ms에 실행되므로 넉넉히 여유를 둔다.
        asyncScheduler().schedule(() -> {
            Js.asPropertyMap(DomGlobal.window).set("__TEST_DONE__", true);
            DomGlobal.console.log("__TEST_DONE__=true");
        }, 3500);
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
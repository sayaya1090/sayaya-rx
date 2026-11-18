
package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import dev.sayaya.rx.subject.AsyncSubject;
import dev.sayaya.rx.subject.ReplaySubject;
import dev.sayaya.rx.subject.Subject;
import elemental2.dom.DomGlobal;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

import static dev.sayaya.rx.Helper.*;
import static dev.sayaya.rx.Observable.of;
import static dev.sayaya.rx.scheduler.Scheduler.asyncScheduler;
import static dev.sayaya.rx.subject.AsyncSubject.async;
import static dev.sayaya.rx.subject.BehaviorSubject.behavior;
import static dev.sayaya.rx.subject.ReplaySubject.replayWithBuffer;
import static dev.sayaya.rx.subject.Subject.subject;

public class SubjectTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        // Playwright 등 외부 테스트 러너가 "페이지 로직이 끝났는지" 기다릴 수 있도록 플래그 제공
        Js.asPropertyMap(DomGlobal.window).set("__TEST_DONE__", false);
        testSubject();
        testBehaviorSubject();
        testReplaySubject();
        testAsyncSubject();
        testAsObservable();
        testGetValue();
        testReplayWithBufferAndWindowTime();
        testAwait();
        testJson();
        signalDoneWhenSettled(0);
    }

    private static void testSubject() {
        var result1 = new StringBuilder();
        var result2 = new StringBuilder();
        var subject = subject(Integer.class);
        subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
        subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
        var observable = of(1, 2, 3);
        observable.subscribe(subject);
        assertEquals("Subject should multicast to all observers", "observer A:1\nobserver A:2\nobserver A:3\n", result1.toString());
        assertEquals("Subject should multicast to all observers", "observer B:1\nobserver B:2\nobserver B:3\n", result2.toString());
    }

    private static void testBehaviorSubject() {
        var result = new StringBuilder();
        var subject = behavior("Hello,");
        subject.subscribe(result::append);
        subject.next(" World!");
        assertEquals("BehaviorSubject should emit initial value and subsequent values", "Hello, World!", result.toString());
    }

    private static void testReplaySubject() {
        var result1 = new StringBuilder();
        var result2 = new StringBuilder();
        var subject = replayWithBuffer(Integer.class, 3);
        subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
        subject.next(1);
        subject.next(2);
        subject.next(3);
        subject.next(4);
        subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
        subject.next(5);
        assertEquals("ReplaySubject should replay all values to first observer", "observer A:1\nobserver A:2\nobserver A:3\nobserver A:4\nobserver A:5\n", result1.toString());
        assertEquals("ReplaySubject should replay buffered values to late observer", "observer B:2\nobserver B:3\nobserver B:4\nobserver B:5\n", result2.toString());
    }

    private static void testAsyncSubject() {
        var result1 = new StringBuilder();
        var result2 = new StringBuilder();
        var subject = async(Integer.class);
        subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
        subject.next(1);
        subject.next(2);
        subject.next(3);
        subject.next(4);
        subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
        subject.next(5);
        assertEquals("AsyncSubject should not emit before completion", "", result1.toString());
        assertEquals("AsyncSubject should not emit before completion", "", result2.toString());
        subject.complete();
        assertEquals("AsyncSubject should emit only last value after completion", "observer A:5\n", result1.toString());
        assertEquals("AsyncSubject should emit only last value after completion", "observer B:5\n", result2.toString());
    }

    private static void testAsObservable() {
        var result = new StringBuilder();
        Subject<Integer> subject = subject();
        Observable<Integer> view = subject.asObservable();
        view.subscribe(v -> result.append(v).append(","));
        subject.next(1);
        subject.next(2);
        assertEquals("asObservable() should relay values from the subject", "1,2,", result.toString());
        assertTrue("asObservable() should not expose the Subject itself", !(view instanceof Subject));
    }

    private static void testGetValue() {
        var subject = behavior("a");
        var first = subject.getValue();
        subject.next("b");
        assertEquals("getValue() should return the latest value of BehaviorSubject", "a;b", first + ";" + subject.getValue());
    }

    private static void testReplayWithBufferAndWindowTime() {
        var result1 = new StringBuilder();
        ReplaySubject<Integer> buffered = ReplaySubject.replayWithBufferAndWindowTime(2, 10000);
        buffered.next(1);
        buffered.next(2);
        buffered.next(3);
        buffered.subscribe(v -> result1.append(v).append(","));
        assertEquals("replayWithBufferAndWindowTime() should replay up to buffer size", "2,3,", result1.toString());

        var result2 = new StringBuilder();
        ReplaySubject<Integer> windowed = ReplaySubject.replayWithBufferAndWindowTime(10, 100);
        windowed.next(1);
        asyncScheduler().schedule(() -> {
            windowed.next(2);
            windowed.subscribe(v -> result2.append(v).append(","));
        }, 300);
        eventually("replayWithBufferAndWindowTime() should drop values older than the window time", "2,", result2, 4000);
    }

    private static void testAwait() {
        var result = new StringBuilder();
        AsyncSubject<String> subject = AsyncSubject.await(Promise.resolve("done"));
        subject.subscribe(v -> result.append(v).append(","));
        eventually("await() should emit the resolved value of the promise", "done,", result, 4000);
    }

    private static void testJson() {
        var result = new StringBuilder();
        AsyncSubject<JsPropertyMap<Object>> subject = AsyncSubject.json(DomGlobal.fetch("rx-test-data.json"));
        subject.subscribe(v -> result.append(v.get("ok")));
        eventually("json() should emit the parsed JSON body of the response", "true", result, 4000);
    }
}

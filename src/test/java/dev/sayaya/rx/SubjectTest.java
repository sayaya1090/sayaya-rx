
package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;

import static dev.sayaya.rx.Helper.*;
import static dev.sayaya.rx.Observable.of;
import static dev.sayaya.rx.subject.AsyncSubject.async;
import static dev.sayaya.rx.subject.BehaviorSubject.behavior;
import static dev.sayaya.rx.subject.ReplaySubject.replayWithBuffer;
import static dev.sayaya.rx.subject.Subject.subject;

public class SubjectTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        testSubject();
        testBehaviorSubject();
        testReplaySubject();
        testAsyncSubject();
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
}
package dev.sayaya.rx;

import dev.sayaya.rx.subject.AsyncSubject;
import dev.sayaya.rx.subject.BehaviorSubject;
import dev.sayaya.rx.subject.ReplaySubject;
import dev.sayaya.rx.subject.Subject;
import elemental2.core.JsError;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.sayaya.rx.Observable.*;
import static dev.sayaya.rx.subject.AsyncSubject.async;
import static dev.sayaya.rx.subject.BehaviorSubject.behavior;
import static dev.sayaya.rx.subject.ReplaySubject.replayWithBuffer;
import static dev.sayaya.rx.subject.Subject.subject;
import static elemental2.dom.DomGlobal.document;

public class RxJsTest extends GwtJsInteropTestCase {
    @Override
    public String getModuleName() {
        return "dev.sayaya.Rx";
    }
    public void test() {
        delayTestFinish(5000);
        waitForScriptToLoad("https://unpkg.com/rxjs/dist/bundles/rxjs.umd.min.js", () -> {
            _testObservableGenerator();
            _testOperator();
            _testSubject();
            finishTest();
        });
    }
    private void _testObservableGenerator() {
        StringBuilder result = new StringBuilder();
        // of
        of(10, 20, 30).subscribe(new Observer<Integer>() {
            @Override
            public void next(Integer value) {
                result.append("next:").append(value).append('\n');
            }
            @Override
            public void error(JsError e) {
                result.append("error:").append(e.message).append('\n');
            }
            @Override
            public void complete() {
                result.append("complete");
            }
        });
        assertEquals("next:10\nnext:20\nnext:30\ncomplete", result.toString());

        // fromEvent
        AtomicBoolean called = new AtomicBoolean(false);
        fromEvent(document, "click").subscribe(evt->called.set(true));
        document.documentElement.click();
        assertTrue("fromEvent allows subscribing to events emitted by the obtained Observable", called.get());
    }
    private void _testOperator() {
        {
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).map(x->x*2).subscribe(x->result.append(x).append(","));
            assertEquals("20,40,60,", result.toString());
        } {
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).scan(Integer::sum).subscribe(x->result.append(x).append(","));
            assertEquals("10,30,30,", result.toString());
        } {
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).filter(x->x>10).subscribe(x->result.append(x).append(","));
            assertEquals("20,30,", result.toString());
        } {
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).take(2).subscribe(x->result.append(x).append(","));
            assertEquals("10,20,", result.toString());
        } {
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).skip(2).subscribe(x->result.append(x).append(","));
            assertEquals("30,", result.toString());
        } {
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).startWith(0).subscribe(x->result.append(x).append(","));
            assertEquals("0,10,20,30,", result.toString());
        }
    }
    private void _testSubject() {
        {
            StringBuilder result1 = new StringBuilder();
            StringBuilder result2 = new StringBuilder();
            Subject<Integer> subject = subject(Integer.class);
            subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
            subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
            Observable<Integer> observable = of(1, 2, 3);
            observable.subscribe(subject);
            assertEquals("observer A:1\nobserver A:2\nobserver A:3\n", result1.toString());
            assertEquals("observer B:1\nobserver B:2\nobserver B:3\n", result2.toString());
        }{
            StringBuilder result = new StringBuilder();
            BehaviorSubject<String> subject = behavior("Hello,");
            subject.subscribe(result::append);
            subject.next(" World!");
            assertEquals("Hello, World!", result.toString());
        }{
            StringBuilder result1 = new StringBuilder();
            StringBuilder result2 = new StringBuilder();
            ReplaySubject<Integer> subject = replayWithBuffer(Integer.class, 3);
            subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
            subject.next(1);
            subject.next(2);
            subject.next(3);
            subject.next(4);
            subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
            subject.next(5);
            assertEquals("observer A:1\nobserver A:2\nobserver A:3\nobserver A:4\nobserver A:5\n", result1.toString());
            assertEquals("observer B:2\nobserver B:3\nobserver B:4\nobserver B:5\n", result2.toString());
        }{
            StringBuilder result1 = new StringBuilder();
            StringBuilder result2 = new StringBuilder();
            AsyncSubject<Integer> subject = async(Integer.class);
            subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
            subject.next(1);
            subject.next(2);
            subject.next(3);
            subject.next(4);
            subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
            subject.next(5);
            assertEquals("", result1.toString());
            assertEquals("", result2.toString());
            subject.complete();
            assertEquals("observer A:5\n", result1.toString());
            assertEquals("observer B:5\n", result2.toString());

        }
    }
}

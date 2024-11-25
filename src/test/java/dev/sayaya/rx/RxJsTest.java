package dev.sayaya.rx;

import com.google.gwt.core.client.Scheduler;
import dev.sayaya.rx.subject.AsyncSubject;
import dev.sayaya.rx.subject.BehaviorSubject;
import dev.sayaya.rx.subject.ReplaySubject;
import dev.sayaya.rx.subject.Subject;
import elemental2.core.JsError;
import elemental2.dom.DomGlobal;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.sayaya.rx.Observable.*;
import static dev.sayaya.rx.subject.AsyncSubject.async;
import static dev.sayaya.rx.subject.BehaviorSubject.behavior;
import static dev.sayaya.rx.subject.ReplaySubject.replayWithBuffer;
import static dev.sayaya.rx.subject.Subject.subject;
import static elemental2.dom.DomGlobal.document;
import static org.jboss.elemento.EventType.click;

public class RxJsTest extends GwtJsInteropTestCase {
    @Override
    public String getModuleName() {
        return "dev.sayaya.Rx";
    }
    public void test() {
        delayTestFinish(15000);
        waitForScriptToLoad("https://unpkg.com/rxjs/dist/bundles/rxjs.umd.min.js", () -> {
            _testObservableGenerator();
            _testOperator();
            _testSubject();
            Scheduler.get().scheduleFixedDelay(()->{
                finishTest();
                return false;
            }, 5000);
        });
    }
    private void _testObservableGenerator() {
        {   // of
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).subscribe(observer(result));
            assertEquals("10,20,30,|", result.toString());
        } { // range
            StringBuilder result = new StringBuilder();
            range(1, 10).subscribe(observer(result));
            assertEquals("1,2,3,4,5,6,7,8,9,10,|", result.toString());
        } { // fromEvent
            AtomicBoolean called = new AtomicBoolean(false);
            fromMouseEvent(document, click).subscribe(evt->called.set(true));
            document.documentElement.click();
            assertTrue("fromEvent allows subscribing to events emitted by the obtained Observable", called.get());
        } { // ajax
            {
                StringBuilder result = new StringBuilder();
                fromJson("https://api.github.com/users?per_page=5", GitHubUser[].class).subscribe(x -> {
                    for (GitHubUser u : x) result.append(u.id).append(",");
                });
                Scheduler.get().scheduleFixedDelay(() -> {
                    assertEquals("1,2,3,4,5,", result.toString());
                    return false;
                }, 2000);
            } {
                StringBuilder result = new StringBuilder();
                // AjaxRequest request = AjaxRequest.builder().url("https://api.github.com/users?per_page=5").build();
                AjaxRequest request = new AjaxRequest();
                request.url = "https://api.github.com/users?per_page=5";
                fromRequest(request, GitHubUser[].class).subscribe(x-> {
                    result.append(x.responseType).append(";")
                          .append(x.status).append(";");
                    for(GitHubUser u: x.response) result.append(u.id).append(",");
                });
                Scheduler.get().scheduleFixedDelay(()->{
                    assertEquals("json;200;1,2,3,4,5,", result.toString());
                    return false;
                }, 2000);
            } {
                StringBuilder result = new StringBuilder();
                AjaxRequest request = new AjaxRequest();
                request.url = "https://api.github.com/invalid-url";
                fromRequest(request).catchError(e -> of(e)).subscribe(e->result.append("error:").append(e.message));
                Scheduler.get().scheduleFixedDelay(()->{
                    assertEquals("error:ajax error 404", result.toString());
                    return false;
                }, 2000);
            }
        }
    }
    private void _testOperator() {
        {   // map
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).map(x->x*2).subscribe(observer(result));
            assertEquals("20,40,60,|", result.toString());
        } { // scan
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).scan(Integer::sum).subscribe(observer(result));
            assertEquals("10,30,60,|", result.toString());
        } { // filter
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).filter(x->x>10).subscribe(observer(result));
            assertEquals("20,30,|", result.toString());
        } {
            // take
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).take(2).subscribe(observer(result));
            assertEquals("10,20,|", result.toString());
        } { // skip
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).skip(2).subscribe(observer(result));
            assertEquals("30,|", result.toString());
        } { // retry
            StringBuilder result = new StringBuilder();
            Subject<Integer> obs = replayWithBuffer(Integer.class, 10);
            obs.retry(2).subscribe(observer(result));
            obs.next(1);
            obs.next(2);
            obs.next(3);
            obs.error(new JsError("error"));
            assertEquals("1,2,3,1,2,3,1,2,3,X", result.toString());
        } { // startWith
            StringBuilder result = new StringBuilder();
            of(10, 20, 30).startWith(0).subscribe(x->result.append(x).append(","));
            assertEquals("0,10,20,30,", result.toString());
        } { // concatAll
            StringBuilder result = new StringBuilder();
            Subject<Integer> obs1 = replayWithBuffer(Integer.class, 10);
            Subject<Integer> obs2 = replayWithBuffer(Integer.class, 10);
            Subject<Integer> obs3 = replayWithBuffer(Integer.class, 10);
            Subject<Subject<Integer>> outer = behavior(obs1);
            outer.concatAll(Integer.class).subscribe(x->result.append(x).append(","));
            obs1.next(1);
            outer.next(obs2);
            obs2.next(3);
            outer.next(obs3);
            obs2.next(4);
            obs3.next(5);
            obs2.complete();
            obs1.next(2);
            obs3.next(6);
            obs3.complete();
            obs1.complete();
            assertEquals("1,2,3,4,5,6,", result.toString());
        } { // switchAll
            StringBuilder result = new StringBuilder();
            Subject<Integer> obs1 = replayWithBuffer(Integer.class, 10);
            Subject<Integer> obs2 = replayWithBuffer(Integer.class, 10);
            Subject<Subject<Integer>> outer = behavior(obs1);
            outer.switchAll(Integer.class).subscribe(x->result.append(x).append(","));
            obs1.next(1);
            obs1.next(2);
            outer.next(obs2);
            obs1.next(3);
            obs2.next(5);
            obs1.next(4);
            obs2.next(6);
            obs1.complete();
            obs2.next(7);
            obs2.complete();
            assertEquals("1,2,5,6,7,", result.toString());
        } { // mergeAll
            StringBuilder result = new StringBuilder();
            Subject<Integer> obs1 = replayWithBuffer(Integer.class, 10);
            Subject<Integer> obs2 = replayWithBuffer(Integer.class, 10);
            Subject<Subject<Integer>> outer = behavior(obs1);
            outer.mergeAll(Integer.class).subscribe(x->result.append(x).append(","));
            obs1.next(1);
            obs1.next(2);
            outer.next(obs2);
            obs1.next(3);
            obs2.next(5);
            obs1.next(4);
            obs2.next(6);
            obs1.complete();
            obs2.next(7);
            obs2.complete();
            assertEquals("1,2,3,5,4,6,7,", result.toString());
        } { // exhaustAll
            StringBuilder result = new StringBuilder();
            Subject<Integer> obs1 = replayWithBuffer(Integer.class, 10);
            Subject<Integer> obs2 = replayWithBuffer(Integer.class, 10);
            Subject<Integer> obs3 = replayWithBuffer(Integer.class, 10);
            Subject<Subject<Integer>> outer = behavior(obs1);
            outer.exhaustAll(Integer.class).subscribe(x->result.append(x).append(","));
            obs1.next(1);
            obs1.next(2);
            outer.next(obs2);
            obs1.next(3);
            obs2.next(4);
            obs2.next(5);
            obs1.complete();
            outer.next(obs3);
            outer.complete();
            obs3.next(7);
            obs2.next(6);
            obs3.next(8);
            obs2.complete();
            obs3.next(9);
            obs3.complete();
            assertEquals("1,2,3,7,8,9,", result.toString());
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
        } {
            StringBuilder result = new StringBuilder();
            BehaviorSubject<String> subject = behavior("Hello,");
            subject.subscribe(result::append);
            subject.next(" World!");
            assertEquals("Hello, World!", result.toString());
        } {
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
        } {
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
    private static Observer<Integer> observer(StringBuilder result) {
        return new Observer<Integer>() {
            @Override
            public void next(Integer value) {
                result.append(value).append(",");
            }
            @Override
            public void error(JsError error) {
                result.append("X");
            }
            @Override
            public void complete() {
                result.append("|");
            }
        };
    }

    @JsType(isNative = true)
    public static class GitHubUser {
        Double id;
        String login;
        @JsProperty(name="node_id")
        String nodeId;
        @JsProperty(name="avatar_url")
        String avatarUrl;
        @JsProperty(name="gravatar_id")
        String gravatarId;
        String url;
        String type;
        @JsProperty(name="user_view_type")
        String userViewType;
        @JsProperty(name="site_admin")
        Boolean siteAdmin;
    }
}

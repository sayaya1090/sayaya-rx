package dev.sayaya.rx;

import elemental2.core.JsError;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static dev.sayaya.rx.Observable.*;
import static dev.sayaya.rx.scheduler.Scheduler.*;
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
            _testScheduler();
            asyncScheduler().schedule(this::finishTest, 10000);
        });
    }
    private void _testObservableGenerator() {
        {   // of
            var result = new StringBuilder();
            of(10, 20, 30).subscribe(observer(result));
            assertEquals("10,20,30,|", result.toString());
        } { // range
            var result = new StringBuilder();
            range(1, 10).subscribe(observer(result));
            assertEquals("1,2,3,4,5,6,7,8,9,10,|", result.toString());
        } { // fromEvent
            var called = new AtomicBoolean(false);
            fromMouseEvent(document, click).subscribe(evt->called.set(true));
            document.documentElement.click();
            assertTrue("fromEvent allows subscribing to events emitted by the obtained Observable", called.get());
        } { // ajax
            {
                var result = new StringBuilder();
                fetchJson("https://api.github.com/users?per_page=5", GitHubUser[].class).subscribe(x -> {
                    for (GitHubUser u : x) result.append(u.id).append(",");
                });
                asyncScheduler().schedule(()->{
                    assertEquals("1,2,3,4,5,", result.toString());
                }, 2000);
            } {
                var result = new StringBuilder();
                // AjaxRequest request = AjaxRequest.builder().url("https://api.github.com/users?per_page=5").build();
                var request = new AjaxRequest();
                request.url = "https://api.github.com/users?per_page=5";
                fromRequest(request, GitHubUser[].class).subscribe(x-> {
                    result.append(x.responseType).append(";")
                            .append(x.status).append(";");
                    for(GitHubUser u: x.response) result.append(u.id).append(",");
                });
                asyncScheduler().schedule(()->{
                    assertEquals("json;200;1,2,3,4,5,", result.toString());
                }, 2000);
            } {
                var result = new StringBuilder();
                var request = new AjaxRequest();
                request.url = "https://api.github.com/invalid-url";
                fromRequest(request).catchError(e -> of(e)).subscribe(e->result.append("error:").append(e.message));
                asyncScheduler().schedule(()->{
                    assertEquals("error:ajax error 404", result.toString());
                }, 2000);
            }
        } { // timer
            var result = new StringBuilder();
            timer(0, 100).take(5).subscribe(observer(result));
            assertEquals("", result.toString());
            asyncScheduler().schedule(()->{
                assertEquals("0,1,2,3,4,|", result.toString());
            }, 2000);
        } {
            // merge
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->100+i);
            var obs2 = timer(0, 30);
            merge(obs1, obs2).take(10).subscribe(observer(result));
            assertEquals("", result.toString());
            asyncScheduler().schedule(()->{
                assertEquals("100,0,1,2,3,101,4,5,6,102,|", result.toString());
            }, 2000);
        } {
            // forkJoin
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->100+i).take(5); // 100, 101, 102, 103, 104
            var obs2 = timer(0, 30).take(5); // 0, 1, 2, 3, 4
            forkJoin(obs1, obs2).map(arr -> {
                var i1 = Integer.parseInt(arr[0].toString()); // 마지막 값이므로 104
                var i2 = Integer.parseInt(arr[1].toString()); // 마지막 값이므로 4
                return i1 + i2;
            }).subscribe(observer(result));
            assertEquals("", result.toString());
            asyncScheduler().schedule(()->{
                assertEquals("108,|", result.toString());
            }, 2000);
        }
    }
    private void _testOperator() {
        {   // map
            var result = new StringBuilder();
            of(10, 20, 30).map(x->x*2).subscribe(observer(result));
            assertEquals("20,40,60,|", result.toString());
        } {   // tap
            {
                var result = new StringBuilder();
                var result2 = new StringBuilder();
                range(0, 10).tap(i->result2.append(i).append(",")).subscribe(observer(result));
                assertEquals("0,1,2,3,4,5,6,7,8,9,", result2.toString());
                assertEquals("0,1,2,3,4,5,6,7,8,9,|", result.toString());
            } {
                var result = new StringBuilder();
                var result2 = new StringBuilder();
                range(0, 10).tap(observer(result2)).subscribe(observer(result));
                assertEquals("0,1,2,3,4,5,6,7,8,9,|", result2.toString());
                assertEquals(result2.toString(), result.toString());
            }
        } { // mergeMap
            {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
                var obs2 = timer(0, 60).map(i->10).take(3);
                obs1.mergeMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
                asyncScheduler().schedule(()->{
                    assertEquals("10,10,10,30,30,40,30,40,40,|", result.toString());
                }, 2000);
            } {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
                var obs2 = timer(0, 60).map(i->10).take(3);
                obs1.mergeMap(obs2).subscribe(observer(result));
                asyncScheduler().schedule(()->{
                    assertEquals("10,10,10,10,10,10,10,10,10,|", result.toString());
                }, 2000);
            }
        } { // concatMap
            {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
                var obs2 = timer(0, 60).map(i->10).take(3);
                obs1.concatMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
                asyncScheduler().schedule(()->{
                    assertEquals("10,10,10,30,30,30,40,40,40,|", result.toString());
                }, 2000);
            } {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
                var obs2 = timer(0, 60).map(i->10).take(3);
                obs1.concatMap(obs2).subscribe(observer(result));
                asyncScheduler().schedule(()->{
                    assertEquals("10,10,10,10,10,10,10,10,10,|", result.toString());
                }, 2000);
            }
        } { // concatWith
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.concatWith(obs2).subscribe(observer(result));
            asyncScheduler().schedule(()->{
                assertEquals("1,3,4,10,10,10,|", result.toString());
            }, 2000);
        } { // switchMap
            {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
                var obs2 = timer(0, 60).map(i->10).take(3);
                obs1.switchMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
                asyncScheduler().schedule(()->{
                    assertEquals("10,10,10,30,30,40,40,40,|", result.toString());
                }, 2000);
            } {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
                var obs2 = timer(0, 60).map(i->10).take(3);
                obs1.switchMap(obs2).subscribe(observer(result));
                asyncScheduler().schedule(()->{
                    assertEquals("10,10,10,10,10,10,10,10,|", result.toString());
                }, 2000);
            }
        } { // zip
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->(Object) String.valueOf((char) ('a' + i))).take(5);
            var obs2 = timer(0, 60).take(4);
            obs1.zip(obs2).map(arr-> arr.get(0).toString() + arr.get(1)).subscribe(s->result.append(s).append(","));
            asyncScheduler().schedule(()->{
                assertEquals("a0,b1,c2,d3,", result.toString());
            }, 2000);
        } { // combineLatest
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->(Object) String.valueOf((char) ('a' + i))).take(5);
            var obs2 = timer(0, 60).take(4);
            obs1.combineLatest(obs2).map(arr-> arr.get(0).toString() + arr.get(1)).subscribe(s->result.append(s).append(","));
            asyncScheduler().schedule(()->{
                assertEquals("a0,a1,b1,b2,b3,c3,d3,e3,", result.toString());
            }, 2000);
        } { // bufferTime
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->(Object) String.valueOf((char) ('a' + i))).take(10);
            obs1.bufferTime( 200).map(arr->"[" + Arrays.stream(arr).map(Object::toString).collect(Collectors.joining(",")) + "]").subscribe(s->result.append(s).append(","));
            asyncScheduler().schedule(()->{
                assertEquals("[a,b],[c,d],[e,f],[g,h],[i,j],", result.toString());
            }, 2000);
        } { // windowTime
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->(Object) String.valueOf((char) ('a' + i))).take(10);
            obs1.windowTime(200).subscribe(obs->
                    obs.take(1).subscribe(s->result.append(s).append(","))
            );
            asyncScheduler().schedule(()->{
                assertEquals("a,c,e,g,i,", result.toString());
            }, 2000);
        } { // scan
            var result = new StringBuilder();
            of(10, 20, 30).scan(Integer::sum).subscribe(observer(result));
            assertEquals("10,30,60,|", result.toString());
        } { // debounce
            {
                var result = new StringBuilder();
                var obs1 = timer(0, 100).filter(i->{
                    if(i > 0 && i < 3) return false;
                    else return i < 5 || i >= 8;
                }).map(i->(Object) String.valueOf((char) ('a' + i))).take(12);
                obs1.debounce(a->timer(0, 150).skip(1).take(1)).subscribe(s->result.append(s).append(","));
                asyncScheduler().schedule(()->{
                    assertEquals("a,e,q,", result.toString());
                }, 4000);
            } {
                var result = new StringBuilder();
                var obs = timer(0, 100).filter(i->{
                    if(i > 0 && i < 3) return false;
                    else return i < 5 || i >= 8;
                }).map(i->(Object) String.valueOf((char) ('a' + i))).take(12);
                obs.debounceTime(150).subscribe(s->result.append(s).append(","));
                asyncScheduler().schedule(()->{
                    assertEquals("a,e,q,", result.toString());
                }, 4000);
            }
        } { // throttle
            var result = new StringBuilder();
            var obs = timer(0, 100).filter(i->{
                if(i > 0 && i < 3) return false;
                else return i < 5 || i >= 8;
            }).map(i->(Object) String.valueOf((char) ('a' + i))).take(12);
            obs.throttleTime(150).subscribe(s->result.append(s).append(","));
            asyncScheduler().schedule(()->{
                assertEquals("a,d,i,k,m,o,q,", result.toString());
            }, 4000);
        } { // distinct
            {
                var result = new StringBuilder();
                of(1, 1, 2, 2, 2, 1, 2, 3, 4, 3, 2, 1).distinct().subscribe(observer(result));
                assertEquals("1,2,3,4,|", result.toString());
            } {
                var result = new StringBuilder();
                of(1, 1, 2, 2, 2, 1, 2, 3, 4, 3, 2, 1).distinct(a->a%2).subscribe(observer(result));
                assertEquals("1,2,|", result.toString());
            }
        } { // distinctUntilChanged
            {
                var result = new StringBuilder();
                of(1, 1, 2, 2, 2, 4, 2, 3, 4, 3, 2, 1).distinctUntilChanged().subscribe(observer(result));
                assertEquals("1,2,4,2,3,4,3,2,1,|", result.toString());
            } {
                var result = new StringBuilder();
                of(1, 1, 2, 2, 2, 4, 2, 3, 4, 3, 2, 1).distinctUntilChanged(Comparator.comparing(a->a%2)).subscribe(observer(result));
                assertEquals("1,2,3,4,3,2,1,|", result.toString());
            }
        } { // filter
            var result = new StringBuilder();
            of(10, 20, 30).filter(x->x>10).subscribe(observer(result));
            assertEquals("20,30,|", result.toString());
        } {
            // take
            var result = new StringBuilder();
            of(10, 20, 30).take(2).subscribe(observer(result));
            assertEquals("10,20,|", result.toString());
        } { // skip
            var result = new StringBuilder();
            of(10, 20, 30).skip(2).subscribe(observer(result));
            assertEquals("30,|", result.toString());
        } { // retry
            var result = new StringBuilder();
            var obs = replayWithBuffer(Integer.class, 10);
            obs.retry(2).subscribe(observer(result));
            obs.next(1);
            obs.next(2);
            obs.next(3);
            obs.error(new JsError("error"));
            assertEquals("1,2,3,1,2,3,1,2,3,X", result.toString());
        } { // startWith
            var result = new StringBuilder();
            of(10, 20, 30).startWith(0).subscribe(x->result.append(x).append(","));
            assertEquals("0,10,20,30,", result.toString());
        } { // concatAll
            var result = new StringBuilder();
            var obs1 = replayWithBuffer(Integer.class, 10);
            var obs2 = replayWithBuffer(Integer.class, 10);
            var obs3 = replayWithBuffer(Integer.class, 10);
            var outer = behavior(obs1);
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
            var result = new StringBuilder();
            var obs1 = replayWithBuffer(Integer.class, 10);
            var obs2 = replayWithBuffer(Integer.class, 10);
            var outer = behavior(obs1);
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
            var result = new StringBuilder();
            var obs1 = replayWithBuffer(Integer.class, 10);
            var obs2 = replayWithBuffer(Integer.class, 10);
            var outer = behavior(obs1);
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
            var result = new StringBuilder();
            var obs1 = replayWithBuffer(Integer.class, 10);
            var obs2 = replayWithBuffer(Integer.class, 10);
            var obs3 = replayWithBuffer(Integer.class, 10);
            var outer = behavior(obs1);
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
        } { // finalize
            {
                var result = new StringBuilder();
                range(0, 5).finalize(()->result.append(" Sequence Complete")).subscribe(observer(result));
                assertEquals("0,1,2,3,4,| Sequence Complete", result.toString());
            } {
                var result = new StringBuilder();
                var obs = timer(0, 100).finalize(()->result.append(" Sequence Complete"));
                var subscription = obs.subscribe(i->result.append(i).append(","));
                asyncScheduler().schedule(()->{
                    subscription.unsubscribe();
                    assertTrue(result.toString().endsWith("Sequence Complete"));
                }, 500);
            }
        }
    }
    private void _testSubject() {
        {
            var result1 = new StringBuilder();
            var result2 = new StringBuilder();
            var subject = subject(Integer.class);
            subject.subscribe(v-> result1.append("observer A:").append(v).append("\n"));
            subject.subscribe(v-> result2.append("observer B:").append(v).append("\n"));
            var observable = of(1, 2, 3);
            observable.subscribe(subject);
            assertEquals("observer A:1\nobserver A:2\nobserver A:3\n", result1.toString());
            assertEquals("observer B:1\nobserver B:2\nobserver B:3\n", result2.toString());
        } {
            var result = new StringBuilder();
            var subject = behavior("Hello,");
            subject.subscribe(result::append);
            subject.next(" World!");
            assertEquals("Hello, World!", result.toString());
        } {
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
            assertEquals("observer A:1\nobserver A:2\nobserver A:3\nobserver A:4\nobserver A:5\n", result1.toString());
            assertEquals("observer B:2\nobserver B:3\nobserver B:4\nobserver B:5\n", result2.toString());
        } {
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
            assertEquals("", result1.toString());
            assertEquals("", result2.toString());
            subject.complete();
            assertEquals("observer A:5\n", result1.toString());
            assertEquals("observer B:5\n", result2.toString());
        }
    }
    private void _testScheduler() {
        var result = new StringBuilder();
        asyncScheduler().schedule(()->result.append("Async;"), 1000);
        asapScheduler().schedule(()->{
            asapScheduler().schedule(()->{
                result.append("Second asap;");
            });
            result.append("First asap;");
        });
        queueScheduler().schedule(()->{
            queueScheduler().schedule(()->{
                result.append("Second queue;");
            });
            result.append("First queue;");
        });
        result.append("Scheduler Test:");
        asyncScheduler().schedule(()->
                        assertEquals("First queue;Second queue;" +
                                "Scheduler Test:" +
                                "First asap;Second asap;" +
                                "Async;", result.toString())
                , 2000);

        var sc = animationFrameScheduler(Integer.class);
        sc.schedule(height->{
            result.append(height).append(";");
            sc.schedule(height+1);
        }, 0, 0);
    }
    private static Observer<Integer> observer(StringBuilder result) {
        return new Observer<>() {
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
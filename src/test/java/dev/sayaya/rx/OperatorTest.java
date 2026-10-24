package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import elemental2.core.JsError;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static dev.sayaya.rx.Helper.*;
import static dev.sayaya.rx.Observable.*;
import static dev.sayaya.rx.scheduler.Scheduler.asyncScheduler;
import static dev.sayaya.rx.subject.BehaviorSubject.behavior;
import static dev.sayaya.rx.subject.ReplaySubject.replayWithBuffer;

public class OperatorTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        // Playwright 등 외부 테스트 러너가 "페이지 로직이 끝났는지" 기다릴 수 있도록 플래그 제공
        Js.asPropertyMap(DomGlobal.window).set("__TEST_DONE__", false);
        testMap();
        testTap();
        testMergeMap();
        testConcatMap();
        testConcatWith();
        testSwitchMap();
        testZip();
        testCombineLatest();
        testBufferTime();
        testWindowTime();
        testScan();
        testDebounce();
        testThrottle();
        testDistinct();
        testDistinctUntilChanged();
        testFilter();
        testTake();
        testSkip();
        testRetry();
        testStartWith();
        testConcatAll();
        testSwitchAll();
        testMergeAll();
        testExhaustAll();
        testFinalize();
        testSubscribeLambdaReportsError();
        testFromArray();
        testFromPromise();
        testAddedApis();
        testRemainingApis();

        // 현재 켜져 있는 테스트들은 4초( debounce/throttle ) 뒤에 assert가 수행됨.
        // 약간의 여유를 두고 완료 플래그를 올린다.
        // 고정 지연으로 남은 assert(완료 표시가 없는 debounce/throttle/buffer 등, 최대 4000ms)가 실행된 뒤,
        // eventually 검사가 모두 끝나면 완료 신호를 올린다.
        signalDoneWhenSettled(4500);
    }

    private static void testMap() {
        var result = new StringBuilder();
        of(10, 20, 30).map(x->x*2).subscribe(observer(result));
        assertEquals("map() should transform each value", "20,40,60,|", result.toString());
    }

    private static void testTap() {
        {
            var result = new StringBuilder();
            var result2 = new StringBuilder();
            range(0, 10).tap(i->result2.append(i).append(",")).subscribe(observer(result));
            assertEquals("tap() should perform side effect without affecting stream", "0,1,2,3,4,5,6,7,8,9,", result2.toString());
            assertEquals("tap() should not modify emitted values", "0,1,2,3,4,5,6,7,8,9,|", result.toString());
        }
        {
            var result = new StringBuilder();
            var result2 = new StringBuilder();
            range(0, 10).tap(observer(result2)).subscribe(observer(result));
            assertEquals("tap() with observer should mirror stream", "0,1,2,3,4,5,6,7,8,9,|", result2.toString());
            assertEquals("tap() with observer should not modify stream", result2.toString(), result.toString());
        }
    }

    private static void testMergeMap() {
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.mergeMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
            eventually("mergeMap() should flatten and merge inner observables concurrently", "10,10,10,30,30,40,30,40,40,|", result, 4000);
        }
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.mergeMap(obs2).subscribe(observer(result));
            eventually("mergeMap() with constant observable should merge all emissions", "10,10,10,10,10,10,10,10,10,|", result, 4000);
        }
    }

    private static void testConcatMap() {
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.concatMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
            eventually("concatMap() should flatten and concatenate inner observables sequentially", "10,10,10,30,30,30,40,40,40,|", result, 4000);
        }
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.concatMap(obs2).subscribe(observer(result));
            eventually("concatMap() with constant observable should concatenate all emissions", "10,10,10,10,10,10,10,10,10,|", result, 4000);
        }
    }

    private static void testConcatWith() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
        var obs2 = timer(0, 60).map(i->10).take(3);
        obs1.concatWith(obs2).subscribe(observer(result));
        eventually("concatWith() should concatenate observables in sequence", "1,3,4,10,10,10,|", result, 4000);
    }

    private static void testSwitchMap() {
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.switchMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
            eventually("switchMap() should switch to latest inner observable", "10,10,10,30,30,40,40,40,|", result, 4000);
        }
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.switchMap(obs2).subscribe(observer(result));
            eventually("switchMap() with constant observable should switch on each emission", "10,10,10,10,10,10,10,10,|", result, 4000);
        }
    }

    private static void testZip() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->(Object) String.valueOf((char) ('a' + i))).take(5);
        var obs2 = timer(0, 60).take(4);
        obs1.zip(obs2).map(arr-> arr.get(0).toString() + arr.get(1)).subscribe(s->result.append(s).append(","));
        asyncScheduler().schedule(()->{
            assertEquals("zip() should pair values at same index", "a0,b1,c2,d3,", result.toString());
        }, 2000);
    }

    private static void testCombineLatest() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->(Object) String.valueOf((char) ('a' + i))).take(5);
        var obs2 = timer(0, 60).take(4);
        obs1.combineLatest(obs2).map(arr-> arr[0].toString() + arr[1]).subscribe(s->result.append(s).append(","));
        asyncScheduler().schedule(()->{
            assertEquals("combineLatest() should combine latest values from each observable", "a0,a1,b1,b2,b3,c3,d3,e3,", result.toString());
        }, 2000);
    }

    private static void testBufferTime() {
        var result = new StringBuilder();
        var obs1 = timer(0, 200).map(i->(Object) String.valueOf((char) ('a' + i))).take(6);
        obs1.bufferTime(500).subscribe(arr->{
            result.append("[").append(Arrays.stream(arr).map(Object::toString).collect(Collectors.joining(","))).append("],");
        });
        asyncScheduler().schedule(()->{
            // 정확한 값 대신 버퍼링이 동작했는지만 확인
            var str = result.toString();
            assertTrue("bufferTime() should create buffers with brackets", str.contains("[") && str.contains("]"));
            assertTrue("bufferTime() should buffer multiple values", str.contains(","));
            assertTrue("bufferTime() should create multiple buffers", str.split("\\[").length > 2);
        }, 2000);
    }

    private static void testWindowTime() {
        var result = new StringBuilder();
        var obs1 = timer(0, 200).map(i->(Object) String.valueOf((char) ('a' + i))).take(6);
        var windowCount = new java.util.concurrent.atomic.AtomicInteger(0);
        obs1.windowTime(500).subscribe(obs->{
            windowCount.incrementAndGet();
            obs.take(1).subscribe(s->result.append(s).append(","));
        });
        asyncScheduler().schedule(()->{
            // 윈도우가 생성되고 값이 방출되는지만 확인
            assertTrue("windowTime() should create multiple windows", windowCount.get() > 1);
            assertTrue("windowTime() should emit values from windows", result.length() > 0);
            assertTrue("windowTime() should contain letters", result.toString().matches(".*[a-z].*"));
        }, 2000);
    }

    private static void testScan() {
        var result = new StringBuilder();
        of(10, 20, 30).scan(Integer::sum).subscribe(observer(result));
        assertEquals("scan() should accumulate values over time", "10,30,60,|", result.toString());
    }

    private static void testDebounce() {
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).filter(i->{
                if(i > 0 && i < 3) return false;
                else return i < 5 || i >= 8;
            }).map(i->(Object) String.valueOf((char) ('a' + i))).take(12);
            obs1.debounce(a->timer(0, 150).skip(1).take(1)).subscribe(s->result.append(s).append(","));
            asyncScheduler().schedule(()->{
                assertEquals("debounce() should emit values after silence period", "a,e,q,", result.toString());
            }, 4000);
        }
        {
            var result = new StringBuilder();
            var obs = timer(0, 100).filter(i->{
                if(i > 0 && i < 3) return false;
                else return i < 5 || i >= 8;
            }).map(i->(Object) String.valueOf((char) ('a' + i))).take(12);
            obs.debounceTime(150).subscribe(s->result.append(s).append(","));
            asyncScheduler().schedule(()->{
                assertEquals("debounceTime() should emit values after time silence", "a,e,q,", result.toString());
            }, 4000);
        }
    }

    private static void testThrottle() {
        var result = new StringBuilder();
        var obs = timer(0, 100).filter(i->{
            if(i > 0 && i < 3) return false;
            else return i < 5 || i >= 8;
        }).map(i->(Object) String.valueOf((char) ('a' + i))).take(12);
        obs.throttleTime(150).subscribe(s->result.append(s).append(","));
        asyncScheduler().schedule(()->{
            assertEquals("throttleTime() should emit first value then silence for duration", "a,d,i,k,m,o,q,", result.toString());
        }, 4000);
    }

    private static void testDistinct() {
        {
            var result = new StringBuilder();
            of(1, 1, 2, 2, 2, 1, 2, 3, 4, 3, 2, 1).distinct().subscribe(observer(result));
            assertEquals("distinct() should filter out duplicate values", "1,2,3,4,|", result.toString());
        }
        {
            var result = new StringBuilder();
            of(1, 1, 2, 2, 2, 1, 2, 3, 4, 3, 2, 1).distinct(a->a%2).subscribe(observer(result));
            assertEquals("distinct() with selector should filter by key", "1,2,|", result.toString());
        }
    }

    private static void testDistinctUntilChanged() {
        {
            var result = new StringBuilder();
            of(1, 1, 2, 2, 2, 4, 2, 3, 4, 3, 2, 1).distinctUntilChanged().subscribe(observer(result));
            assertEquals("distinctUntilChanged() should filter consecutive duplicates", "1,2,4,2,3,4,3,2,1,|", result.toString());
        }
        {
            var result = new StringBuilder();
            of(1, 1, 2, 2, 2, 4, 2, 3, 4, 3, 2, 1).distinctUntilChanged(Comparator.comparing(a->a%2)).subscribe(observer(result));
            assertEquals("distinctUntilChanged() with comparator should filter by comparison", "1,2,3,4,3,2,1,|", result.toString());
        }
    }

    private static void testFilter() {
        var result = new StringBuilder();
        of(10, 20, 30).filter(x->x>10).subscribe(observer(result));
        assertEquals("filter() should emit only values that pass predicate", "20,30,|", result.toString());
    }

    private static void testTake() {
        var result = new StringBuilder();
        of(10, 20, 30).take(2).subscribe(observer(result));
        assertEquals("take() should emit only first n values", "10,20,|", result.toString());
    }

    private static void testSkip() {
        var result = new StringBuilder();
        of(10, 20, 30).skip(2).subscribe(observer(result));
        assertEquals("skip() should skip first n values", "30,|", result.toString());
    }

    private static void testRetry() {
        var result = new StringBuilder();
        var obs = replayWithBuffer(Integer.class, 10);
        obs.retry(2).subscribe(observer(result));
        obs.next(1);
        obs.next(2);
        obs.next(3);
        obs.error(new JsError("error"));
        assertEquals("retry() should resubscribe on error up to count times", "1,2,3,1,2,3,1,2,3,X", result.toString());
    }

    private static void testStartWith() {
        var result = new StringBuilder();
        of(10, 20, 30).startWith(0).subscribe(x->result.append(x).append(","));
        assertEquals("startWith() should emit initial value before stream", "0,10,20,30,", result.toString());
    }

    private static void testConcatAll() {
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
        assertEquals("concatAll() should concatenate inner observables sequentially", "1,2,3,4,5,6,", result.toString());
    }

    private static void testSwitchAll() {
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
        assertEquals("switchAll() should switch to latest inner observable", "1,2,5,6,7,", result.toString());
    }

    private static void testMergeAll() {
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
        assertEquals("mergeAll() should merge all inner observable emissions", "1,2,3,5,4,6,7,", result.toString());
    }

    private static void testExhaustAll() {
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
        assertEquals("exhaustAll() should ignore new inner observables while current is active", "1,2,3,7,8,9,", result.toString());
    }

    private static void testFinalize() {
        {
            var result = new StringBuilder();
            range(0, 5).finalize(()->result.append(" Sequence Complete")).subscribe(observer(result));
            assertEquals("finalize() should execute callback on completion", "0,1,2,3,4,| Sequence Complete", result.toString());
        }
        {
            var result = new StringBuilder();
            var obs = timer(0, 100).finalize(()->result.append(" Sequence Complete"));
            var subscription = obs.subscribe(i->result.append(i).append(","));
            asyncScheduler().schedule(()->{
                subscription.unsubscribe();
                assertTrue("finalize() should execute callback on unsubscribe", result.toString().endsWith("Sequence Complete"));
            }, 500);
        }
    }
    private static void testSubscribeLambdaReportsError() {
        var result = new StringBuilder();
        var config = Js.asPropertyMap(Js.asPropertyMap(DomGlobal.window).get("rxjs")).get("config");
        var cfg = Js.asPropertyMap(config);
        Object prev = cfg.get("onUnhandledError");
        cfg.set("onUnhandledError", (dev.sayaya.rx.function.VoidFunction<Object>) e -> result.append("reported;"));
        of(1, 2).map(i -> {
            if (i == 2) throw new IllegalStateException("boom");
            return i;
        }).subscribe(v -> result.append(v).append(","));
        eventually("subscribe(lambda) should report unhandled stream errors instead of swallowing them", "1,reported;", result, 3000, () -> {
            cfg.set("onUnhandledError", prev);
            // 핸들러를 복원한 뒤에 다음 에러 보고 테스트를 시작해, 두 테스트의 핸들러가 겹치지 않게 한다.
            testObserverNextReportsError();
        });
    }
    private static void testFromArray() {
        var result = new StringBuilder();
        try {
            Observable.<Integer>from(new Object[]{1, 2, 3}).subscribe(observer(result));
        } catch (Throwable e) { result.append("ERR:").append(e); }
        eventually("from() with array should emit each element", "1,2,3,|", result, 2000);
    }
    private static void testFromPromise() {
        var result = new StringBuilder();
        Observable.<Integer>from(elemental2.promise.Promise.resolve(7)).subscribe(observer(result));
        eventually("from() with promise should emit resolved value", "7,|", result, 2300);
    }
    private static void later(String name, String expected, StringBuilder r, int ms) {
        eventually(name, expected, r, Math.max(ms * 2, ms + 2000));
    }
    private static void testAddedApis() {
        { var r = new StringBuilder();
          Observable.<Integer>from(Arrays.asList(4, 5)).subscribe(observer(r));
          later("from() with list should emit each element", "4,5,|", r, 0); }
        { var r = new StringBuilder();
          timer(0, 100).takeUntil(timer(250)).subscribe(observer(r));
          later("takeUntil() should complete when notifier emits", "0,1,2,|", r, 800); }
        { var r = new StringBuilder();
          of(10, 20, 30).first().subscribe(observer(r));
          of(10, 20, 30).first(x -> x > 15).subscribe(observer(r));
          later("first() should emit the first (matching) value", "10,|20,|", r, 0); }
        { var r = new StringBuilder();
          of(10, 20, 30).last().subscribe(observer(r));
          of(10, 20, 30).last(x -> x < 25).subscribe(observer(r));
          later("last() should emit the last (matching) value", "30,|20,|", r, 0); }
        { var r = new StringBuilder();
          of(1, 2, 3, 4).reduce((a, v) -> a + v).subscribe(observer(r));
          of(1, 2, 3).reduce((String a, Integer v) -> a + v, "s").subscribe(v -> r.append(v).append(";"));
          later("reduce() should accumulate to a single value", "10,|s123;", r, 0); }
        { var r = new StringBuilder();
          of(1).delay(200).subscribe(observer(r));
          asyncScheduler().schedule(() -> r.append("@100;"), 100);
          later("delay() should postpone emissions", "@100;1,|", r, 500); }
        { var r = new StringBuilder(); int[] count = {0};
          var shared = Observable.<Integer>defer(() -> { count[0]++; return timer(0, 50).take(2); }).share();
          shared.subscribe(v -> r.append("A").append(v).append(","));
          shared.subscribe(v -> r.append("B").append(v).append(","));
          asyncScheduler().schedule(() -> r.append("count=").append(count[0]), 400);
          later("share() should multicast a single source subscription", "A0,B0,A1,B1,count=1", r, 500); }
        { var r = new StringBuilder(); int[] count = {0};
          var replayed = Observable.<Integer>defer(() -> { count[0]++; return of(1, 2, 3); }).shareReplay(1);
          replayed.subscribe(v -> r.append("A").append(v).append(","));
          replayed.subscribe(v -> r.append("B").append(v).append(","));
          r.append("count=").append(count[0]);
          later("shareReplay() should replay the last value without resubscribing", "A1,A2,A3,B3,count=1", r, 0); }
        { var r = new StringBuilder();
          var latest = behavior("x");
          of(1, 2).withLatestFrom(latest).subscribe(arr -> r.append(arr[0]).append(arr[1]).append(","));
          later("withLatestFrom() should combine with the latest value of other", "1x,2x,", r, 0); }
        { var r = new StringBuilder();
          timer(0, 100).take(4).exhaustMap(i -> timer(150).map(x -> i)).subscribe(observer(r));
          later("exhaustMap() should ignore new values while inner is active", "0,2,|", r, 900); }
        { var r = new StringBuilder();
          of(1, 2, 3).toArray().subscribe(list -> { for (var v : list) r.append(v).append(","); r.append(list.size()); });
          later("toArray() should collect all values into a list", "1,2,3,3", r, 0); }
        { var r = new StringBuilder();
          of(1, 2, 3, 1).takeWhile(x -> x < 3).subscribe(observer(r));
          later("takeWhile() should stop at the first failing value", "1,2,|", r, 0); }
        { var r = new StringBuilder();
          of(1, 2, 3).pairwise().subscribe(p -> r.append(p.get(0)).append("-").append(p.get(1)).append(","));
          later("pairwise() should emit consecutive pairs", "1-2,2-3,", r, 0); }
        { var r = new StringBuilder();
          concat(of(1, 2), of(3)).subscribe(observer(r));
          later("concat() should emit observables in sequence", "1,2,3,|", r, 0); }
        { var r = new StringBuilder(); int[] c = {0};
          var d = Observable.<Integer>defer(() -> { c[0]++; return of(1); });
          r.append(c[0]).append(";");
          d.subscribe(observer(r));
          r.append(c[0]);
          later("defer() should create the observable lazily per subscription", "0;1,|1", r, 0); }
        { var r = new StringBuilder();
          Observable.<Integer>empty().subscribe(observer(r));
          later("empty() should complete immediately without values", "|", r, 0); }
        { var r = new StringBuilder();
          Observable.<Integer>throwError(new JsError("bad")).subscribe(v -> r.append(v), e -> r.append("E:").append(e.message));
          later("throwError() should deliver the error to the error callback", "E:bad", r, 0); }
        { var r = new StringBuilder();
          of(1, 2).subscribe(v -> r.append(v).append(","), e -> r.append("E"), () -> r.append("|"));
          later("subscribe(next,error,complete) should call each callback", "1,2,|", r, 0); }
        { var r = new StringBuilder();
          timer(100).subscribe(observer(r));
          later("timer(due) should emit 0 once after due", "0,|", r, 400); }
        { var r = new StringBuilder();
          fromEvent(DomGlobal.window, "resize").subscribe(e -> r.append(e.type));
          DomGlobal.window.dispatchEvent(new elemental2.dom.Event("resize"));
          later("fromEvent(EventTarget) should accept window as target", "resize", r, 0); }
        { var r = new StringBuilder();
          var s1 = asyncScheduler().schedule(() -> r.append("async;"), 100);
          var s2 = dev.sayaya.rx.scheduler.Scheduler.asapScheduler().schedule(() -> r.append("asap;"));
          asyncScheduler().schedule(() -> r.append("kept;"), 100);
          s1.unsubscribe();
          s2.unsubscribe();
          later("schedule() returns Subscription that cancels the task", "kept;", r, 400); }
        { var r = new StringBuilder();
          Observable.<Integer>throwError(new JsError("x")).catchError(e -> of("recovered:" + e.message)).subscribe(v -> r.append(v).append(";"));
          Observable.<Integer>throwError(new JsError("y")).catchError((e, caught) -> of("again")).subscribe(v -> r.append(v).append(";"));
          later("catchError() should switch to a fallback observable of a new type", "recovered:x;again;", r, 0); }
    }
    private static void testRemainingApis() {
        { var r = new StringBuilder();
          of(1, 2, 3).scan((String acc, Integer v) -> acc + v, "s").subscribe(v -> r.append(v).append(";"));
          later("scanWithSeed() should accumulate from the seed", "s1;s12;s123;", r, 0); }
        { var r = new StringBuilder();
          of(1, 2, 3).throttleTime(100, asyncScheduler(Integer.class)).subscribe(observer(r));
          later("throttleTimeScheduler() should accept a scheduler", "1,|", r, 300); }
        { var r = new StringBuilder();
          of(1, 2, 3).throttleTime(100, false, true).subscribe(observer(r));
          later("throttleTimeConfig() should honor leading=false, trailing=true", "3,|", r, 500); }
        { var r = new StringBuilder();
          dev.sayaya.rx.subject.Subject<Integer> s = dev.sayaya.rx.subject.Subject.subject();
          s.subscribe(v -> r.append("S").append(v).append(","));
          s.next(1);
          dev.sayaya.rx.subject.ReplaySubject<Integer> rp = dev.sayaya.rx.subject.ReplaySubject.replay();
          rp.next(1); rp.next(2);
          rp.subscribe(v -> r.append("R").append(v).append(","));
          dev.sayaya.rx.subject.ReplaySubject<Integer> rb = dev.sayaya.rx.subject.ReplaySubject.replayWithBuffer(1);
          rb.next(1); rb.next(2);
          rb.subscribe(v -> r.append("B").append(v).append(","));
          dev.sayaya.rx.subject.AsyncSubject<Integer> as = dev.sayaya.rx.subject.AsyncSubject.async();
          as.subscribe(v -> r.append("A").append(v).append(","));
          as.next(1); as.next(2); as.complete();
          later("subjectFactories() without Class argument should work", "S1,R1,R2,B2,A2,", r, 0); }
    }
    private static void testObserverNextReportsError() {
        { var r = new StringBuilder();
          var cfg = Js.asPropertyMap(Js.asPropertyMap(DomGlobal.window).get("rxjs")).get("config");
          var map = Js.asPropertyMap(cfg);
          Object prev = map.get("onUnhandledError");
          map.set("onUnhandledError", (dev.sayaya.rx.function.VoidFunction<Object>) e -> r.append("reported;"));
          of(1, 2).map(i -> {
              if (i == 2) throw new IllegalStateException("boom");
              return i;
          }).subscribe(Observer.next(v -> r.append(v).append(",")));
          eventually("Observer.next() should report unhandled errors instead of swallowing them", "1,reported;", r, 3000, () -> map.set("onUnhandledError", prev)); }
    }
}

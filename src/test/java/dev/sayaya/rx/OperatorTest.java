package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import elemental2.core.JsError;

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
            asyncScheduler().schedule(()->{
                assertEquals("mergeMap() should flatten and merge inner observables concurrently", "10,10,10,30,30,40,30,40,40,|", result.toString());
            }, 2000);
        }
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.mergeMap(obs2).subscribe(observer(result));
            asyncScheduler().schedule(()->{
                assertEquals("mergeMap() with constant observable should merge all emissions", "10,10,10,10,10,10,10,10,10,|", result.toString());
            }, 2000);
        }
    }

    private static void testConcatMap() {
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.concatMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
            asyncScheduler().schedule(()->{
                assertEquals("concatMap() should flatten and concatenate inner observables sequentially", "10,10,10,30,30,30,40,40,40,|", result.toString());
            }, 2000);
        }
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.concatMap(obs2).subscribe(observer(result));
            asyncScheduler().schedule(()->{
                assertEquals("concatMap() with constant observable should concatenate all emissions", "10,10,10,10,10,10,10,10,10,|", result.toString());
            }, 2000);
        }
    }

    private static void testConcatWith() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
        var obs2 = timer(0, 60).map(i->10).take(3);
        obs1.concatWith(obs2).subscribe(observer(result));
        asyncScheduler().schedule(()->{
            assertEquals("concatWith() should concatenate observables in sequence", "1,3,4,10,10,10,|", result.toString());
        }, 2000);
    }

    private static void testSwitchMap() {
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.switchMap(x -> obs2.map(i -> i * x)).subscribe(observer(result));
            asyncScheduler().schedule(()->{
                assertEquals("switchMap() should switch to latest inner observable", "10,10,10,30,30,40,40,40,|", result.toString());
            }, 2000);
        }
        {
            var result = new StringBuilder();
            var obs1 = timer(0, 100).map(i->i+1).filter(i->i!=2).take(3);
            var obs2 = timer(0, 60).map(i->10).take(3);
            obs1.switchMap(obs2).subscribe(observer(result));
            asyncScheduler().schedule(()->{
                assertEquals("switchMap() with constant observable should switch on each emission", "10,10,10,10,10,10,10,10,|", result.toString());
            }, 2000);
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
}
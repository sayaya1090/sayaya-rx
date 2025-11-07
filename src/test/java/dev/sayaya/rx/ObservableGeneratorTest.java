package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import dev.sayaya.rx.Helper.GitHubUser;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.sayaya.rx.Helper.*;
import static dev.sayaya.rx.scheduler.Scheduler.asyncScheduler;
import static elemental2.dom.DomGlobal.document;
import static org.jboss.elemento.EventType.click;
import static dev.sayaya.rx.Observable.*;

public class ObservableGeneratorTest implements EntryPoint {
    @Override
    public void onModuleLoad() {
        testOf();
        testRange();
        testFromEvent();
        testAjax();
        testTimer();
        testMerge();
        testForkJoin();
    }
    private static void testOf() {
        var result = new StringBuilder();
        of(10, 20, 30).subscribe(observer(result));
        assertEquals("of() should emit values in sequence", "10,20,30,|", result.toString());
    }
    private static void testRange() {
        var result = new StringBuilder();
        range(1, 10).subscribe(observer(result));
        assertEquals("range() should emit sequential numbers", "1,2,3,4,5,6,7,8,9,10,|", result.toString());
    }
    private static void testFromEvent() {
        var called = new AtomicBoolean(false);
        fromMouseEvent(document, click).subscribe(evt->called.set(true));
        document.documentElement.click();
        assertTrue("fromEvent() should capture DOM events", called.get());
    }
    private static void testAjax() {{
        var result = new StringBuilder();
        fetchJson("https://api.github.com/users?per_page=5", GitHubUser[].class).subscribe(x -> {
            for (GitHubUser u : x) result.append(u.id).append(",");
        });
        asyncScheduler().schedule(() -> assertEquals("ajax: fetchJson() should fetch and parse JSON data", "1,2,3,4,5,", result.toString()), 2000);
    }{
        var result = new StringBuilder();
        var request = new AjaxRequest();
        request.url = "https://api.github.com/users?per_page=5";
        fromRequest(request, GitHubUser[].class).subscribe(x-> {
            result.append(x.responseType).append(";")
                    .append(x.status).append(";");
            for(var u: x.response) result.append(u.id).append(",");
        });
        asyncScheduler().schedule(()->{
            assertEquals("ajax: fromRequest() should return full response with metadata", "json;200;1,2,3,4,5,", result.toString());
        }, 2000);
    } {
        var result = new StringBuilder();
        var request = new AjaxRequest();
        request.url = "https://api.github.com/invalid-url";
        fromRequest(request).catchError(e -> of(e)).subscribe(e->result.append("error:").append(e.message));
        asyncScheduler().schedule(()->{
            assertEquals("ajax: fromRequest() should handle HTTP errors properly", "error:ajax error", result.toString());
        }, 2000);
    }}
    private static void testTimer() {
        var result = new StringBuilder();
        timer(0, 100).take(5).subscribe(observer(result));
        assertEquals("timer() should start with empty result", "", result.toString());
        asyncScheduler().schedule(()->{
            assertEquals("timer() should emit values at regular intervals", "0,1,2,3,4,|", result.toString());
        }, 2000);
    }
    private static void testMerge() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->100+i);
        var obs2 = timer(0, 30);
        merge(obs1, obs2).take(10).subscribe(observer(result));
        assertEquals("merge() should start with empty result", "", result.toString());
        asyncScheduler().schedule(()->{
            assertEquals("merge() should interleave emissions from multiple observables", "100,0,1,2,3,101,4,5,6,102,|", result.toString());
        }, 2000);
    }
    private static void testForkJoin() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->100+i).take(5); // 100, 101, 102, 103, 104
        var obs2 = timer(0, 30).take(5); // 0, 1, 2, 3, 4
        forkJoin(obs1, obs2).map(arr -> {
            var i1 = Integer.parseInt(arr[0].toString()); // 마지막 값이므로 104
            var i2 = Integer.parseInt(arr[1].toString()); // 마지막 값이므로 4
            return i1 + i2;
        }).subscribe(observer(result));
        assertEquals("forkJoin() should start with empty result", "", result.toString());
        asyncScheduler().schedule(()->{
            assertEquals("forkJoin() should wait for all observables to complete and emit last values", "108,|", result.toString());
        }, 2000);
    }
}

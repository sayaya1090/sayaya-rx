package dev.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import elemental2.dom.DomGlobal;
import jsinterop.base.Js;
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
        // Playwright 등 외부 테스트 러너가 "페이지 로직이 끝났는지" 기다릴 수 있도록 플래그 제공
        Js.asPropertyMap(DomGlobal.window).set("__TEST_DONE__", false);
        testOf();
        testRange();
        testFromEvent();
        testAjax();
        testTimer();
        testMerge();
        testForkJoin();
        testInterval();
        testAjaxHeaders();
        // 모든 지연 assert가 eventually로 바뀌었으므로, 검사가 모두 끝나면 완료 신호를 올린다.
        signalDoneWhenSettled(0);
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
        fetchJson("users.json", GitHubUser[].class).subscribe(x -> {
            for (GitHubUser u : x) result.append(u.id).append(",");
        });
        eventually("ajax: fetchJson() should fetch and parse JSON data", "1,2,3,4,5,", result, 4000);
    }{
        var result = new StringBuilder();
        var request = new AjaxRequest();
        request.url = "users.json";
        fromRequest(request, GitHubUser[].class).subscribe(x-> {
            result.append(x.responseType).append(";")
                    .append(x.status).append(";");
            for(var u: x.response) result.append(u.id).append(",");
        });
        eventually("ajax: fromRequest() should return full response with metadata", "json;200;1,2,3,4,5,", result, 4000);
    } {
        var result = new StringBuilder();
        var request = new AjaxRequest();
        request.url = "does-not-exist.json";
        fromRequest(request).catchError(e -> of(e)).subscribe(e->result.append("error:").append(e.message));
        eventually("ajax: fromRequest() should handle HTTP errors properly", "error:ajax error 404", result, 4000);
    }}
    private static void testTimer() {
        var result = new StringBuilder();
        timer(0, 100).take(5).subscribe(observer(result));
        assertEquals("timer() should start with empty result", "", result.toString());
        eventually("timer() should emit values at regular intervals", "0,1,2,3,4,|", result, 4000);
    }
    private static void testMerge() {
        var result = new StringBuilder();
        var obs1 = timer(0, 100).map(i->100+i);
        var obs2 = timer(0, 30);
        merge(obs1, obs2).take(10).subscribe(observer(result));
        assertEquals("merge() should start with empty result", "", result.toString());
        eventually("merge() should interleave emissions from multiple observables", "100,0,1,2,3,101,4,5,6,102,|", result, 4000);
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
        eventually("forkJoin() should wait for all observables to complete and emit last values", "108,|", result, 4000);
    }
    private static void testInterval() {
        var result = new StringBuilder();
        interval(50).take(3).map(i -> i * 10).subscribe(observer(result));
        eventually("interval() should emit sequential integers at the given period", "0,10,20,|", result, 4000);
    }
    private static void testAjaxHeaders() {
        var result = new StringBuilder();
        var request = new AjaxRequest();
        request.url = "rx-test-data.json";
        request.headers = jsinterop.base.JsPropertyMap.of();
        request.headers.set("X-Test", "1");
        fromRequest(request).subscribe(new Observer<AjaxResponse<?>>() {
            @Override public void next(AjaxResponse<?> x) {
                try {
                    result.append(x.status).append(";")
                          .append(x.request.headers.get("x-test")).append(";")
                          .append(String.valueOf(x.headers.get("content-type")).contains("json"));
                } catch (Throwable e) { result.append("ERR:").append(e); }
            }
            @Override public void error(elemental2.core.JsError error) { result.append("X:").append(error.message); }
            @Override public void complete() { result.append("|"); }
        });
        eventually("headers(ajax): request headers should be sent and response headers should be readable", "200;1;true|", result, 4000);
    }
}

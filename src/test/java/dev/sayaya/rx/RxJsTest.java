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

public class RxJsTest {

    /*@Override
    public String getModuleName() {
        return "dev.sayaya.Rx";
    }
    public void test() {
        delayTestFinish(15000);
        waitForScriptToLoad("https://unpkg.com/rxjs/dist/bundles/rxjs.umd.min.js", () -> {
            _testOperator();
            _testScheduler();
            asyncScheduler().schedule(this::finishTest, 10000);
        });
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
    }*/
}
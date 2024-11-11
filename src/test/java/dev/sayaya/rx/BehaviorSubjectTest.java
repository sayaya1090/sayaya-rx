package dev.sayaya.rx;

import dev.sayaya.rx.subject.BehaviorSubject;

import java.util.logging.Logger;

public class BehaviorSubjectTest extends GwtRxTestCase {
    private final Logger logger = Logger.getLogger(getClass().getName());
    @Override
    public String getModuleName() {
        return "dev.sayaya.Rx";
    }
    public void testBehaviorSubject() {
        delayTestFinish(5000);
        waitForScriptToLoad("https://unpkg.com/rxjs/dist/bundles/rxjs.umd.min.js", () -> {
            BehaviorSubject<String> subject = BehaviorSubject.behavior("Hello, World!");
            subject.subscribe(logger::info);
            subject.next("Hello, RxJS!");
            assertTrue(true);
            finishTest();
        });
    }
}

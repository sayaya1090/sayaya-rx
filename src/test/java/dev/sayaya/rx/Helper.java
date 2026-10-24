package dev.sayaya.rx;

import elemental2.core.JsError;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static elemental2.dom.DomGlobal.console;
import static dev.sayaya.rx.scheduler.Scheduler.asyncScheduler;

public class Helper {
    private static int pending = 0;
    /**
     * 고정 지연 대신, 결과가 기대값에 도달할 때까지 20ms 간격으로 확인한 뒤 assert합니다.
     * timeoutMs 안에 도달하지 못하면 그 시점의 실제 값으로 실패합니다.
     * 결과가 확정되는 경우(완료 표시로 끝나거나 한 번에 만들어지는 값)에만 사용합니다.
     */
    public static void eventually(String testName, String expected, StringBuilder actual, int timeoutMs) {
        eventually(testName, expected, actual, timeoutMs, null);
    }
    /** before는 assert 직전에(성공·실패와 무관하게) 실행됩니다. 정리 작업에 씁니다. */
    public static void eventually(String testName, String expected, StringBuilder actual, int timeoutMs, Runnable before) {
        pending++;
        poll(testName, expected, actual, timeoutMs, before, elemental2.core.JsDate.now());
    }
    private static void poll(String testName, String expected, StringBuilder actual, int timeoutMs, Runnable before, double start) {
        if (expected.equals(actual.toString()) || elemental2.core.JsDate.now() - start >= timeoutMs) {
            pending--;
            if (before != null) before.run();
            assertEquals(testName, expected, actual.toString());
        } else {
            asyncScheduler().schedule(() -> poll(testName, expected, actual, timeoutMs, before, start), 20);
        }
    }
    /**
     * minMs가 지난 뒤, 진행 중인 eventually 검사가 모두 끝나면 window.__TEST_DONE__을 올립니다.
     * minMs는 아직 고정 지연으로 남은 assert가 실행될 시간입니다(없으면 0).
     */
    public static void signalDoneWhenSettled(int minMs) {
        asyncScheduler().schedule(Helper::signalIfSettled, minMs);
    }
    private static void signalIfSettled() {
        if (pending > 0) { asyncScheduler().schedule(Helper::signalIfSettled, 50); return; }
        jsinterop.base.Js.asPropertyMap(elemental2.dom.DomGlobal.window).set("__TEST_DONE__", true);
        console.log("__TEST_DONE__=true");
    }
    public static void assertEquals(String testName, String expected, String actual) {
        if (!expected.equals(actual)) {
            console.error(testName + ": ✗ Assertion failed! Expected: " + expected + ", but got: " + actual);
            throw new AssertionError("Expected: " + expected + ", but got: " + actual);
        } else {
            console.log(testName + ": ✓ Assertion passed: " + expected);
        }
    }
    public static void assertTrue(String testName, boolean value) {
        assertEquals(testName, String.valueOf(true), String.valueOf(value));
    }
    public static Observer<Integer> observer(StringBuilder result) {
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
        public Double id;
        public String login;
        @JsProperty(name="node_id") public String nodeId;
        @JsProperty(name="avatar_url") public String avatarUrl;
        @JsProperty(name="gravatar_id") public String gravatarId;
        public String url;
        public String type;
        @JsProperty(name="user_view_type") public String userViewType;
        @JsProperty(name="site_admin") public Boolean siteAdmin;
    }
}

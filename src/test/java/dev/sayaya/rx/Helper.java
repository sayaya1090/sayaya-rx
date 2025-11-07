package dev.sayaya.rx;

import elemental2.core.JsError;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static elemental2.dom.DomGlobal.console;

public class Helper {
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

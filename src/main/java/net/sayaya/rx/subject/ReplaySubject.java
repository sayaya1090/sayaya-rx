package net.sayaya.rx.subject;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="ReplaySubject")
public class ReplaySubject<T> extends SubjectJs<T> {
    @JsOverlay
    public static <T> ReplaySubject<T> replay(Class<T> clazz, int buffer) {
        return new ReplaySubject<>();
    }
    @JsOverlay
    public static <T> ReplaySubject<T> replayWithBuffer(Class<T> clazz, int buffer) {
        return new ReplaySubject<>(buffer);
    }
    @JsOverlay
    public static <T> ReplaySubject<T> replayWithBufferAndWindowTime(Class<T> clazz, int buffer, int windowTime) {
        return new ReplaySubject<>(buffer, windowTime);
    }
    protected ReplaySubject(){}
    protected ReplaySubject(int buffer){}
    protected ReplaySubject(int buffer, int windowTime){}
}

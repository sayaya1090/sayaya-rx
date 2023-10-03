package net.sayaya.rx.subject;

import lombok.experimental.Delegate;

public class BehaviorSubject<T> {
    public static <T> BehaviorSubject<T> behavior(T init) {
        return new BehaviorSubject<>(init);
    }
    @Delegate private final BehaviorSubjectJs<T> delegate;
    public BehaviorSubject(T init) { delegate = new BehaviorSubjectJs<>(init); }
}

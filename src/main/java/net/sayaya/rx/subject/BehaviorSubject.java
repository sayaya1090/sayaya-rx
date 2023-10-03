package net.sayaya.rx.subject;

import lombok.experimental.Delegate;
import net.sayaya.rx.HasValueChangeHandlers;

public class BehaviorSubject<T> implements HasValueChangeHandlers.ValueChangeEventListener<T> {
    public static <T> BehaviorSubject<T> behavior(T init) {
        return new BehaviorSubject<>(init);
    }
    @Delegate private final BehaviorSubjectJs<T> delegate;
    public BehaviorSubject(T init) { delegate = new BehaviorSubjectJs<>(init); }
    @Override
    public void handle(HasValueChangeHandlers.ValueChangeEvent<T> evt) {
        delegate.next(evt.value());
    }
}

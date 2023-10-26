package net.sayaya.rx.subject;

import lombok.experimental.Delegate;
import net.sayaya.rx.HasValueChangeHandlers;

public class BehaviorSubject<T> implements HasValueChangeHandlers.ValueChangeEventListener<T> {
    public static <T> BehaviorSubject<T> behavior(T init) {
        return new BehaviorSubject<>(init);
    }
    public static <T> BehaviorSubject<ListOf<T>> listOf(Class<T> clazz) {
        var list = new ListOf<T>();
        var behavior = new BehaviorSubject<>(list);
        list.onValueChange(evt->behavior.next(list));
        return behavior;
    }
    @Delegate private final BehaviorSubjectJs<T> delegate;
    public BehaviorSubject(T init) { delegate = new BehaviorSubjectJs<>(init); }
    @Override
    public void handle(HasValueChangeHandlers.ValueChangeEvent<T> evt) {
        delegate.next(evt.value());
    }
}

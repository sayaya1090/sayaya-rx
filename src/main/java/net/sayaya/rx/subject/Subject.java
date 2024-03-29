package net.sayaya.rx.subject;

import lombok.experimental.Delegate;
import net.sayaya.rx.HasValueChangeHandlers;

public class Subject<T> implements HasValueChangeHandlers.ValueChangeEventListener<T> {
    public static <T> Subject<T> subject(Class<T> clazz) {
        return new Subject<>();
    }
    public static <T> Subject<ListOf<T>> listOf(Class<T> clazz) {
        var list = new ListOf<T>();
        var subject = (Subject<ListOf<T>>) subject(list.getClass());
        list.onValueChange(evt->subject.next(list));
        return subject;
    }
    @Delegate private final SubjectJs<T> delegate;
    public Subject() { delegate = new SubjectJs<>(); }
    @Override
    public void handle(HasValueChangeHandlers.ValueChangeEvent<T> evt) {
        delegate.next(evt.value());
    }
}

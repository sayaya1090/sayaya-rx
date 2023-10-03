package net.sayaya.rx.subject;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import lombok.experimental.Delegate;

public class Subject<T> implements ValueChangeHandler<T> {
    public static <T> Subject<T> subject(Class<T> clazz) {
        return new Subject<>();
    }
    @Delegate private final SubjectJs<T> delegate;
    public Subject() { delegate = new SubjectJs<>(); }
    @Override
    public void onValueChange(ValueChangeEvent<T> event) {
        delegate.next(event.getValue());
    }
}

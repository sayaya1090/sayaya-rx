package net.sayaya.rx.subject;

import java.util.List;

public class BehaviorListSubject<T> extends BehaviorSubject<List<T>> {
    protected BehaviorListSubject(List<T> value){
        super(value);
    }
    public void add(T data) {
        getValue().add(data);
        next(getValue());
    }
    public void remove(T data) {
        getValue().remove(data);
        next(getValue());
    }
}

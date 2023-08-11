package net.sayaya.rx.subject;

import java.util.List;

public class BehaviorListSubject<T> extends BehaviorSubject<List<BehaviorSubject<T>>> {
    protected BehaviorListSubject(List<BehaviorSubject<T>> behaviors){
        super(behaviors);
    }
    public void add(BehaviorSubject<T> data) {
        getValue().add(data);
        next(getValue());
    }
    public void remove(BehaviorSubject<T> data) {
        getValue().remove(data);
        next(getValue());
    }
}

package net.sayaya.rx.subject;

import elemental2.dom.CustomEvent;
import net.sayaya.rx.HasValueChangeHandlers;
import net.sayaya.rx.Subscription;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.*;

import static net.sayaya.rx.subject.Subject.subject;

public final class ListOf<T> implements HasValueChangeHandlers<List<T>>, Iterable<T> {
    private final LinkedList<T> data = new LinkedList<>();
    private final Map<T, Subscription> subscriptions = new HashMap<>();
    private final List<ValueChangeEventListener<List<T>>> handlers = new LinkedList<>();
    public void add(T datum) {
        listen(datum);
        data.add(datum);
        fire();
    }
    private void listen(T datum) {
        var subject = subject(datum.getClass());
        var subscription = subject.subscribe(evt->fire());
        subscriptions.put(datum, subscription);
    }
    public void remove(T datum) {
        var subscription = subscriptions.get(datum);
        if(subscription!=null) {
            subscription.unsubscribe();
            subscriptions.remove(datum);
        }
        data.remove(datum);
        fire();
    }
    public void clear() {
        for(var subscription: subscriptions.values()) subscription.unsubscribe();
        data.clear();
        fire();
    }
    public void addAll(Collection<T> collection) {
        for(T datum: collection) {
            listen(datum);
            data.add(datum);
        }
        fire();
    }
    public T first() {
        return data.getFirst();
    }
    public T last() {
        return data.getLast();
    }
    public T pollFirst() {
        return data.pollFirst();
    }
    public T pollLast() {
        return data.pollLast();
    }
    public boolean contains(T obj) {
        return subscriptions.containsKey(obj);
    }
    public boolean isEmpty() {
        return data.isEmpty();
    }
    public T get(int index) {
        return data.get(index);
    }
    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }
    public int size() {
        return data.size();
    }
    private void fire() {
        var values = value();
        var dataUpdateEvt = HasValueChangeHandlers.ValueChangeEvent.event(new CustomEvent<>("change"), values);
        for(var handler: handlers) handler.handle(dataUpdateEvt);
    }
    @Override
    public List<T> value() {
        return new ArrayList<>(data);
    }
    @Override
    public HandlerRegistration onValueChange(ValueChangeEventListener<List<T>> valueChangeEventListener) {
        handlers.add(valueChangeEventListener);
        return ()->handlers.remove(valueChangeEventListener);
    }
}

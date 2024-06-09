package net.sayaya.rx;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import elemental2.dom.DomGlobal;

public class Test implements EntryPoint {
    @Override
    public void onModuleLoad() {
        var observable = new Observable<Integer>(subscriber->{
            subscriber.next(1);
            subscriber.next(2);
            subscriber.next(3);
            Scheduler.get().scheduleFixedDelay(()->{
                subscriber.next(4);
                //subscriber.error(new RuntimeException("RFFWEF"));
                subscriber.complete();
                subscriber.next(5);
                return false;
            }, 1000);
        });
        DomGlobal.console.log(observable);
        var subscription = observable.map(i->i/10.2).map(i->"[" + i + "]")
                .subscribe(x->{
                    DomGlobal.console.log(x);
                });
        var k = Observable.of("A", "B", "C");
        k.subscribe(x->DomGlobal.console.log(x));
        var subject = new Subject<Integer>();
        subject.subscribe(x->DomGlobal.console.log(x));
        subject.next(1000);
        subject.next(2000);
        Scheduler.get().scheduleFixedDelay(()->{
            subject.next(3000);
            //subscriber.error(new RuntimeException("RFFWEF"));
            subject.complete();
            subject.next(4000);
            return false;
        }, 3000);
        // subscription.unsubscribe();
    }
}

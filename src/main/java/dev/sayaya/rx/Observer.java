package dev.sayaya.rx;

import elemental2.dom.DomGlobal;
import jsinterop.annotations.*;

@JsType(isNative = true, namespace="rxjs", name="Observer")
public interface Observer<T> {
    @JsMethod void next(T value);
    @JsMethod void error(Throwable error);
    @JsMethod void complete();

    interface ObserverDefault<T> extends Observer<T> {
        default void error(Throwable error) {
            DomGlobal.console.log(error);
        }
        default void complete() {
            DomGlobal.console.log("Done");
        }
    }
}

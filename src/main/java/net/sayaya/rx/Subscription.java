package net.sayaya.rx;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="Subscription")
//@JsFunction
public class Subscription {
    native void unsubscribe();
}

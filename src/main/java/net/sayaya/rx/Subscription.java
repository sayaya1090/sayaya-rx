package net.sayaya.rx;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="Subscription")
public class Subscription {
    native void unsubscribe();
}

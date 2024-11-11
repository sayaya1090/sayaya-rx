package dev.sayaya.rx;

import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace="rxjs", name="Subscription")
public class Subscription {
   public native void unsubscribe();
}

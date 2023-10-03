package net.sayaya.rx;

import elemental2.dom.Event;
import elemental2.dom.EventListener;
import elemental2.dom.EventTarget;
import org.gwtproject.event.shared.HandlerRegistration;

import static org.jboss.elemento.EventType.bind;

public interface HasValueChangeHandlers<V> {
	V value();
	interface ValueChangeEventListener<V> {
		void handle(ValueChangeEvent<V> evt);
	}
	final class ValueChangeEvent<V> {
		private Event event;
		private V value;
		public static <V> ValueChangeEvent<V> event(Event event, V value) {
			ValueChangeEvent<V> evt = new ValueChangeEvent<V>();
			evt.event = event;
			evt.value = value;
			return evt;
		}
		private ValueChangeEvent(){}
		public Event event() {
			return event;
		}
		public ValueChangeEvent<V> event(Event evt) {
			this.event = evt;
			return this;
		}
		public V value() {
			return value;
		}
		public ValueChangeEvent<V> value(V value) {
			this.value = value;
			return this;
		}
	}
	HandlerRegistration onValueChange(ValueChangeEventListener<V> listener);
	default HandlerRegistration onValueChange(EventTarget dom, ValueChangeEventListener<V> listener) {
		EventListener wrapper = evt->listener.handle(new ValueChangeEvent<V>().event(evt).value(value()));
		return bind(dom, "change", wrapper);
	}
}
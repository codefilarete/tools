package org.gama.lang.function;

import java.util.function.Supplier;

/**
 * A simple interface to define that you can hang something on it, opposit to {@link Supplier}
 * 
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Hanger<T> {
	
	void set(T value);
	
	/**
	 * A {@link Hanger} and {@link Supplier}
	 * 
	 * @param <T>
	 */
	class Holder<T> implements Hanger<T>, Supplier<T> {
		
		private T payload;
		
		public Holder() {
		}
		
		public Holder(T payload) {
			this.payload = payload;
		}
		
		@Override
		public void set(T value) {
			this.payload = value;
		}
		
		@Override
		public T get() {
			return payload;
		}
	}
}

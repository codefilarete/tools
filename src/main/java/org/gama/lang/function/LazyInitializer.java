package org.gama.lang.function;

import java.util.function.Supplier;

/**
 * A {@link Supplier} for use case that needs a lazy instanciation of a variable.
 * Non thread-safe (would need a volatile internal variable and a internnaly synchronized get()
 * 
 * @param <T>
 */
public abstract class LazyInitializer<T> implements Supplier<T> {
	
	private T instance = null;
	
	public LazyInitializer() {
	}
	
	public T get() {
		if (this.instance == null) {
			this.instance = this.createInstance();
		}		
		return this.instance;
	}
	
	protected abstract T createInstance();
}

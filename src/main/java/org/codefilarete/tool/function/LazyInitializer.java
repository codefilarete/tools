package org.codefilarete.tool.function;

import java.util.function.Supplier;

/**
 * A {@link Supplier} for use case that needs a lazy instantiation of a variable.
 * Non thread-safe (would need a volatile internal variable and an internally synchronized get())
 * 
 * @param <T> the type of the created instance
 * @see ThreadSafeLazyInitializer
 */
public abstract class LazyInitializer<T> implements Supplier<T> {
	
	private T instance = null;
	
	public LazyInitializer() {
	}
	
	@Override
	public T get() {
		if (this.instance == null) {
			this.instance = this.createInstance();
		}		
		return this.instance;
	}
	
	/**
	 * Will be called once, for value initialization
	 * @return a new instance of the expected type
	 */
	protected abstract T createInstance();
}

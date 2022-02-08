package org.codefilarete.tool.function;

import java.util.function.Supplier;

/**
 * A {@link Supplier} for use case that needs a lazy instanciation of a variable.
 * Non thread-safe (would need a volatile internal variable and a internaly synchronized get())
 * 
 * @param <T> the type of the created instance
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
	
	/**
	 * Will be called once, for value initialization
	 * @return a new instance of the expected type
	 */
	protected abstract T createInstance();
}

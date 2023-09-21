package org.codefilarete.tool.function;

import java.util.function.Supplier;

/**
 * A {@link Supplier} for use case that needs a lazy instantiation of a variable in a thread-safe way.
 * Caller must override this class and implements {@link #createInstance()}
 *
 * @param <T> the type of the created instance
 * @see LazyInitializer
 */
public abstract class ThreadSafeLazyInitializer<T> implements Supplier<T> {
	
	private volatile T instance;
	
	/**
	 * Returns the object stored by this instance with thread-safe initialization.
	 * Object instantiation is delegated to {@link #createInstance()} since this method only handles thread-safe
	 * access to internal storage.
	 *
	 * @return the object initialized by {@link #createInstance()}
	 */
	@Override
	public T get() {
		// DO NOT TOUCH THIS ALGORITHM UNLESS YOU'RE AN EXPERT IN LAZY INITIALIZATION AND THREAD-SAFETY
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = createInstance();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Expected to create the object managed by current instance.
	 *
	 * @return the managed data object
	 */
	protected abstract T createInstance();
}

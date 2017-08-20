package org.gama.lang.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Holder<T> {
	
	void set(T value);
}

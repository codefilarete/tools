package org.gama.lang.function;

/**
 * A simple interface to define that you can hang something on it 
 * 
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Hanger<T> {
	
	void set(T value);
}

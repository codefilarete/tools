package org.gama.lang.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface FluentHolder<T, F extends FluentHolder<T, F>> {
	
	F set(T value);
}

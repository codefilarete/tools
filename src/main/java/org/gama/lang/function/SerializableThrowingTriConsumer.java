package org.gama.lang.function;

import java.io.Serializable;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableThrowingTriConsumer<T, U, V, E extends Throwable> extends Serializable {
	
	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t the first input argument
	 * @param u the second input argument
	 * @param v the third input argument
	 */
	void accept(T t, U u, V v) throws E;
}
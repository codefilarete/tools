package org.gama.lang.function;

import java.io.Serializable;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableThrowingConsumer<T, E extends Throwable> extends Serializable {
	
	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t the first input argument
	 */
	void accept(T t) throws E;
}
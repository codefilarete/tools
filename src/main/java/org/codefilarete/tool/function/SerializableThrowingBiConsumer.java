package org.codefilarete.tool.function;

import java.io.Serializable;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableThrowingBiConsumer<T, U, E extends Throwable> extends Serializable {
	
	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param t the first input argument
	 * @param u the second input argument
	 */
	void accept(T t, U u) throws E;
}
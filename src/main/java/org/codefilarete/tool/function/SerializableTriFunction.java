package org.codefilarete.tool.function;

import java.io.Serializable;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableTriFunction<T, U, V, R> extends Serializable {
	
	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @param u the second function argument
	 * @param v the third function argument
	 * @return the function result
	 */
	R apply(T t, U u, V v);
}

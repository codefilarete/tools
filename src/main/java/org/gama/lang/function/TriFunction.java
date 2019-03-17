package org.gama.lang.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
	
	/**
	 * Applies this function to the given arguments.
	 *
	 * @param t the first function argument
	 * @param u the second function argument
	 * @param v the thrid function argument
	 * @return the function result
	 */
	R apply(T t, U u, V v);
}

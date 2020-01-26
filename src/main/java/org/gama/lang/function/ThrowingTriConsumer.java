package org.gama.lang.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingTriConsumer<C, A, B, E extends Throwable> {
	
	/**
	 * Performs this operation on the given argument.
	 *
	 * @param c instance on which this consumer will be invoked
	 * @param a the first argument of the method to be called
	 * @param b the second argument of the method to be called
	 */
	void accept(C c, A a, B b) throws E;
	
}

package org.gama.lang.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingTriConsumer<C, A, B, E extends Throwable> {
	
	/**
	 * Performs this operation on the given argument.
	 *
	 * @param c the first argument
	 * @param t the second argument
	 */
	void accept(C c, A a, B b) throws E;
	
}

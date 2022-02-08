package org.codefilarete.tool.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingBiConsumer<C, T, E extends Throwable> {
	
	/**
	 * Performs this operation on the given argument.
	 *
	 * @param c the first argument
	 * @param t the second argument
	 */
	void accept(C c, T t) throws E;
	
}

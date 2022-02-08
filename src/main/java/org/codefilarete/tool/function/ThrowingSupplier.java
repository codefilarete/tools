package org.codefilarete.tool.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {
	
	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	T get() throws E;
}

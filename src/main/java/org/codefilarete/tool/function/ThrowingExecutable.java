package org.codefilarete.tool.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingExecutable<T, E extends Throwable> {
	
	/**
	 * Execute some action and returns its result.
	 * Technically equivalent to {@link ThrowingSupplier} but differs by semantic (this method may take more time, is not just a provider) 
	 *
	 * @return a result
	 */
	T execute() throws E;
}

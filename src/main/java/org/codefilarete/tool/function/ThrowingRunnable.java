package org.codefilarete.tool.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {
	
	/**
	 * Executes some throwing code
	 */
	void run() throws E;
}

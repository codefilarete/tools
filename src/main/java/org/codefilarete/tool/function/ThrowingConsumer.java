package org.codefilarete.tool.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
	
	/**
	 * Performs this operation on the given argument.
	 *
	 * @param t the input argument
	 */
	void accept(T t) throws E;
	
	default ThrowingConsumer<T, E> andThen(Consumer<? super T> after) {
		Objects.requireNonNull(after);
		return (T t) -> { accept(t); after.accept(t); };
	}
	
	default ThrowingConsumer<T, E> andThen(ThrowingConsumer<? super T, E> after) {
		Objects.requireNonNull(after);
		return (T t) -> { accept(t); after.accept(t); };
	}
}

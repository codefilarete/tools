package org.codefilarete.tool.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingTriFunction<T, U, V, R, E extends Throwable> {
	
	/**
	 * Applies this function to the given argument.
	 *
	 * @param t the function argument
	 * @param u the second function argument
	 * @param v the third function argument
	 * @return the function result
	 */
	R apply(T t, U u, V v) throws E;
	
	default <RR> ThrowingTriFunction<T, U, V, RR, E> andThen(Function<? super R, ? extends RR> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v) -> after.apply(apply(t, u, v));
	}
	
	default <RR> ThrowingTriFunction<T, U, V, RR, E> andThen(ThrowingFunction<? super R, ? extends RR, E> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v) -> after.apply(apply(t, u, v));
	}
}

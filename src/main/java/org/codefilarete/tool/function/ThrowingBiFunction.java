package org.codefilarete.tool.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Throwable> {
	
	/**
	 * Applies this function to the given argument.
	 *
	 * @param t the function argument
	 * @param u the second function argument
	 * @return the function result
	 */
	R apply(T t, U u) throws E;
	
	default <RR> ThrowingBiFunction<T, U, RR, E> andThen(Function<? super R, ? extends RR> after) {
		Objects.requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}
	
	default <RR> ThrowingBiFunction<T, U, RR, E> andThen(ThrowingFunction<? super R, ? extends RR, E> after) {
		Objects.requireNonNull(after);
		return (T t, U u) -> after.apply(apply(t, u));
	}
}

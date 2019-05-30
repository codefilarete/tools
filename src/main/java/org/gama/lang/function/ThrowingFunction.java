package org.gama.lang.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {
	
	/**
	 * Applies this function to the given argument.
	 *
	 * @param t the function argument
	 * @return the function result
	 */
	R apply(T t) throws E;
	
	default <RR> ThrowingFunction<T, RR, E> andThen(Function<? super R, ? extends RR> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}
	
	default <RR> ThrowingFunction<T, RR, E> andThen(ThrowingFunction<? super R, ? extends RR, E> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}
}

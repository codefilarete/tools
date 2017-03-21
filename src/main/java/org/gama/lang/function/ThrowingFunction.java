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
	
	default <V> ThrowingFunction<V, R, E> compose(Function<? super V, ? extends T> before) {
		Objects.requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}
	
	default <V> ThrowingFunction<V, R, E> compose(ThrowingFunction<? super V, ? extends T, E> before) {
		Objects.requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}
	
	default <V> ThrowingFunction<T, V, E> andThen(Function<? super R, ? extends V> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}
	
	default <V> ThrowingFunction<T, V, E> andThen(ThrowingFunction<? super R, ? extends V, E> after) {
		Objects.requireNonNull(after);
		return (T t) -> after.apply(apply(t));
	}
}

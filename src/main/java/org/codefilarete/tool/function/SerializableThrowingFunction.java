package org.codefilarete.tool.function;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableThrowingFunction<T, R, E extends Throwable> extends Serializable {
	
	/**
	 * Applies this function to the given argument.
	 *
	 * @param t the function argument
	 * @return the function result
	 */
	R apply(T t) throws E;
	
	default <V> SerializableThrowingFunction<V, R, E> compose(Function<? super V, ? extends T> before) {
		return (V v) -> apply(before.apply(v));
	}
	
	default <V> SerializableThrowingFunction<V, R, E> compose(SerializableThrowingFunction<? super V, ? extends T, E> before) {
		return (V v) -> apply(before.apply(v));
	}
	
	default <V> SerializableThrowingFunction<T, V, E> andThen(Function<? super R, ? extends V> after) {
		return (T t) -> after.apply(apply(t));
	}
	
	default <V> SerializableThrowingFunction<T, V, E> andThen(SerializableThrowingFunction<? super R, ? extends V, E> after) {
		return (T t) -> after.apply(apply(t));
	}
}

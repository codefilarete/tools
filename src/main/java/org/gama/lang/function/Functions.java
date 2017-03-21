package org.gama.lang.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
public class Functions {
	
	/**
	 * Chains several {@link Function} taking null-returned values into account by skipping chain hence preventing NullPointerException
	 * @param firstFunction the first function that will be applied in the chain
	 * @param secondFunction the second function that will be applied in the chain
	 * @return a {@link Function} that chains the 2 given ones by avoiding NullPointerException
	 */
	public static <K, A, V> Function<K, V> chain(NullAwareFunction<K, A> firstFunction, NullAwareFunction<A, V> secondFunction) {
		return firstFunction.andThen(secondFunction);
	}
	
	/**
	 * Same as the JDK {@link Function} class but with null-robust chaining through {@link #andThen(Function)}
	 * @param <T>
	 * @param <R>
	 */
	@FunctionalInterface
	public interface NullAwareFunction<T, R> extends Function<T, R> {
		
		@Override
		default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
			Objects.requireNonNull(after);
			return (T t) -> {
				R result = apply(t);
				return result == null ? null : after.apply(result);
			};
		}
	}
}

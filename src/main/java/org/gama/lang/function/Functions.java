package org.gama.lang.function;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Guillaume Mary
 */
public class Functions {
	
	/**
	 * Converts a {@link Predicate} to {@link Function} returning a boolean
	 *
	 * @param predicate the one to be converted
	 * @param <T> input type
	 * @return a new (lambda) {@link Function} plugged onto {@link Predicate}
	 */
	public static <T> Function<T, Boolean> toFunction(Predicate<T> predicate) {
		return predicate::test;
	}
	
	/**
	 * Chains several {@link Function}s taking null-returned values into account by skipping chain hence preventing NullPointerException.
	 * Calling {@link Function#andThen(Function)} on result is also null-proof, making all the chain null-proof.
	 * <br>
	 * Prefer {@link #chain(Function, Function)} if you want to keep the behavior of a default Java statement chaining : throw a {@link NullPointerException}.
	 * <br>
	 * The method name "link" can be discussed, but it finally looks shorter than a "chainBeingNullAware" (kind of) name method which is too long but clearer. 
	 * 
	 * @param firstFunction the first function that will be applied in the chain
	 * @param secondFunction the second function that will be applied in the chain
	 * @return a {@link Function} that chains the 2 given ones by avoiding NullPointerException
	 * @see #chain(Function, Function) 
	 */
	public static <K, A, V> Function<K, V> link(Function<K, A> firstFunction, Function<A, V> secondFunction) {
		return chain(new NullProofFunction<>(firstFunction), new NullProofFunction<>(secondFunction));
	}
	
	/**
	 * Chains several {@link Function}s.
	 * Prefer {@link #link(Function, Function)} if you want a "null proof" chain.
	 * 
	 * @param firstFunction the first function that will be applied in the chain
	 * @param secondFunction the second function that will be applied in the chain
	 * @return a {@link Function} that chains the 2 given ones by avoiding NullPointerException
	 * @see #link(Function, Function)
	 */
	public static <K, A, V> Function<K, V> chain(Function<K, A> firstFunction, Function<A, V> secondFunction) {
		return firstFunction.andThen(secondFunction);
	}
	
	/**
	 * A {@link Function} that skips calling a given one if input is null. Will return null in that case.
	 * {@link Function#andThen(Function)} method is also null-proof, making all the chain null-proof.
	 * 
	 * @param <I> function input type
	 * @param <O> function output type
	 */
	public static class NullProofFunction<I, O> implements Function<I, O> {
		
		private final Function<I, O> surrogate;
		
		public NullProofFunction(Function<I, O> surrogate) {
			this.surrogate = surrogate;
		}
		
		@Override
		public O apply(I input) {
			return input == null ? null : surrogate.apply(input);
		}
		
		/**
		 * Overriden to make it null proof
		 * @param after the next function to apply on the result of this one
		 * @param <V> type of the result of the combined functions
		 * @return a new {@link NullProofFunction} that chains {@code this} with {@code after} only if the result of {@code this} is not null
		 */
		@Override
		public <V> NullProofFunction<I, V> andThen(Function<? super O, ? extends V> after) {
			Objects.requireNonNull(after);
			return new NullProofFunction<>(i -> {
				O result = apply(i);
				return result == null ? null : after.apply(result);
			});
		}
	}
}

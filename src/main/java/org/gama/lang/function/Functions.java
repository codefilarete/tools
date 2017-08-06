package org.gama.lang.function;

import java.util.function.Function;

/**
 * @author Guillaume Mary
 */
public class Functions {
	
	/**
	 * Chains several {@link Function}s taking null-returned values into account by skipping chain hence preventing NullPointerException.
	 * Prefer {@link #chain(Function, Function)} if you want to keep the behavior of a default Java statement chaining : throw a {@link NullPointerException}.
	 * 
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
	 * A {@link Function} that skip calling a given one if input is null. Will return null in that case.
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
	}
}

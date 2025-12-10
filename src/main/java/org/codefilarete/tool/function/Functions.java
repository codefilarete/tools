package org.codefilarete.tool.function;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.codefilarete.tool.Reflections;

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
	 * Converts a {@link Method} to a {@link Function}
	 *
	 * @param getter any method taking no argument
	 * @param <I> target instance type
	 * @param <O> argument type
	 * @return a new (lambda) {@link Function} plugged onto {@link Predicate}
	 */
	public static <I, O> Function<I, O> toFunction(Method getter) {
		if (getter.getParameterCount() > 0) {
			throw new IllegalArgumentException("Method is expected to have no argument but has one : " + Reflections.toString(getter));
		}
		return target -> (O) Reflections.invoke(getter, target);
	}
	
	/**
	 * Converts a {@link Method} to a {@link BiConsumer}
	 *
	 * @param setter any method taking one argument
	 * @param <I> target instance type
	 * @param <O> argument type
	 * @return a new (lambda) {@link Function} plugged onto {@link Predicate}
	 */
	public static <I, O> BiConsumer<I, O> toBiConsumer(Method setter) {
		if (setter.getParameterCount() == 0) {
			throw new IllegalArgumentException("Method is expected to have at least 1 argument but has none : " + Reflections.toString(setter));
		}
		return (target, args) -> Reflections.invoke(setter, target, args);
	}
	
	/**
	 * Chains a {@link Function} and a {@link Predicate} to build a {@link Predicate} on the input argument type of the {@link Function}
	 *
	 * @param function the first function that will be applied
	 * @param predicate the predicate to be applied on result of {@code function}
	 * @return a {@link Predicate} that chains the given arguments
	 */
	public static <K, A> Predicate<K> asPredicate(Function<K, A> function, Predicate<A> predicate) {
		return k -> predicate.test(function.apply(k));
	}
	
	/**
	 * Chains several {@link Function}s taking null-returned values into account by skipping chain hence preventing NullPointerException.
	 * Calling {@link Function#andThen(Function)} on result is also null-proof, making all the chain null-proof.
	 * <br>
	 * Prefer {@link #chain(Function, Function)} if you want to keep the behavior of a default Java statement chaining : throws a {@link NullPointerException}.
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
	 * Chains several {@link Function}s taking null-returned values into account by skipping chain hence preventing NullPointerException.
	 * Calling {@link Function#andThen(Function)} on result is also null-proof, making all the chain null-proof.
	 * <br>
	 * Prefer {@link #chain(Function, Function, Function)} if you want to keep the behavior of a default Java statement chaining : throws a {@link NullPointerException}.
	 * <br>
	 * The method name "link" can be discussed, but it finally looks shorter than a "chainBeingNullAware" (kind of) name method which is too long but clearer. 
	 *
	 * @param firstFunction the first function that will be applied in the chain
	 * @param secondFunction the second function that will be applied in the chain
	 * @param thirdFunction the third function that will be applied in the chain
	 * @return a {@link Function} that chains the 3 given ones by avoiding NullPointerException
	 * @see #chain(Function, Function)
	 */
	public static <K, A, B, V> Function<K, V> link(Function<K, A> firstFunction, Function<A, B> secondFunction, Function<B, V> thirdFunction) {
		return chain(new NullProofFunction<>(firstFunction), new NullProofFunction<>(secondFunction), new NullProofFunction<>(thirdFunction));
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
	 * Chains several {@link Function}s.
	 * Prefer {@link #link(Function, Function, Function)} if you want a "null proof" chain.
	 *
	 * @param firstFunction the first function that will be applied in the chain
	 * @param secondFunction the second function that will be applied in the chain
	 * @return a {@link Function} that chains the 2 given ones by avoiding NullPointerException
	 * @see #link(Function, Function)
	 */
	public static <K, A, B, V> Function<K, V> chain(Function<K, A> firstFunction, Function<A, B> secondFunction, Function<B, V> thirdFunction) {
		return chain(firstFunction, secondFunction).andThen(thirdFunction);
	}
	
	/**
	 * A {@link Function} that skips calling a given one if input is null. Will return null in that case.
	 * {@link Function#andThen(Function)} method is also null-proof, making all the chain null-proof.
	 * 
	 * @param <I> function input type
	 * @param <O> function output type
	 */
	public static class NullProofFunction<I, O> implements Function<I, O> {
		
		private final Function<I, O> delegate;
		
		public NullProofFunction(Function<I, O> delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public O apply(I input) {
			return input == null ? null : delegate.apply(input);
		}
		
		/**
		 * Overridden to make it null proof
		 * @param after the next function to apply on the result of this one
		 * @param <V> type of the result of the combined functions
		 * @return a new {@link NullProofFunction} that chains {@code this} with {@code after} only if the result of {@code this} is not null
		 */
		@Override
		public <V> NullProofFunction<I, V> andThen(Function<? super O, ? extends V> after) {
			return new NullProofFunction<>(i -> {
				O result = apply(i);
				return result == null ? null : after.apply(result);
			});
		}
	}
}

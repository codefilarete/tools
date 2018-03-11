package org.gama.lang;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.gama.lang.function.Functions;
import org.gama.lang.function.ThrowingConsumer;
import org.gama.lang.function.ThrowingFunction;

/**
 * Class that tries to fullfill use cases on which {@link java.util.Optional} is insufficient
 *
 * @author Guillaume Mary
 */
@ParametersAreNonnullByDefault
public class Nullable<T> implements Supplier<T> {
	
	/**
	 * Shortcut for new Nullable(value)
	 * 
	 * @param value a nullable value
	 * @param <T> value type
	 * @return new Nullable(value)
	 */
	public static <T> Nullable<T> nullable(@javax.annotation.Nullable T value) {
		return new Nullable<>(value);
	}
	
	/**
	 * Shortcut for new Nullable(value)
	 *
	 * @param value a nullable value
	 * @param <T> value type
	 * @return new Nullable(value)
	 */
	public static <T> Nullable<T> nullable(Supplier<T> value) {
		return new Nullable<>(value);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #apply(Function)}
	 *
	 * @param input a nullable value
	 * @param f a function to be applied on the input
	 * @param <I> value type
	 * @return new Nullable(input).orApply(f)
	 */
	public static <I, O> Nullable<O> nullable(@javax.annotation.Nullable I input, Function<I, O> f) {
		return nullable(input).apply(f);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #apply(Function)}
	 *
	 * @param input a nullable value
	 * @param f a function to be applied on the input
	 * @param <I> value type
	 * @return new Nullable(input).orApply(f)
	 */
	public static <I, O> Nullable<O> nullable(Supplier<I> input, Function<I, O> f) {
		return nullable(input).apply(f);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #apply(Function)}
	 *
	 * @param input a nullable value
	 * @param f1 a function to be applied on the input
	 * @param f2 a second function to be applied on the result of the first function
	 * @param <I> value type
	 * @return new Nullable(input).apply(f1).apply(f2)
	 */
	public static <I, O, A> Nullable<O> nullable(@javax.annotation.Nullable I input, Function<I, A> f1, Function<A, O> f2) {
		return nullable(input).apply(f1).apply(f2);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #apply(Function)}
	 *
	 * @param input a nullable value
	 * @param f1 a function to be applied on the input
	 * @param f2 a second function to be applied on the result of the first function
	 * @param <I> value type
	 * @return new Nullable(input).apply(f1).apply(f2)
	 */
	public static <I, O, A> Nullable<O> nullable(Supplier<I> input, Function<I, A> f1, Function<A, O> f2) {
		return nullable(input).apply(f1).apply(f2);
	}
	
	/**
	 * @param <T> type of the {@link Nullable}
	 * @return a {@link Nullable} of null
	 */
	public static <T> Nullable<T> empty() {
		return Nullable.nullable(null);
	}
	
	/** The payload, may be null itself or owned value may be null */
	private Supplier<T> value;
	
	/**
	 * Constructor from a nullable value
	 * @param value a nullable object
	 */
	private Nullable(@javax.annotation.Nullable T value) {
		this.value = () -> value;
	}
	
	/**
	 * Constructor from a non null supplier which may supply a null value
	 * @param value a non null supplier
	 */
	private Nullable(Supplier<T> value) {
		this.value = value;
	}
	
	/**
	 * @return true if internal value is not null
	 */
	public boolean isPresent() {
		return value.get() != null;
	}
	
	/**
	 * Return current value: null or not
	 * 
	 * @return internal value
	 */
	@Override
	public T get() {
		return value == null ? null : value.get();
	}
	
	/**
	 * Gives another value is current is null
	 * 
	 * @param anotherValue the other value to return in case of current one is null
	 * @return {@link #get} or anotherValue if non present value
	 */
	public T orGet(@javax.annotation.Nullable T anotherValue) {
		return orGet(() -> anotherValue);
	}
	
	/**
	 * Gives another value is current is null
	 *
	 * @param anotherValue the supplier called in case of current null value
	 * @return {@link #get} or anotherValue.get() if non present value
	 */
	public T orGet(Supplier<T> anotherValue) {
		return !isPresent() ? anotherValue.get() : get();
	}
	
	/**
	 * Gives the value returned by the function applied on the current value if exists
	 * 
	 * @param function the function to apply
	 * @param <C> result type
	 * @return {@link #get} or {@link #apply(Function)}.get() if present value
	 */
	public <C> C orGet(Function<? super T, C> function) {
		return apply(function).get();
	}
	
	/**
	 * Changes internal value by another
	 *
	 * @param otherValue the replacing value
	 * @return this
	 */
	public Nullable<T> set(@javax.annotation.Nullable T otherValue) {
		return set(() -> otherValue);
	}
	
	/**
	 * Changes internal value by another
	 *
	 * @param otherValue a replacing {@link java.util.function.Supplier}
	 * @return this
	 */
	public Nullable<T> set(Supplier<T> otherValue) {
		value = otherValue;
		return this;
	}
	
	/**
	 * Changes internal value by another if current is null
	 *
	 * @param otherValue the replacing value
	 * @return this
	 */
	public Nullable<T> orSet(@javax.annotation.Nullable T otherValue) {
		return orSet(() -> otherValue);
	}
	
	/**
	 * Changes internal value by another if current is null
	 * 
	 * @param otherValue a replacing {@link java.util.function.Supplier}
	 * @return this
	 */
	public Nullable<T> orSet(Supplier<T> otherValue) {
		if (!isPresent()) {
			value = otherValue;
		}
		return this;
	}
	
	/**
	 * Applies a function on current value if present
	 * 
	 * @param function a function to be applied on value
	 * @param <O> the function returned type
	 * @return a {@link Nullable} of the function result or of null if value wasn't present
	 */
	public <O> Nullable<O> apply(Function<? super T, ? extends O> function) {
		return nullable(ifPresent(function::apply));
	}
	
	/**
	 * Same as {@link #apply(Function)}, to mimic {@link java.util.Optional#map(Function)}
	 *
	 * @param mapper a function to be applied on value
	 * @param <O> the function returned type
	 * @return a {@link Nullable} of the function result or of null if value wasn't present
	 */
	public<O> Nullable<O> map(Function<? super T, ? extends O> mapper) {
		return apply(mapper);
	}
	
	/**
	 * Consumes the value if present
	 *
	 * @param consumer a value consumer
	 * @return this
	 */
	public Nullable<T> accept(Consumer<T> consumer) {
		return ifPresent(consumer::accept);
	}
	
	/**
	 * Tests the value if present
	 *
	 * @param predicate a predicate on value type
	 * @return a {@link Nullable} on predicate's result
	 */
	public Nullable<Boolean> test(Predicate<? super T> predicate) {
		return nullable(ifPresent(Functions.toFunction(predicate)::apply));
	}
	
	/**
	 * Tests the value if present
	 * 
	 * @param predicate a predicate on value type
	 * @return this if present and predicate matches, else a {@link Nullable} of null
	 */
	public Nullable<T> filter(Predicate<? super T> predicate) {
		if (isPresent() && predicate.test(get())) {
			return this;
		} else {
			return empty();
		}
	}
	
	/**
	 * Same as {@link #apply(Function)} with a throw clause
	 *
	 * @param function a function to be applied on value
	 * @param <O> the function returned type
	 * @param <E> le type d'exception
	 * @return a {@link Nullable} of the function result or of null if value wasn't present
	 * @throws E type of exception thrown by the function
	 */
	public <O, E extends Exception> Nullable<O> applyThrowing(ThrowingFunction<? super T, ? extends O, E> function) throws E {
		return nullable(ifPresent(function));
	}
	
	/**
	 * Same as {@link #accept(Consumer)} with a throw clause
	 *
	 * @param consumer a function to be applied on value
	 * @param <E> le type d'exception
	 * @return this
	 * @throws E type of exception thrown by the function
	 */
	public <E extends Exception> Nullable<T> acceptThrowing(ThrowingConsumer<? super T, E> consumer) throws E {
		return ifPresent(consumer::accept);
	}
	
	/**
	 * Will thrown the given {@link Exception} in case of missing value
	 * 
	 * @param throwable the {@link Exception} to be thrown
	 * @param <E> the {@link Exception} type
	 * @return this (if value is present)
	 * @throws E the given throwable
	 */
	public <E extends Throwable> Nullable<T> orThrow(E throwable) throws E {
		// NB we don't use ifPresent because it's quite too much for this case (and requires to return null in given function, ugly)
		if (!isPresent()) {
			throw throwable;
		} else {
			return this;
		}
	}
	
	/**
	 * Gives the current value or throws the given {@link Exception} in case of missing value.
	 * Shortcut for {@link #orThrow(Throwable)}.get()
	 *
	 * @param throwable the {@link Exception} to be thrown
	 * @param <E> the {@link Exception} type
	 * @return this (if value is present)
	 * @throws E the given throwable
	 */
	public <E extends Throwable> T getOrThrow(E throwable) throws E {
		return orThrow(throwable).get();
	}
	
	/** Applies the function if value is present */
	private <O, E extends Exception> O ifPresent(ThrowingFunction<? super T, ? extends O, E> mapper) throws E {
		return isPresent() ? mapper.apply(get()) : null;
	}
	
	/** Applies the consumer if value is present */
	private <E extends Exception> Nullable<T> ifPresent(ThrowingConsumer<? super T, E> consumer) throws E {
		if (isPresent()) {
			consumer.accept(get());
		}
		return this;
	}
}
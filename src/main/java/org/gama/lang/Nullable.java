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
	 * Shortcut for new Nullable(input) + {@link #map(Function)}
	 *
	 * @param input a nullable value
	 * @param f a function to be applied on the input
	 * @param <I> value type
	 * @return new Nullable(input).map(f)
	 */
	public static <I, O> Nullable<O> nullable(@javax.annotation.Nullable I input, Function<I, O> f) {
		return nullable(input).map(f);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #map(Function)}
	 *
	 * @param input a nullable value
	 * @param f a function to be applied on the input
	 * @param <I> value type
	 * @return new Nullable(input).map(f)
	 */
	public static <I, O> Nullable<O> nullable(Supplier<I> input, Function<I, O> f) {
		return nullable(input).map(f);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #map(Function)}
	 *
	 * @param input a nullable value
	 * @param f1 a function to be applied on the input
	 * @param f2 a second function to be applied on the result of the first function
	 * @param <I> value type
	 * @return new Nullable(input).map(f1).map(f2)
	 */
	public static <I, O, A> Nullable<O> nullable(@javax.annotation.Nullable I input, Function<I, A> f1, Function<A, O> f2) {
		return nullable(input).map(f1).map(f2);
	}
	
	/**
	 * Shortcut for new Nullable(input) + {@link #map(Function)}
	 *
	 * @param input a nullable value
	 * @param f1 a function to be applied on the input
	 * @param f2 a second function to be applied on the result of the first function
	 * @param <I> value type
	 * @return new Nullable(input).map(f1).map(f2)
	 */
	public static <I, O, A> Nullable<O> nullable(Supplier<I> input, Function<I, A> f1, Function<A, O> f2) {
		return nullable(input).map(f1).map(f2);
	}
	
	/**
	 * @param <T> type of the {@link Nullable}
	 * @return a {@link Nullable} of null
	 */
	public static <T> Nullable<T> empty() {
		return Nullable.nullable(() -> null);
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
	@javax.annotation.Nullable
	public T get() {
		return value == null ? null : value.get();
	}
	
	/**
	 * Replaces value by given one
	 * 
	 * @param value a new value, may be null
	 * @return this
	 */
	public Nullable<T> set(@javax.annotation.Nullable T value) {
		this.value = () -> value;
		return this;
	}
	
	/**
	 * Gives another value if current is null
	 * 
	 * @param anotherValue the other value to return in case of current one is null
	 * @return {@link #get} or anotherValue if non present value
	 */
	@javax.annotation.Nullable
	public T getOr(@javax.annotation.Nullable T anotherValue) {
		return getOr(() -> anotherValue);
	}
	
	/**
	 * Gives another value if current is null
	 *
	 * @param anotherValue the supplier called in case of current null value
	 * @return {@link #get} or anotherValue.get() if non present value
	 */
	public T getOr(Supplier<T> anotherValue) {
		return !isPresent() ? anotherValue.get() : get();
	}
	
	/**
	 * Gives the current value or throws the given {@link Throwable} in case of missing value.
	 * Shortcut for {@link #elseThrow(Throwable)}.get()
	 *
	 * @param throwable the provider of {@link Throwable} to be thrown
	 * @param <E> the {@link Throwable} type
	 * @return this (if value is present)
	 * @throws E the given throwable
	 */
	public <E extends Throwable> T getOrThrow(Supplier<E> throwable) throws E {
		return elseThrow(throwable).get();
	}
	
	/**
	 * Changes internal value by another if current is null
	 *
	 * @param otherValue the replacing value
	 * @return this
	 */
	public Nullable<T> elseSet(@javax.annotation.Nullable T otherValue) {
		return elseSet(() -> otherValue);
	}
	
	/**
	 * Changes internal value by another if current is null
	 * 
	 * @param otherValue a replacing {@link java.util.function.Supplier}
	 * @return this
	 */
	public Nullable<T> elseSet(Supplier<T> otherValue) {
		if (!isPresent()) {
			value = otherValue;
		}
		return this;
	}
	
	/**
	 * Will throw the given {@link Throwable} if current value is null
	 *
	 * @param throwable the {@link Throwable} to be thrown
	 * @param <E> the {@link Throwable} type
	 * @return this (if value is present)
	 * @throws E the given throwable
	 */
	public <E extends Throwable> Nullable<T> elseThrow(E throwable) throws E {
		return this.<E>elseThrow(() -> throwable);
	}
	
	/**
	 * Will throw supplied {@link Throwable} if current value is null.
	 * Tips to avoid invokation conflict with {@link #elseThrow(Throwable)} : one should specify generics before method call sush as:
	 * <code>
	 *     Nullable.empty().&lt;IOException&gt;elseThrow(IOException::new);
	 * </code>
	 *
	 * @param throwableSupplier the {@link Supplier} that will give the {@link Throwable} to be thrown
	 * @param <E> the {@link Throwable} type
	 * @return this (if value is present)
	 * @throws E the given throwable
	 */
	public <E extends Throwable> Nullable<T> elseThrow(Supplier<E> throwableSupplier) throws E {
		// NB we don't use ifPresent because it's quite too much for this case (and requires to return null in given function, ugly)
		if (!isPresent()) {
			throw throwableSupplier.get();
		} else {
			return this;
		}
	}
	
	/**
	 * Applies a function on current value if present
	 *
	 * @param mapper a function to be applied on value
	 * @param <O> the function returned type
	 * @return a {@link Nullable} of the function result or of null if value wasn't present
	 */
	public<O> Nullable<O> map(Function<? super T, ? extends O> mapper) {
		return nullable(ifPresent(mapper::apply));
	}
	
	/**
	 * Same as {@link #map(Function)} with a throw clause
	 *
	 * @param function a function to be applied on value
	 * @param <O> the function returned type
	 * @param <E> le type d'exception
	 * @return a {@link Nullable} of the function result or of null if value wasn't present
	 * @throws E type of exception thrown by the function
	 */
	public <O, E extends Exception> Nullable<O> mapThrower(ThrowingFunction<? super T, ? extends O, E> function) throws E {
		return nullable(ifPresent(function));
	}
	
	/**
	 * Consumes the value if present
	 *
	 * @param consumer a value consumer
	 * @return this
	 */
	public Nullable<T> invoke(Consumer<T> consumer) {
		return ifPresent(consumer::accept);
	}
	
	/**
	 * Same as {@link #invoke(Consumer)} with a throw clause
	 *
	 * @param consumer a function to be applied on value
	 * @param <E> le type d'exception
	 * @return this
	 * @throws E type of exception thrown by the function
	 */
	public <E extends Exception> Nullable<T> invokeThrower(ThrowingConsumer<? super T, E> consumer) throws E {
		return ifPresent(consumer);
	}
	
	/**
	 * Tests the value if present, if not returns an empty {@link Nullable}
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
	 * Tests the value if present and returns the result
	 * 
	 * @param predicate a predicate on value type, invoked only if value is present
	 * @return {@link Predicate} result wrapped into a {@link Nullable}
	 */
	public Nullable<Boolean> test(Predicate<? super T> predicate) {
		return Nullable.nullable(ifPresent(Functions.toFunction(predicate)::apply));
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
package org.codefilarete.tool.function;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Guillaume Mary
 */
public class Predicates {
	
	/**
	 * Static method to negate the given predicate so one can write {@code not(String::contains)}.
	 *
	 * @param predicate any {@link Predicate}, a method reference is preferred else this method as no purpose and can be replaced by {@link Predicate#negate}
	 * @param <E> input type of tested elements
	 * @return a negated {@link Predicate} of the given one
	 */
	public static <E> Predicate<E> not(Predicate<E> predicate) {
		return predicate.negate();
	}
	
	/**
	 * Converts a {@link Function} returning a boolean to {@link Predicates}
	 *
	 * @param booleanFunction the one to be converted
	 * @param <E> input type
	 * @return a new (lambda) {@link Predicate} plugged onto the given {@link Function}
	 */
	public static <E> Predicate<E> predicate(Function<E, Boolean> booleanFunction) {
		return booleanFunction::apply;
	}
	
	/**
	 * Creates a {@link Predicate} from a mapping function and a predicate applied to the result of the function
	 *
	 * @param mapper the {@link Function} that gives the value to be tested
	 * @param predicate the {@link Predicate} to apply onto the result of the mapping function 
	 * @param <I> input type
	 * @param <O> output type
	 * @return a new (lambda) {@link Predicate} plugged onto the given {@link Function} and {@link Predicate}
	 */
	public static <I, O> Predicate<I> predicate(Function<I, O> mapper, Predicate<O> predicate) {
		return predicate(mapper.andThen(Functions.toFunction(predicate)));
	}
	
	/**
	 * Creates a new {@link Comparator} returning 0 if given predicate returns true, -1 if false
	 * @param predicate any {@link Predicate} checking for element equality
	 * @param <E> element type to be compared
	 * @return a new {@link Comparator} returning 0 if given predicate returns true, -1 if false
	 */
	public static <E> Comparator<E> toComparator(BiPredicate<E, E> predicate) {
		return (o1, o2) -> predicate.test(o1, o2) ? 0 : -1; 
	} 
	
	/**
	 * Creates a {@link BiPredicate} which makes a logical AND between the results of the given {@link Function}s.
	 * Allows one to test objects equality on some properties by referencing them with getter references (which are {@link Function}s)
	 *
	 * @param testableProperties some {@link Function}s
	 * @param <I> input predicate type
	 * @return a {@link Predicate} as a logical AND between all given {@link Predicate}s
	 */
	public static <I> BiPredicate<I, I> and(Function<I, ?> ... testableProperties) {
		BiPredicate<I, I> result = (a, b) -> true;
		for (Function<I, ?> printableProperty : testableProperties) {
			result = result.and((a, b) -> equalOrNull(printableProperty.apply(a), printableProperty.apply(b)));
		}
		return result;
	}
	
	/**
	 * @return a {@link Predicate} that always matches
	 */
	public static <C> Predicate<C> acceptAll() {
		return new AlwaysTrue<>();
	}
	
	/**
	 * @return a {@link Predicate} that never matches
	 */
	public static <C> Predicate<C> rejectAll() {
		return new AlwaysFalse<>();
	}
	
	/**
	 * Checks that o1.equals(o2) by taking null check into account
	 *
	 * @param o1 an instance, null possible
	 * @param o2 an instance, null possible
	 * @return - true if o1 == null && o2 == null
	 *		<br> - false if o1 != null or-exclusive o2 != null
	 *		<br> - else (i.e o1 and o2 not null) o1.equals(o2)
	 */
	public static <T, U> boolean equalOrNull(@Nullable T o1, @Nullable U o2) {
		return equalOrNull(o1, o2, Object::equals);
	}
	
	/**
	 * Checks that 2 instances are equal according to a {@link Predicate} and by taking null check into account
	 *
	 * @param t an instance, null possible
	 * @param u an instance, null possible
	 * @param equalsNonNullDelegate the predicate delegate to test non null values
	 * @return - true if o1 == null && o2 == null
	 *		<br> - false if o1 != null or-exclusive o2 != null
	 *		<br> - else (i.e o1 and o2 not null) o1.equals(o2)
	 */
	public static <T, U> boolean equalOrNull(@Nullable T t, @Nullable U u, BiPredicate<T, U> equalsNonNullDelegate) {
		return (t == null && u == null) || (t != null && u != null && equalsNonNullDelegate.test(t, u));
	}
	
	/**
	 * A {@link Predicate} that always returns true
	 * @param <C>
	 */
	private static class AlwaysTrue<C> implements Predicate<C> {
		@Override
		public boolean test(C o) {
			return true;
		}
	}
	
	/**
	 * A {@link Predicate} that always returns false
	 * @param <C>
	 */
	private static class AlwaysFalse<C> implements Predicate<C> {
		@Override
		public boolean test(C o) {
			return false;
		}
	}
}

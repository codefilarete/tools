package org.gama.lang.bean;

import java.util.function.Predicate;

import org.gama.lang.function.Predicates;

/**
 * @author Guillaume Mary
 */
public class Objects {
	
	public static String preventNull(String value) {
		return preventNull(value, "");
	}
	
	public static <E> E preventNull(E value, E nullValue) {
		return value == null ? nullValue : value;
	}
	
	/**
	 * Returns a fallback value if the given input equals a matching one
	 *
	 * @param input value that may equals the matching one
	 * @param matchingCase value on which fallback value will be return
	 * @param fallbackValue value returned when input equals matching value
	 * @param <C> type of mangaed value
	 * @return fallbackValue if input equals matching case
	 */
	public static <C> C fallback(C input, C matchingCase, C fallbackValue) {
		if (java.util.Objects.equals(input, matchingCase)) {
			return fallbackValue;
		} else {
			return input;
		}
	}
	
	/**
	 * Static method to negate the given predicate so one can write {@code not(String::contains)}.
	 * 
	 * @param predicate any {@link Predicate}, a method reference is prefered else this method as no purpose and can be replaced by {@link Predicate#negate}
	 * @param <E> input type of tested elements
	 * @return a negated {@link Predicate} of the given one
	 */
	public static <E> Predicate<E> not(Predicate<E> predicate) {
		return Predicates.not(predicate);
	}
}

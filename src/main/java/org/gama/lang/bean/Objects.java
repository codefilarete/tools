package org.gama.lang.bean;

import java.util.Arrays;
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
	
	/**
	 * Returns true if the arguments are equal to each other and false otherwise. As the opposit of {@link java.util.Objects#equals(Object, Object)}
	 * this implementation works on array since it's bounded to {@link java.util.Objects#deepEquals(Object, Object)}.
	 *
	 * @param a an object
	 * @param b an object to be compared with {@code a} for equality
	 * @return {@code true} if the arguments are equal to each other and {@code false} otherwise
	 * @see java.util.Objects#deepEquals(Object, Object)
	 */
	public static boolean equals(Object a, Object b) {
		return java.util.Objects.deepEquals(a, b);
	}
	
	/**
	 * Returns the hash code of an object. As the opposit of {@link java.util.Objects#hashCode(Object)} or {@link java.util.Objects#hash(Object...)}
	 * this implementation works on array since it's bounded to {@link java.util.Arrays#deepHashCode(Object[])}.
	 *
	 * @param o an object
	 * @return the hash code of a non-{@code null} argument and 0 for a {@code null} argument
	 * @see Object#hashCode
	 * @see java.util.Arrays#deepHashCode(Object[])
	 */
	public static int hashCode(Object o) {
		if (o == null) return 0;
		if (o.getClass().isArray()) {
			return Arrays.deepHashCode((Object[]) o);
		} else {
			return o.hashCode();
		}
	}
	
	/**
	 * Generates the hash code for a sequence of input values. As the opposit of {@link java.util.Objects#hashCode(Object)} or {@link java.util.Objects#hash(Object...)}
	 * this implementation works on array of arrays since it's bounded to {@link java.util.Arrays#deepHashCode(Object[])}.
	 *
	 * @param values the values to be hashed
	 * @return a hash value of the sequence of input values
	 * @see Object#hashCode
	 * @see java.util.Arrays#deepHashCode(Object[]) 
	 */
	public static int hashCode(Object... values) {
		if (values == null) return 0;
		int result = 0;
		for (Object element : values)
			result = 31 * result + hashCode(element);
		return result;
	}
}

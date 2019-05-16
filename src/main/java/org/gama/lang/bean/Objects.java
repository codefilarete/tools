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

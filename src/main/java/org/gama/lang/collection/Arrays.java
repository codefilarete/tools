package org.gama.lang.collection;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Guillaume Mary
 */
public class Arrays {
	
	private static final Integer[] EMPTY_INTEGER_ARRAY = {};
	
	public static <T> List<T> asList(T... a) {
		return new ArrayList<>(java.util.Arrays.asList(a));
	}
	
	@SuppressWarnings("squid:S1319")	// LinkedHashSet return type is voluntary because it is the goal of this method
	public static <T> LinkedHashSet<T> asSet(T ... a) {
		LinkedHashSet<T> toReturn = new LinkedHashSet<>();
		java.util.Collections.addAll(toReturn, a);
		return toReturn;
	}
	
	@SuppressWarnings("squid:S1319")	// HashSet return type is voluntary because it is the goal of this method
	public static <T> HashSet<T> asHashSet(T ... a) {
		HashSet<T> toReturn = new HashSet<>();
		java.util.Collections.addAll(toReturn, a);
		return toReturn;
	}
	
	@SuppressWarnings("squid:S1319")	// TreeSet return type is voluntary because it is the goal of this method
	public static <T> TreeSet<T> asTreeSet(Comparator<? super T> comparator, T ... a) {
		TreeSet<T> toReturn = new TreeSet<>(comparator);
		java.util.Collections.addAll(toReturn, a);
		return toReturn;
	}
	
	@SuppressWarnings("squid:S1319")	// TreeSet return type is voluntary because it is the goal of this method
	public static <T> TreeSet<T> asTreeSet(Comparator<? super T> comparator, Collection<T> a) {
		TreeSet<T> toReturn = new TreeSet<>(comparator);
		toReturn.addAll(a);
		return toReturn;
	}
	
	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}
	
	/**
	 * Tranforms an array of primitive integers to an array of Object integers to overcome the impossibility to cast a {@code int[]} to a {@code Integer[]}
	 * in the Java language.
	 * 
	 * @param integers any {@code int[]), null included
	 * @return a new {@code Integer[]} that contains all values of passed argument
	 */
	@Nullable
	public static Integer[] fromPrimitive(@Nullable int[] integers) {
		if (integers == null) {
			return null;
		} else if (integers.length == 0) {
			return EMPTY_INTEGER_ARRAY;
		} else {
			Integer[] result = new Integer[integers.length];
			for (int i = 0; i < integers.length; i++) {
				result[i] = integers[i];
			}
			return result;
		}
	}
	
	/**
	 * Method that can be used as a method reference for array index access.
	 * 
	 * @param index index of the array to return
	 * @param <C> type of the array to use
	 * @return a function that can be used as a reference for array index acccess
	 */
	public static <C> Function<C[], C> get(int index) {
		return cs -> cs[index];
	}
	
	/**
	 * Method that can be used as a method reference for array index access. Will call the {@link Supplier} in case of index that is out of the array
	 * boundaries.
	 *
	 * @param index index of the array to return
	 * @param defaultValue the value to return when boundaires are reached (negative index or higher than array length)
	 * @param <C> type of the array to use
	 * @return a function that can be used as a reference for array index acccess
	 */
	public static <C> Function<C[], C> get(int index, Supplier<C> defaultValue) {
		return cs -> isOutOfBounds(index, cs) ? defaultValue.get() : cs[index];
	}
	
	private static <C> boolean isOutOfBounds(int index, C[] cs) {
		return index < 0 || index > cs.length-1;
	}
	
	public static <C> C first(C[] args) {
		return args[0];
	}
	
	public static <C> C last(C[] args) {
		return args[args.length-1];
	}
	
	/**
	 * Concats 2 arrays of int
	 * @param src1 an array
	 * @param src2 an array
	 * @return a new array of length src1 + src2 containing the aggregation of both
	 */
	public static int[] cat(int[] src1, int[] src2) {
		int[] result = new int[src1.length + src2.length];
		System.arraycopy(src1, 0, result, 0, src1.length);
		System.arraycopy(src2, 0, result, src1.length, src2.length);
		return result;
	}
	
	/**
	 * Concats 2 arrays of long
	 * @param src1 an array
	 * @param src2 an array
	 * @return a new array of length src1 + src2 containing the aggregation of both
	 */
	public static long[] cat(long[] src1, long[] src2) {
		long[] result = new long[src1.length + src2.length];
		System.arraycopy(src1, 0, result, 0, src1.length);
		System.arraycopy(src2, 0, result, src1.length, src2.length);
		return result;
	}
	
	/**
	 * Concats 2 arrays of objects
	 * 
	 * @param src1 an array
	 * @param src2 an array
	 * @return a new array of length src1 + src2 containing the aggregation of both
	 */
	public static <E> E[] cat(E[] src1, E[] src2) {
		return cat(src1, src2, newInstance(src1, src1.length + src2.length));
	}
	
	private static <E> E[] cat(E[] src1, E[] src2, E[] dest) {
		System.arraycopy(src1, 0, dest, 0, src1.length);
		System.arraycopy(src2, 0, dest, src1.length, src2.length);
		return dest;
	}
	
	/**
	 * Gives the headSize firsts elements of the given array
	 * 
	 * @param src a source array
	 * @param headSize number of elements to be kept in final result
	 * @param <E> array elements type
	 * @return the headSize firsts elements of the given array
	 */
	public static <E> E[] head(E[] src, int headSize) {
		E[] result = newInstance(src, headSize);
		System.arraycopy(src, 0, result, 0, headSize);
		return result;
	}
	
	/**
	 * Gives the headSize firsts elements of the given array
	 * 
	 * @param src a source array
	 * @param headSize number of elements to be kept in final result
	 * @return the headSize firsts elements of the given array
	 */
	public static long[] head(long[] src, int headSize) {
		return (long[]) arrayCopy(src, headSize, new long[headSize]);
	}
	
	/**
	 * Gives the headSize firsts elements of the given array
	 * 
	 * @param src a source array
	 * @param headSize number of elements to be kept in final result
	 * @return the headSize firsts elements of the given array
	 */
	public static int[] head(int[] src, int headSize) {
		return (int[]) arrayCopy(src, headSize, new int[headSize]);
	}
	
	/**
	 * Gives the headSize firsts elements of the given array
	 * 
	 * @param src a source array
	 * @param headSize number of elements to be kept in final result
	 * @return the headSize firsts elements of the given array
	 */
	public static byte[] head(byte[] src, int headSize) {
		return (byte[]) arrayCopy(src, headSize, new byte[headSize]);
	}
	
	private static Object arrayCopy(Object src, int headSize, Object result) {
		System.arraycopy(src, 0, result, 0, headSize);
		return result;
	}
	
	/**
	 * Gives the tailSize lasts elements of the given array
	 *
	 * @param src a source array
	 * @param tailSize number of elements to be kept in final result
	 * @param <E> array elements type
	 * @return the headSize lasts elements of the given array
	 */
	public static <E> E[] tail(E[] src, int tailSize) {
		E[] result = newInstance(src, tailSize);
		System.arraycopy(src, src.length - tailSize, result, 0, tailSize);
		return result;
	}
	
	private static <E> E[] newInstance(E[] template, int length) {
		return (E[]) Array.newInstance(template.getClass().getComponentType(), length);
	}
	
	private Arrays() {
		// tool class
	}
}

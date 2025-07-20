package org.codefilarete.tool.collection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Guillaume Mary
 */
public class Collections {
	
	/**
	 * Returns true if the Collection is null or empty
	 * 
	 * @param c a Collection
	 * @return true if the Collection is null or empty
	 */
	public static boolean isEmpty(Collection c) {
		return c == null || c.isEmpty();
	}
	
	/**
	 * Returns true if the Map is null or empty
	 *
	 * @param m a Map
	 * @return true if the Map is null or empty
	 */
	public static boolean isEmpty(Map m) {
		return m == null || m.isEmpty();
	}
	
	/**
	 * Creates a {@link Set} based on object identity for its comparison
	 * @param <E> element type
	 * @return a new {@link Set} based on object identity for its comparison
	 */
	public static <E> Set<E> newIdentitySet() {
		return java.util.Collections.newSetFromMap(new IdentityHashMap<>());
	}
	
	/**
	 * Creates a {@link Set} based on object identity for its comparison
	 * 
	 * @param initialCapacity the wanted initial capacity of the {@link Set}
	 * @param <E> element type
	 * @return a new {@link Set} based on object identity for its comparison
	 */
	public static <E> Set<E> newIdentitySet(int initialCapacity) {
		return java.util.Collections.newSetFromMap(new IdentityHashMap<>(initialCapacity));
	}
	
	/**
	 * Create a Last-In-First-Out {@link Queue}
	 * 
	 * @param <E> element type
	 * @return a new Last-In-First-Out {@link Queue}
	 */
	public static <E> Queue<E> newLifoQueue() {
		return java.util.Collections.asLifoQueue(new ArrayDeque<>());
	}
	
	/**
	 * Same as {@link java.util.Collections#addAll(Collection, Object[])} but with Collection return type
	 * @param c a (non null) collection
	 * @param elements elements to be added to the collection
	 * @param <T> Element type
	 * @param <C> Collection type
	 * @return c
	 */
	public static <T, C extends Collection<? super T>> C addAll(C c, T... elements) {
		c.addAll(Arrays.asList(elements));
		return c;
	}
	
	public static <E> List<E> cat(Collection<? extends E>... collections) {
		List<E> toReturn = new ArrayList<>(collections.length * 10);    // arbitrary size, ArrayList.addAll will adapt
		for (Collection<? extends E> collection : collections) {
			toReturn.addAll(collection);
		}
		return toReturn;
	}
}

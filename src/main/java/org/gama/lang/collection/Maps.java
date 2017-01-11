package org.gama.lang.collection;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * @author Guillaume Mary
 */
public final class Maps {
	
	/**
	 * Allow chaining of the {@link ChainingMap#add(Object, Object)} method and then easily create a Map
	 * @return a new Map instance
	 */
	public static <K, V> ChainingMap<K, V> asMap(K key, V value) {
		return new ChainingMap<K, V>().add(key, value);
	}
	
	/**
	 * Allow chaining of the {@link ChainingMap#add(Object, Object)} method and then easily create a TreeMap
	 * @param comparator the {@link Comparator} for the keys of the {@link TreeMap}
	 * @return a new TreeMap instance
	 */
	public static <K, V> ChainingComparingMap<K, V> asComparingMap(Comparator<K> comparator, K key, V value) {
		return new ChainingComparingMap<K, V>(comparator).add(key, value);
	}
	
	/**
	 * Simple {@link LinkedHashMap} that allows to chain calls to {@link #add(Object, Object)} (same as put) and so quickly create a Map.
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static class ChainingMap<K, V> extends LinkedHashMap<K, V> {
		
		public ChainingMap() {
			super();
		}
		
		public ChainingMap<K, V> add(K key, V value) {
			put(key, value);
			return this;
		}
	}
	
	/**
	 * Simple {@link TreeMap} that allows to chain calls to {@link #add(Object, Object)} (same as put) and so quickly create a Map.
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static class ChainingComparingMap<K, V> extends TreeMap<K, V> {
		
		public ChainingComparingMap(Comparator<K> comparator) {
			super(comparator);
		}
		
		public ChainingComparingMap<K, V> add(K key, V value) {
			put(key, value);
			return this;
		}
	}
}

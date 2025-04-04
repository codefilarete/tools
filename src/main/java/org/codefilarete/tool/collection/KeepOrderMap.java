package org.codefilarete.tool.collection;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map that keeps insertion order. Made for clearer intention (by its name) than {@link java.util.LinkedHashMap} (which is the delegate)
 * 
 * @author Guillaume Mary
 */
public class KeepOrderMap<K, V> extends MapWrapper<K, V> {
	
	public KeepOrderMap() {
		this(new LinkedHashMap<>());
	}
	
	public KeepOrderMap(int initialCapacity) {
		this(new LinkedHashMap<>(initialCapacity));
	}
	
	public KeepOrderMap(int initialCapacity, float loadFactor) {
		this(new LinkedHashMap<>(initialCapacity, loadFactor));
	}
	
	public KeepOrderMap(LinkedHashMap<K, V> delegate) {
		super (delegate);
	}
	
	public KeepOrderMap(Map<? extends K, ? extends V> delegate) {
		super(new LinkedHashMap<> (delegate));
	}
	
	/**
	 * Overridden to refine return type
	 * @return the delegate map cast as a {@link LinkedHashMap}
	 */
	@Override
	public LinkedHashMap<K, V> getDelegate() {
		return (LinkedHashMap<K, V>) super.getDelegate();
	}
	
	/**
	 * Gives element at given index.
	 *
	 * @param index position of element to give 
	 * @return null if this set is empty or index is out of bounds (negative or higher than this set size)
	 */
	public Entry<K, V> getAt(int index) {
		if (isEmpty() || index > size() || index < 0) {
			return null;
		}
		Iterator<Entry<K, V>> iterator = entrySet().iterator();
		int currentIndex = 0;
		Entry<K, V> result = null;
		while (iterator.hasNext()) {
			Entry<K, V> pawn = iterator.next();
			if (currentIndex++ == index) {
				result = pawn;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Removes element at given index.
	 * Does nothing if this set is empty or index is out of bounds (negative or higher than this set size)
	 *
	 * @param index position of element to remove 
	 */
	public void removeAt(int index) {
		if (isEmpty() || index > size() || index < 0) {
			return;
		}
		Iterator<Entry<K, V>> iterator = entrySet().iterator();
		int currentIndex = 0;
		while (iterator.hasNext()) {
			iterator.next();
			if (currentIndex++ == index) {
				iterator.remove();
				break;
			}
		}
	}
}

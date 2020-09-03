package org.gama.lang.collection;

import java.util.LinkedHashMap;

/**
 * Map that keeps insertion order. Made for clearer intention (by its name) than {@link java.util.LinkedHashMap} (which is the surrogate)
 * 
 * @author Guillaume Mary
 */
public class KeepOrderMap<K, V> extends MapWrapper<K, V> {
	
	public KeepOrderMap() {
		this(new LinkedHashMap<>());
	}
	
	public KeepOrderMap(LinkedHashMap<K, V> surrogate) {
		super(surrogate);
	}
	
	/**
	 * Overriden to refine return type
	 * @return the surrogate map casted as a {@link LinkedHashMap}
	 */
	@Override
	public LinkedHashMap<K, V> getSurrogate() {
		return (LinkedHashMap<K, V>) super.getSurrogate();
	}
	
}

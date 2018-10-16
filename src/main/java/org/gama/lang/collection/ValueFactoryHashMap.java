package org.gama.lang.collection;

import java.util.HashMap;

import org.gama.lang.bean.Factory;

/**
 * Specialized {@link ValueFactoryMap} for HashMap.
 *
 * @author Guillaume Mary
 */
public class ValueFactoryHashMap<K, V> extends ValueFactoryMap<K, V> {

	public ValueFactoryHashMap(Factory<K, V> factory) {
		super(new HashMap<>(), factory);
	}

	public ValueFactoryHashMap(int initialCapacity, Factory<K, V> factory) {
		super(new HashMap<>(initialCapacity), factory);
	}

	public ValueFactoryHashMap(int initialCapacity, float loadFactor, Factory<K, V> factory) {
		super(new HashMap<>(initialCapacity, loadFactor), factory);
	}
}

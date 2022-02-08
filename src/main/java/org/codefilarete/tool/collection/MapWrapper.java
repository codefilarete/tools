package org.codefilarete.tool.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * {@link Map} that wraps another one and delegates all its methods to it without any additionnal feature.
 * Made for overriding only some targeted methods.
 * 
 * @author Guillaume Mary
 */
public class MapWrapper<K, V> implements Map<K, V> {
	
	private final Map<K, V> surrogate;
	
	public MapWrapper(Map<K, V> surrogate) {
		this.surrogate = surrogate;
	}
	
	public Map<K, V> getSurrogate() {
		return surrogate;
	}
	
	@Override
	public int size() {
		return surrogate.size();
	}
	
	@Override
	public boolean isEmpty() {
		return surrogate.isEmpty();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return surrogate.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		return surrogate.containsValue(value);
	}
	
	@Override
	public V get(Object key) {
		return surrogate.get(key);
	}
	
	@Override
	public V put(K key, V value) {
		return surrogate.put(key, value);
	}
	
	@Override
	public V remove(Object key) {
		return surrogate.remove(key);
	}
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		surrogate.putAll(m);
	}
	
	@Override
	public void clear() {
		surrogate.clear();
	}
	
	@Override
	public Set<K> keySet() {
		return surrogate.keySet();
	}
	
	@Override
	public Collection<V> values() {
		return surrogate.values();
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		return surrogate.entrySet();
	}
	
	@Override
	public boolean equals(Object o) {
		return surrogate.equals(o);
	}
	
	@Override
	public int hashCode() {
		return surrogate.hashCode();
	}
	
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return surrogate.getOrDefault(key, defaultValue);
	}
	
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		surrogate.forEach(action);
	}
	
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		surrogate.replaceAll(function);
	}
	
	@Override
	public V putIfAbsent(K key, V value) {
		return surrogate.putIfAbsent(key, value);
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		return surrogate.remove(key, value);
	}
	
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return surrogate.replace(key, oldValue, newValue);
	}
	
	@Override
	public V replace(K key, V value) {
		return surrogate.replace(key, value);
	}
	
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return surrogate.computeIfAbsent(key, mappingFunction);
	}
	
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return surrogate.computeIfPresent(key, remappingFunction);
	}
	
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return surrogate.compute(key, remappingFunction);
	}
	
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return surrogate.merge(key, value, remappingFunction);
	}
}

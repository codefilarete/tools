package org.codefilarete.tool.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Storage of key-value pairs based on key {@link System#identityHashCode} to avoid loss of bean in classical
 * {@link Map} : because those are based on instance hashCode, beans can't be retrieved due to some hashCode change
 * when computation is based on incompletely filled attributes, such as collection.
 * <p>
 * It does not implement {@link Map} because it is mainly used as a marking name instead of the anonymous Map class
 * which only instantiation brings the implementation : by this name the developer clearly says its intention (and
 * should add a comment why such a Map is required in its algorithm ;) ).
 * It could also have been replaced by {@link java.util.IdentityHashMap} (or use it internally) but, first it would
 * have broken initial need, and overall one have to know that {@link java.util.IdentityHashMap} compares keys on their
 * {@link System#identityHashCode} but also its values which, beyond being not really intuitive, might not be required
 * by production code, and brings some difficulties in tests (because even same Strings are different with '==').
 * 
 * @param <K> key type
 * @param <V> value type
 */
public class IdentityMap<K, V> {
	
	private final Map<Integer, V> delegate;
	
	public IdentityMap() {
		this.delegate = new HashMap<>();
	}
	
	public IdentityMap(int capacity) {
		this.delegate = new HashMap<>(capacity);
	}
	
	protected int hash(K key) {
		return System.identityHashCode(key);
	}
	
	public V put(K key, V value) {
		return this.delegate.put(hash(key), value);
	}
	
	public V get(K key) {
		return this.delegate.get(hash(key));
	}
	
	public boolean containsKey(K key) {
		return delegate.containsKey(hash(key));
	}
	
	public V putIfAbsent(K key, V value) {
		return delegate.putIfAbsent(hash(key), value);
	}
	
	public V putIfAbsent(K key, Supplier<V> value) {
		return delegate.putIfAbsent(hash(key), value.get());
	}
	
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return delegate.computeIfAbsent(hash(key), k -> mappingFunction.apply(key));
	}
	
	public Collection<V> values() {
		return delegate.values();
	}
	
	public V remove(K key) {
		return this.delegate.remove(hash(key));
	}
	
	public void clear() {
		this.delegate.clear();
	}
	
	public int size() {
		return this.delegate.size();
	}
	
	/**
	 * Exposes internal storage, made for testing purpose, not expected to be used in production
	 * @return internal storage
	 */
	public Map<Integer, V> getDelegate() {
		return delegate;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IdentityMap<?, ?> that = (IdentityMap<?, ?>) o;
		return delegate.equals(that.delegate);
	}
	
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}
	
	/**
	 * Implemented for easier debug
	 *
	 * @return delegate's toString()
	 */
	@Override
	public String toString() {
		return delegate.toString();
	}
}
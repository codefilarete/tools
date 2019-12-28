package org.gama.lang.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Set that keeps insertion order. Made for clearer intention (by its name) than {@link LinkedHashSet} (which is the surrogate)
 * 
 * @author Guillaume Mary
 */
public class KeepOrderSet<E> implements Set<E> {
	
	private LinkedHashSet<E> delegate = new LinkedHashSet<>();
	
	public KeepOrderSet(E ... e) {
		this(Arrays.asList(e));
	}
	
	public KeepOrderSet(List<E> elements) {
		this.delegate.addAll(elements);
	}
	
	public int size() {
		return delegate.size();
	}
	
	public boolean isEmpty() {
		return delegate.isEmpty();
	}
	
	public boolean contains(Object o) {
		return delegate.contains(o);
	}
	
	public Iterator<E> iterator() {
		return delegate.iterator();
	}
	
	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return delegate.toArray(a);
	}
	
	public boolean add(E e) {
		return delegate.add(e);
	}
	
	@Override
	public boolean remove(Object o) {
		return delegate.remove(o);
	}
	
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}
	
	public boolean addAll(Collection<? extends E> c) {
		return delegate.addAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return delegate.retainAll(c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return delegate.removeAll(c);
	}
	
	@Override
	public void clear() {
		delegate.clear();
	}
	
	public boolean equals(Object o) {
		return o instanceof KeepOrderSet && delegate.equals(o);
	}
	
	public int hashCode() {
		return delegate.hashCode();
	}
	
	public Stream<E> stream() {
		return this.delegate.stream();
	}
	
	/**
	 * @return a copy of its content as a {@link LinkedHashSet}
	 */
	public LinkedHashSet<E> asSet() {
		return new LinkedHashSet<>(delegate);
	}
}

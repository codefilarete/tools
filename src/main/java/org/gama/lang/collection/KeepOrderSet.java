package org.gama.lang.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Fake Set that keeps insertion order
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
		return false;
	}
	
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}
	
	public boolean addAll(Collection<? extends E> c) {
		return delegate.addAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}
	
	@Override
	public void clear() {
		
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
	 * Renvoie l'équivalent de cette instance sous la forme d'un vrai Set (cloné)
	 * @return le contenu de cette instance dans un LinkedHashSet
	 */
	public LinkedHashSet<E> asSet() {
		return new LinkedHashSet<>(delegate);
	}
}

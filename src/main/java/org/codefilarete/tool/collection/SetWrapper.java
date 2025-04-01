package org.codefilarete.tool.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@link Set} that wraps another one and delegates all its methods to it without any additionnal feature.
 * Made for overriding only some targeted methods.
 * 
 * @author Guillaume Mary
 */
public class SetWrapper<E> implements Set<E> {
	
	private final Set<E> delegate;
	
	public SetWrapper(Set<E> delegate) {
		this.delegate = delegate;
	}
	
	public Set<E> getDelegate() {
		return delegate;
	}
	
	@Override
	public int size() {
		return delegate.size();
	}
	
	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}
	
	@Override
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
	
	@Override
	public boolean add(E e) {
		return delegate.add(e);
	}
	
	@Override
	public boolean remove(Object o) {
		return delegate.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll(c);
	}
	
	@Override
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
	
	@Override
	public boolean equals(Object o) {
		return delegate.equals(o);
	}
	
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}
	
	@Override
	public Spliterator<E> spliterator() {
		return delegate.spliterator();
	}
	
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return delegate.removeIf(filter);
	}
	
	@Override
	public Stream<E> stream() {
		return delegate.stream();
	}
	
	@Override
	public Stream<E> parallelStream() {
		return delegate.parallelStream();
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		delegate.forEach(action);
	}
	
	@Override
	public String toString() {
		return delegate.toString();
	}
}

package org.gama.lang.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * 
 * @param <E>
 * @param <C>
 */
public class ReadOnlyCollection<E, C extends Collection<E>> extends CollectionWrapper<E, C> {
	
	protected ReadOnlyCollection(C delegate) {
		super(delegate);
	}
	
	@Override
	public final boolean add(E e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean removeIf(Predicate<? super E> filter) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<E> iterator() {
		return ReadOnlyIterator.wrap(delegate.iterator());
	}
}
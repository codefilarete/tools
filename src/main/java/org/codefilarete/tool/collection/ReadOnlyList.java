package org.codefilarete.tool.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A marking class for {@link List} which elements cannot be added nor removed.
 *
 * @param <E> element type
 * @author Guillaume Mary
 */
public class ReadOnlyList<E> extends ReadOnlyCollection<E, List<E>> implements List<E> {
	
	public ReadOnlyList() {
		this(new ArrayList<>());
	}
	
	public ReadOnlyList(int initialCapacity) {
		this(new ArrayList<>(initialCapacity));
	}
	
	public ReadOnlyList(List<? extends E> list) {
		super((List<E>) list);
	}
	
	@Override
	public E get(int index) {
		return delegate.get(index);
	}
	
	@Override
	public Iterator<E> iterator() {
		return listIterator();
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		return new ReadOnlyListIterator(index);
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new ReadOnlyList<>(delegate.subList(fromIndex, toIndex));
	}
	
	@Override
	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}
	
	@Override
	public final E set(int index, E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final void add(int index, E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final E remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final void replaceAll(UnaryOperator<E> operator) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public final void sort(Comparator<? super E> c) {
		throw new UnsupportedOperationException();
	}
	
	class ReadOnlyListIterator extends ReadOnlyIterator<E> implements ListIterator<E> {
		
		private final ListIterator<? extends E> delegateIterator;
		
		ReadOnlyListIterator(int index) {
			delegateIterator = delegate.listIterator(index);
		}
		
		@Override
		public boolean hasNext() {
			return delegateIterator.hasNext();
		}
		
		@Override
		public E next() {
			return delegateIterator.next();
		}
		
		@Override
		public boolean hasPrevious() {
			return delegateIterator.hasPrevious();
		}
		
		@Override
		public E previous() {
			return delegateIterator.previous();
		}
		
		@Override
		public int nextIndex() {
			return delegateIterator.nextIndex();
		}
		
		@Override
		public int previousIndex() {
			return delegateIterator.previousIndex();
		}
		
		@Override
		public void forEachRemaining(Consumer<? super E> action) {
			delegateIterator.forEachRemaining(action);
		}
		
		@Override
		public final void set(E e) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public final void add(E e) {
			throw new UnsupportedOperationException();
		}
	}
}
package org.gama.lang.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.gama.lang.Duo;

/**
 * {@link Iterator} of 2 others by giving {@link Duo}s. Gives elements while 2 Iterators have elements together.
 * 
 * @param <K> type of first {@link Iterator}'s element
 * @param <V> type of second {@link Iterator}'s element
 * @author Guillaume Mary
 */
public class PairIterator<K, V> implements Iterator<Duo<K, V>> {
	
	protected Iterator<? extends K> iterator1;
	protected Iterator<? extends V> iterator2;
	
	public PairIterator(Iterable<? extends K> iterator1, Iterable<? extends V> iterator2) {
		this(iterator1.iterator(), iterator2.iterator());
	}
	
	public PairIterator(Iterator<? extends K> iterator1, Iterator<? extends V> iterator2) {
		this.iterator1 = iterator1;
		this.iterator2 = iterator2;
	}
	
	@Override
	public boolean hasNext() {
		return iterator1.hasNext() && iterator2.hasNext();
	}
	
	@Override
	public Duo<K, V> next() {
		return new Duo<>(iterator1.next(), iterator2.next());
	}
	
	@Override
	public void remove() {
		iterator1.remove();
		iterator2.remove();
	}
	
	/**
	 * {@link Iterator} which continues while one of the surrogate {@link Iterator} still has elements. So it stops when both {@link Iterator}s are
	 * drained.
	 * Gives the pair as a {@link Duo}.
	 * Missing values are overridable through {@link #getMissingKey()} and {@link #getMissingValue()}.
	 *
	 * @param <K> type of first {@link Iterator}'s element
	 * @param <V> type of second {@link Iterator}'s element
	 */
	public static class UntilBothIterator<K, V> extends PairIterator<K, V> {
		
		public UntilBothIterator(Iterable<? extends K> iterator1, Iterable<? extends V> iterator2) {
			super(iterator1, iterator2);
		}
		
		public UntilBothIterator(Iterator<? extends K> iterator1, Iterator<? extends V> iterator2) {
			super(iterator1, iterator2);
		}
		
		@Override
		public boolean hasNext() {
			return iterator1.hasNext() || iterator2.hasNext();
		}
		
		@Override
		public Duo<K, V> next() {
			K val1;
			if (iterator1.hasNext()) {
				val1 = iterator1.next();
			} else {
				val1 = getMissingKey();
			}
			V val2;
			if (iterator2.hasNext()) {
				val2 = iterator2.next();
			} else {
				val2 = getMissingValue();
			}
			return new Duo<>(val1, val2);
		}
		
		public K getMissingKey() {
			return null;
		}
		
		public V getMissingValue() {
			return null;
		}
	}
	
	/**
	 * {@link Iterator} running indefinitely, starts by giving surrogate's elements, then gives what {@link #getMissingElement()} returns
	 * (default is null).
	 * 
	 * @param <E>
	 */
	public static class InfiniteIterator<E> implements Iterator<E> {
		
		private Iterator<E> delegate;
		
		public InfiniteIterator(Iterator<E> delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public boolean hasNext() {
			return true;
		}
		
		@Override
		public E next() {
			try {
				return delegate.next();
			} catch (NoSuchElementException e) {
				return getMissingElement();
			}
		}
		
		@Override
		public void remove() {
			delegate.remove();
		}
		
		public E getMissingElement() {
			return null;
		}
	}
	
	/**
	 * {@link Iterator} without any element. Stub for API that needs an {@link Iterator}.
	 * @param <E>
	 */
	public static class EmptyIterator<E> implements Iterator<E> {
		
		@Override
		public boolean hasNext() {
			return false;
		}
		
		@Override
		public E next() {
			throw new NoSuchElementException();
		}
		
		@Override
		public void remove() {
			
		}
	}
}

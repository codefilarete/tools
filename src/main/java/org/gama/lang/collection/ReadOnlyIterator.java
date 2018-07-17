package org.gama.lang.collection;

import java.util.Iterator;

/**
 * A marking class for {@link Iterator} which elements cannot be removed.
 * 
 * @author Guillaume Mary
 */
public abstract class ReadOnlyIterator<E> implements Iterator<E> {
	
	public static <C> ReadOnlyIterator<C> wrap(Iterable<C> iterable) {
		return new ReadOnlyWrappedIterator<>(iterable.iterator());
	}
	
	public static <C> ReadOnlyIterator<C> wrap(Iterator<C> iterator) {
		return new ReadOnlyWrappedIterator<>(iterator);
	}
	
	/** Overriden to mark it final */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}
	
	private static class ReadOnlyWrappedIterator<C> extends ReadOnlyIterator<C> {
		
		private final Iterator<C> surrogate;
		
		public ReadOnlyWrappedIterator(Iterator<C> surrogate) {
			this.surrogate = surrogate;
		}
		
		@Override
		public boolean hasNext() {
			return surrogate.hasNext();
		}
		
		@Override
		public C next() {
			return surrogate.next();
		}
	}
}

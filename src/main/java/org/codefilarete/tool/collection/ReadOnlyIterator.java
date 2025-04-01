package org.codefilarete.tool.collection;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A marking class for {@link Iterator} which elements cannot be removed.
 * 
 * @author Guillaume Mary
 */
public abstract class ReadOnlyIterator<E> implements Iterator<E> {
	
	/**
	 * Simple shortcut to {@link ReadOnlyWrappedIterator#ReadOnlyWrappedIterator(Iterator)}, made because it seems more readable
	 * at usage than instantiating the class. Totally subjective.
	 *
	 * @param iterable the {@link Iterable} that the iterator will be taken on
	 * @param <C> iterator element type
	 * @return a new {@link ReadOnlyIterator} wrapping given {@link Iterable} iterator
	 */
	public static <C> ReadOnlyIterator<C> wrap(Iterable<C> iterable) {
		return new ReadOnlyWrappedIterator<>(iterable.iterator());
	}
	
	/**
	 * Simple shortcut to {@link ReadOnlyWrappedIterator#ReadOnlyWrappedIterator(Iterator)}, made because it seems more readable
	 * at usage than instantiating the class. Totally subjective.
	 *
	 * @param iterator the wrapped iterator
	 * @param <C> iterator element type
	 * @return a new {@link ReadOnlyIterator} wrapping given {@link Iterator}
	 */
	public static <C> ReadOnlyIterator<C> wrap(Iterator<C> iterator) {
		return new ReadOnlyWrappedIterator<>(iterator);
	}
	
	/** Overridden to mark it final */
	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}
	
	private static class ReadOnlyWrappedIterator<C> extends ReadOnlyIterator<C> {
		
		private final Iterator<C> delegate;
		
		public ReadOnlyWrappedIterator(Iterator<C> delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}
		
		@Override
		public C next() {
			return delegate.next();
		}
		
		@Override
		public void forEachRemaining(Consumer<? super C> action) {
			delegate.forEachRemaining(action);
		}
	}
}

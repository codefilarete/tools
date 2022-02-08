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
	 * at usage than instanciating the class. Totally subjective.
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
	 * at usage than instanciating the class. Totally subjective.
	 *
	 * @param iterator the wrapped iterator
	 * @param <C> iterator element type
	 * @return a new {@link ReadOnlyIterator} wrapping given {@link Iterator}
	 */
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
		
		@Override
		public void forEachRemaining(Consumer<? super C> action) {
			surrogate.forEachRemaining(action);
		}
	}
}

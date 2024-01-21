package org.codefilarete.tool.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} that iterates over other given {@link Iterator}s
 * 
 * @author Guillaume Mary
 */
public class IteratorIterator<E> implements Iterator<E> {
	
	private final Iterator<Iterable<E>> iterables;
	
	private Iterator<E> currentIterator;
	
	@SafeVarargs // method body doesn't handle improperly varargs parameter so it would generate ClassCastException
	public IteratorIterator(Iterator<E> ... iterables) {
		// we wrap given iterator array into an array of iterables, no very cleanly done but it's a very local usage.
		this(new Iterator<Iterable<E>>() {
			
			private final Iterator<Iterator<E>> delegate = new ArrayIterator<>(iterables);
			
			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}
			
			@Override
			public Iterable<E> next() {
				return delegate::next;
			}
		});
	}
	
	@SafeVarargs // method body doesn't handle improperly varargs parameter so it would generate ClassCastException
	public IteratorIterator(Iterable<E> ... iterables) {
		this(new ArrayIterator<>(iterables));
	}
	
	public IteratorIterator(Iterable<Iterable<E>> iterables) {
		this(iterables.iterator());
	}
	
	public IteratorIterator(Iterator<Iterable<E>> iterables) {
		this.iterables = iterables;
	}
	
	@Override
	public boolean hasNext() {
		if (currentIterator == null || !currentIterator.hasNext()) {
			boolean found = false;
			while(!found) {
				if (iterables.hasNext()) {
					currentIterator = iterables.next().iterator();
					found = currentIterator.hasNext();
				} else {
					return false;
				}
			}
		}
		return currentIterator.hasNext();
	}
	
	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return currentIterator.next();
	}
	
	@Override
	public void remove() {
		currentIterator.remove();
	}
}

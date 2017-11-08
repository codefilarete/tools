package org.gama.lang.collection;

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
	
	public IteratorIterator(Iterable<E> seed, Iterable<E> ... iterables) {
		this(Collections.cat(Arrays.asList(seed), Arrays.asList(iterables)).iterator());
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

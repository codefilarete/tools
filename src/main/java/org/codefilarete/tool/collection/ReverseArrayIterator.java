package org.codefilarete.tool.collection;

import java.util.NoSuchElementException;

/**
 * @author Guillaume Mary
 */
public class ReverseArrayIterator<E> extends ReadOnlyIterator<E> {
	
	private final E[] iterable;
	
	private int currentIndex;
	
	public ReverseArrayIterator(E[] iterable) {
		this.iterable = iterable;
		this.currentIndex = iterable.length;
	}
	
	@Override
	public boolean hasNext() {
		return currentIndex > 0;
	}
	
	@Override
	public E next() {
		if (!hasNext()) {
			// this is necessary to be compliant with Iterator#next(..) contract
			throw new NoSuchElementException();
		}
		return iterable[--currentIndex];
	}
}

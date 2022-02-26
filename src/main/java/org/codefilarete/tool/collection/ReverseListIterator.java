package org.codefilarete.tool.collection;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Guillaume Mary
 */
public class ReverseListIterator<E> implements Iterator<E> {
	
	private final List<E> iterable;
	private int currentIndex;
	
	public ReverseListIterator(List<E> iterable) {
		this.iterable = iterable;
		this.currentIndex = iterable.size();
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
		return iterable.get(--currentIndex);
	}
	
	@Override
	public void remove() {
		iterable.remove(currentIndex);
	}
}

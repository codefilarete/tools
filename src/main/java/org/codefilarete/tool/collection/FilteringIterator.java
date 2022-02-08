package org.codefilarete.tool.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * @author Guillaume Mary
 */
public class FilteringIterator<T> implements Iterator<T> {
	
	private final Iterator<T> delegate;
	private final Predicate<T> acceptFilter;
	private T lookAhead;
	private boolean hasMatcher;
	
	public FilteringIterator(Iterator<T> delegate, Predicate<T> acceptFilter) {
		this.delegate = delegate;
		this.acceptFilter = acceptFilter;
	}
	
	@Override
	public boolean hasNext() {
		consumeDelegateUntilMatch();
		return hasMatcher;
	}
	
	private void consumeDelegateUntilMatch() {
		while (!hasMatcher && delegate.hasNext()) {
			lookAhead = delegate.next();
			if (acceptFilter.test(lookAhead)) {
				hasMatcher = true;
			}
		}
	}
	
	@Override
	public T next() {
		if (!hasMatcher) {
			throw new NoSuchElementException();
		}
		hasMatcher = false;
		return lookAhead;
	}
	
	@Override
	public void remove() {
		delegate.remove();
	}
}
package org.codefilarete.tool.collection;

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
		return iterable[--currentIndex];
	}
}

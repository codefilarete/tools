package org.gama.lang.collection;

import java.util.NoSuchElementException;

/**
 * Wraps an array into an Iterator. Allows reuse of an array in the Iterators API.
 * 
 * @author Guillaume Mary
 */
public class ArrayIterator<O> extends ReadOnlyIterator<O> {

	private final O[] array;
	private int currentIndex = 0;
	private final int maxIndex;
	
	@SafeVarargs // method body doesn't handle improperly varargs parameter so it would generate ClassCastException 
	public ArrayIterator(O ... array) {
		this.array = array;
		this.maxIndex = array.length;
	}

	@Override
	public boolean hasNext() {
		return currentIndex < maxIndex;
	}

	@Override
	public O next() {
		try {
			return this.array[currentIndex++];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new NoSuchElementException();
		}
	}
}

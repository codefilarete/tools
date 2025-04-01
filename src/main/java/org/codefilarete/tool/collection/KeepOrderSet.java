package org.codefilarete.tool.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Set that keeps insertion order. Made for clearer intention (by its name) than {@link LinkedHashSet} (which is the delegate)
 * 
 * @author Guillaume Mary
 */
public class KeepOrderSet<E> extends SetWrapper<E> {
	
	public KeepOrderSet() {
		super(new LinkedHashSet<>());
	}
	
	public KeepOrderSet(E ... e) {
		this(Arrays.asList(e));
	}
	
	/**
	 * Initializes a new instance from given collection, order in the one of collection iteration.
	 * 
	 * @param elements the elements that fill the new instance
	 */
	public KeepOrderSet(Collection<E> elements) {
		super(new LinkedHashSet<>(elements));
	}
	
	/**
	 * Gives element at given index
	 * @param index position of element to give 
	 * @return null if this set is empty or index is out of bounds (negative or higher than this set size)
	 */
	public E getAt(int index) {
		if (isEmpty() || index > size() || index < 0) {
			return null;
		}
		Iterator<E> iterator = iterator();
		int currentIndex = 0;
		E result = null;
		while (iterator.hasNext()) {
			E pawn = iterator.next();
			if (currentIndex++ == index) {
				result = pawn;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Removes element at given index.
	 * Does nothing if this set is empty or index is out of bounds (negative or higher than this set size)
	 * 
	 * @param index position of element to remove 
	 */
	public void removeAt(int index) {
		if (isEmpty() || index > size() || index < 0) {
			return;
		}
		Iterator<E> iterator = iterator();
		int currentIndex = 0;
		while (iterator.hasNext()) {
			iterator.next();
			if (currentIndex++ == index) {
				iterator.remove();
				break;
			}
		}
	}
	
	/**
	 * Overridden to refine return type
	 * @return the delegate map cast as a {@link LinkedHashSet}
	 */
	@Override
	public LinkedHashSet<E> getDelegate() {
		return (LinkedHashSet<E>) super.getDelegate();
	}
}

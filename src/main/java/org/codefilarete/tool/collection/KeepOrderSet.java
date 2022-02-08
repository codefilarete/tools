package org.codefilarete.tool.collection;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Set that keeps insertion order. Made for clearer intention (by its name) than {@link LinkedHashSet} (which is the surrogate)
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
	 * Overriden to refine return type
	 * @return the surrogate map casted as a {@link LinkedHashSet}
	 */
	@Override
	public LinkedHashSet<E> getSurrogate() {
		return (LinkedHashSet<E>) super.getSurrogate();
	}
	
	/**
	 * @return a copy of its content as a {@link LinkedHashSet}
	 */
	public LinkedHashSet<E> asSet() {
		return new LinkedHashSet<>(getSurrogate());
	}
}

package org.codefilarete.tool.bean;

import java.util.NoSuchElementException;

import org.codefilarete.tool.collection.ReadOnlyIterator;

/**
 * Iterator over the class hierarchy of a class
 * 
 * @author Guillaume Mary
 */
public class ClassIterator extends ReadOnlyIterator<Class> {
	
	private Class currentClass;
	private final Class topBoundAncestor;
	
	/**
	 * Constructor for an {@link java.util.Iterator} from fromClass to {@link Object} class included
	 * @param fromClass the start point (included) of this {@link java.util.Iterator}
	 */
	public ClassIterator(Class fromClass) {
		this(fromClass, Object.class);
	}
	
	/**
	 * Constructor for an {@link java.util.Iterator} from fromClass to toClass included
	 * @param fromClass the start point (included) of this {@link java.util.Iterator}
	 * @param toClass end point (included) of this {@link java.util.Iterator}, null authorized
	 */
	public ClassIterator(Class fromClass, Class toClass) {
		this.currentClass = fromClass;
		this.topBoundAncestor = toClass;
	}
	
	@Override
	public boolean hasNext() {
		return currentClass != topBoundAncestor && currentClass.getSuperclass() != null;
	}
	
	@Override
	public Class next() {
		if (!hasNext()) {
			// this is necessary to be compliant with Iterator#next(..) contract
			throw new NoSuchElementException();
		}
		Class next = currentClass;
		currentClass = currentClass.getSuperclass();
		return next;
	}
}

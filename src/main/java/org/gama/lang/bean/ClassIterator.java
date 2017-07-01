package org.gama.lang.bean;

import org.gama.lang.collection.ReadOnlyIterator;

/**
 * Iterator over the class hierarchy of a class
 * 
 * @author Guillaume Mary
 */
public class ClassIterator extends ReadOnlyIterator<Class> {
	
	private Class currentClass, topBoundAncestor;
	
	/**
	 * Constructor for an {@link java.util.Iterator} from fromClass to {@link Object} class included
	 * @param fromClass the start point (included) of this {@link java.util.Iterator}
	 */
	public ClassIterator(Class fromClass) {
		this(fromClass, null);
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
		return !Objects.equalsWithNull(currentClass, topBoundAncestor);
	}
	
	@Override
	public Class next() {
		Class next = currentClass;
		currentClass = currentClass.getSuperclass();
		return next;
	}
}

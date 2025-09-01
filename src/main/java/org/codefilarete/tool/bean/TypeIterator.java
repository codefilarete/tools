package org.codefilarete.tool.bean;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;

import org.codefilarete.tool.collection.ReadOnlyIterator;

/**
 * Iterator over the types (classes and interfaces) of a hierarchy.
 * The iteration is by class-floor: it returns first the class and its interfaces, then the superclass and its interfaces, etc.
 *
 * @see ClassIterator
 * @see InterfaceIterator
 * @author Guillaume Mary
 */
public class TypeIterator extends ReadOnlyIterator<Class<?>> {
	
	private ClassIterator classIterator;
	@Nullable // null at initialization
	private InterfaceIterator interfaceIterator;
	
	/**
	 * Constructor for an {@link java.util.Iterator} from fromClass to {@link Object} class included
	 * @param fromClass the start point (included) of this {@link java.util.Iterator}
	 */
	public TypeIterator(Class<?> fromClass) {
		this(fromClass, Object.class);
	}
	
	/**
	 * Constructor for an {@link java.util.Iterator} from fromClass to toClass included
	 * @param fromClass the start point (included) of this {@link java.util.Iterator}
	 * @param toClass end point (included) of this {@link java.util.Iterator}, null authorized
	 */
	public TypeIterator(Class<?> fromClass, Class<?> toClass) {
		this.classIterator = new ClassIterator(fromClass, toClass);
	}
	
	@Override
	public boolean hasNext() {
		return classIterator.hasNext() || interfaceIterator != null && interfaceIterator.hasNext();
	}
	
	@Override
	public Class<?> next() {
		if (!hasNext()) {
			// this is necessary to be compliant with Iterator#next(..) contract
			throw new NoSuchElementException();
		}
		// we detect the very first iteration (by checking interfaceIterator against null) to return the initial class
		if (interfaceIterator == null) {
			return prepareNextClassScan();
		} else if (interfaceIterator.hasNext()) {
			return interfaceIterator.next();
		} else if (classIterator.hasNext()) {
			return prepareNextClassScan();
		} else {
			return null;
		}
	}
	
	private Class<?> prepareNextClassScan() {
		Class<?> next = classIterator.next();
		interfaceIterator = new InterfaceIterator(new ClassIterator(next, next.getSuperclass()));
		return next;
	}
}

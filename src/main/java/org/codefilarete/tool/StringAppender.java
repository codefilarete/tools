package org.codefilarete.tool;

import java.util.Iterator;

/**
 * Kind of StringBuilder aimed at being simpler by its API.
 * This class can be inherited and it {@link #cat(Object)} method overridden to append any appropriate representation of its given object, according
 * to its type for example.
 * All other methods refer to it, therefore, by overriding this method, it is guaranteed that the representation will also be applied
 * by other methods.
 *
 * @author Guillaume Mary
 * @see #cat(Object)
 */
public class StringAppender implements CharSequence {
	
	protected StringBuilder appender;
	
	public StringAppender() {
		appender = new StringBuilder();
	}
	
	public StringAppender(int capacity) {
		appender = new StringBuilder(capacity);
	}
	
	public StringAppender(StringBuilder appender) {
		this.appender = appender;
	}
	
	public StringAppender(Object... objects) {
		this();
		cat(objects);
	}
	
	/**
	 * Appends given objects to current instance.
	 * This method is expected o be overridden to let the user append an appropriate representation of the object, typically according to its type.
	 * All other methods refer to this method, therefore, by overriding this method, it is guaranteed that the representation will also be applied
	 * by other methods.
	 *
	 * @param object object to be appended
	 * @return this
	 * @see #ccat(Object...)
	 */
	public StringAppender cat(Object object) {
		appender.append(object);
		return this;
	}
	
	/**
	 * Appends given objects to current instance
	 * @param object1 object to be appended
	 * @param object2 object to be appended
	 * @return this
	 * @see #ccat(Object...)
	 */
	public StringAppender cat(Object object1, Object object2) {
		// we skip an array creation by calling cat() multiple times
		return cat(object1).cat(object2);
	}
	
	/**
	 * Appends given objects to current instance
	 * @param object1 object to be appended
	 * @param object2 object to be appended
	 * @param object3 object to be appended
	 * @return this
	 * @see #ccat(Object...)
	 */
	public StringAppender cat(Object object1, Object object2, Object object3) {
		// we skip an array creation by calling cat() multiple times
		return cat(object1).cat(object2).cat(object3);
	}
	
	/**
	 * Appends given objects to current instance
	 * @param objects objects to be appended
	 * @return this
	 * @see #ccat(Object...)
	 */
	public StringAppender cat(Object... objects) {
		for (Object s : objects) {
			cat(s);
		}
		return this;
	}
	
	/**
	 * Appends objects present in given {@link Iterable}
	 * @param objects objects to be appended
	 * @return this
	 */
	public StringAppender cat(Iterable<?> objects) {
		for (Object s : objects) {
			cat(s);
		}
		return this;
	}
	
	public StringAppender catAt(int index, Object... objects) {
		// We make this method uses cat() to ensure that any override of cat() is also used by this method
		// otherwise we could simply use appender.insert(..) 
		// So, we swap this.appender with a new StringBuilder to make cat(..) fill it 
		StringBuilder previous = this.appender;
		StringBuilder target = new StringBuilder();
		this.appender = target;
		// cat() will append to the temporary StringBuilder
		cat(objects);
		this.appender = previous;
		// finally inserting at index
		this.appender.insert(index, target);
		return this;
	}
	
	/**
	 * Appends some object if a condition is met
	 * @param condition indicates if given objects must be appended
	 * @param objects objects to be appended
	 * @return this
	 */
	public StringAppender catIf(boolean condition, Object... objects) {
		if (condition) {
			cat(objects);
		}
		return this;
	}
	
	/**
	 * Appends given objects, except last one which is used as a separator between them
	 * @param objects objects to be appended, last one is used as a separator
	 * @return this
	 */
	public StringAppender ccat(Object... objects) {
		return ccat(objects, objects[objects.length - 1], objects.length - 1);
	}
	
	/**
	 * Appends objects present in given {@link Iterable}, separated by given separator
	 * @param objects objects to be appended
	 * @param sep separator of objects
	 * @return this
	 */
	public StringAppender ccat(Iterable<?> objects, Object sep) {
		Iterator<?> iterator = objects.iterator();
		while (iterator.hasNext()) {
			Object s = iterator.next();
			cat(s).catIf(iterator.hasNext(), sep);
		}
		return this;
	}
	
	/**
	 * Appends objects present in given array, separated by given separator
	 * @param objects objects to be appended
	 * @param sep separator of objects
	 * @return this
	 */
	public StringAppender ccat(Object[] objects, Object sep) {
		return ccat(objects, sep, objects.length);
	}
	
	/**
	 * Appends <code>objectCount</code> first objects of given array, separated by given separator
	 * @param objects objects to be appended
	 * @param sep separator of objects
	 * @param objectCount count of object to be taken in input array
	 * @return this
	 */
	public StringAppender ccat(Object[] objects, Object sep, int objectCount) {
		if (objects.length > 0) {
			int lastIndex = objectCount < 1 ? 0 : objectCount - 1;
			for (int i = 0; i < objectCount; i++) {
				cat(objects[i]).catIf(i != lastIndex, sep);
			}
		}
		return this;
	}
	
	/**
	 * Appends some objects at the beginning and end of current instance
	 * @param open the object to be added at very first place of current instance
	 * @param close the object to be appended at the end of current instance
	 * @return this
	 */
	public StringAppender wrap(Object open, Object close) {
		catAt(0, open).cat(close);
		return this;
	}
	
	/**
	 * Implemented to print the underlying char sequence
	 * @return a {@link String} representation of all appended objects
	 */
	@Override
	public String toString() {
		return appender.toString();
	}
	
	/**
	 * Removes some last characters of current instance.
	 * @param nbChar count of character to be removed
	 * @return this
	 */
	public StringAppender cutTail(int nbChar) {
		int newLength = length() - nbChar;
		if (newLength > -1) {
			appender.setLength(newLength);
		}
		return this;
	}
	
	/**
	 * Removes some first characters of current instance.
	 * @param nbChar count of character to be removed
	 * @return this
	 */
	public StringAppender cutHead(int nbChar) {
		appender.delete(0, nbChar);
		return this;
	}
	
	/**
	 * Gives access to delegate appender, because it can be useful to append directly to the standard API StringBuilder
	 *
	 * @return the underlying appender
	 */
	public StringBuilder getAppender() {
		return appender;
	}
	
	@Override
	public int length() {
		return appender.length();
	}
	
	@Override
	public char charAt(int index) {
		return appender.charAt(index);
	}
	
	@Override
	public CharSequence subSequence(int start, int end) {
		return appender.subSequence(start, end);
	}
	
}

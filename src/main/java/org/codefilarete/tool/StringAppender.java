package org.codefilarete.tool;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Kind of StringBuilder aimed at being simpler by its API.
 *
 * @author Guillaume Mary
 */
public class StringAppender implements Serializable, CharSequence {
	
	protected StringBuilder appender;
	
	public StringAppender() {
		appender = new StringBuilder();
	}
	
	public StringAppender(int capacity) {
		appender = new StringBuilder(capacity);
	}
	
	public StringAppender(Object... s) {
		this();
		cat(s);
	}
	
	public StringAppender cat(Object s) {
		appender.append(s);
		return this;
	}
	
	public StringAppender cat(Object s1, Object s2) {
		// we skip an array creation by calling cat() multiple times
		return cat(s1).cat(s2);
	}
	
	public StringAppender cat(Object s1, Object s2, Object s3) {
		// we skip an array creation by calling cat() multiple times
		return cat(s1).cat(s2).cat(s3);
	}
	
	public StringAppender cat(Object... ss) {
		for (Object s : ss) {
			cat(s);
		}
		return this;
	}
	
	public StringAppender cat(Iterable ss) {
		for (Object s : ss) {
			cat(s);
		}
		return this;
	}
	
	public StringAppender catAt(int index, Object... ss) {
		// We make this method uses cat() to ensure that any override of cat() is also used by this method
		// otherwise we could simply use appender.insert(..) 
		// So, we swap this.appender with a new StringBuilder to make cat(..) fill it 
		StringBuilder previous = this.appender;
		StringBuilder target = new StringBuilder();
		this.appender = target;
		// cat() will append to the temporary StringBuilder
		cat(ss);
		this.appender = previous;
		// finally inserting at index
		this.appender.insert(index, target);
		return this;
	}
	
	public StringAppender catIf(boolean condition, Object... ss) {
		if (condition) {
			cat(ss);
		}
		return this;
	}
	
	public StringAppender ccat(Object... s) {
		return ccat(s, s[s.length - 1], s.length - 1);
	}
	
	public StringAppender ccat(Iterable ss, Object sep) {
		Iterator iterator = ss.iterator();
		while (iterator.hasNext()) {
			Object s = iterator.next();
			cat(s).catIf(iterator.hasNext(), sep);
		}
		return this;
	}
	
	public StringAppender ccat(Object[] s, Object sep) {
		return ccat(s, sep, s.length);
	}
	
	public StringAppender ccat(Object[] s, Object sep, int objectCount) {
		if (s.length > 0) {
			int lastIndex = objectCount < 1 ? 0 : objectCount - 1;
			for (int i = 0; i < objectCount; i++) {
				cat(s[i]).catIf(i != lastIndex, sep);
			}
		}
		return this;
	}
	
	public StringAppender wrap(Object open, Object close) {
		catAt(0, open).cat(close);
		return this;
	}
	
	@Override
	public String toString() {
		return appender.toString();
	}
	
	public StringAppender cutTail(int nbChar) {
		int newLength = length() - nbChar;
		if (newLength > -1) {
			appender.setLength(newLength);
		}
		return this;
	}
	
	public StringAppender cutHead(int nbChar) {
		appender.delete(0, nbChar);
		return this;
	}
	
	/**
	 * Gives access to delegate appender, because it can be usefull to append directly to the standard API StringBuilder
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

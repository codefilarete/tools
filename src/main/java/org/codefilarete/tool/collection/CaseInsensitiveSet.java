package org.codefilarete.tool.collection;

import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Set} that is case-insensitive.
 * Made to clarify some code.
 *
 * @author Guillaume Mary
 */
public class CaseInsensitiveSet extends TreeSet<String> {
	
	/**
	 * Default constructor. Call super one with {@link String#CASE_INSENSITIVE_ORDER} as the comparator.
	 */
	public CaseInsensitiveSet() {
		super(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Allows to initialize the instance with some elements
	 * @param elements some elements that fill the new instance
	 */
	public CaseInsensitiveSet(String... elements) {
		this(Arrays.asSet(elements));
	}
	
	public CaseInsensitiveSet(Set<String> elements) {
		super(String.CASE_INSENSITIVE_ORDER);
		addAll(elements);
	}
}
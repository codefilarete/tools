package org.codefilarete.tool.collection;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A simple {@link Map} that is case-insensitive.
 * Made to clarify some code.
 *
 * @author Guillaume Mary
 */
public class CaseInsensitiveMap<V> extends TreeMap<String, V> {
	
	/**
	 * Default constructor. Call super one with {@link String#CASE_INSENSITIVE_ORDER} as the comparator.
	 */
	public CaseInsensitiveMap() {
		super(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Allows to initialize the instance with some elements
	 * @param elements some elements that fill the new instance
	 */
	public CaseInsensitiveMap(Map<String, ? extends V> elements) {
		super(String.CASE_INSENSITIVE_ORDER);
		putAll(elements);
	}
}
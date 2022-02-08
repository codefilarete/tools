package org.codefilarete.tool.collection;

import java.util.HashSet;
import java.util.Set;

/**
 * A marking class for {@link Set} which elements cannot be added nor removed.
 *
 * @param <E> element type
 * @author Guillaume Mary
 */
public class ReadOnlySet<E> extends ReadOnlyCollection<E, Set<E>> implements Set<E> {
	
	public ReadOnlySet() {
		this(new HashSet<>());
	}
	
	public ReadOnlySet(Set<? extends E> set) {
		super((Set<E>) set);
	}
	
}
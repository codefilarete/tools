package org.codefilarete.tool.collection;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Sorter<E> {
	
	Iterable<E> sort(Iterable<? extends E> c);
}

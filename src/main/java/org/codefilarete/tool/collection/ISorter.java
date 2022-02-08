package org.codefilarete.tool.collection;

/**
 * @author Guillaume Mary
 */
public interface ISorter<C extends Iterable> {
	
	C sort(C c);
}

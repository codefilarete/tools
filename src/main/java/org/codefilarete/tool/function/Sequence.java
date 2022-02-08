package org.codefilarete.tool.function;

/**
 * 
 * @param <I> the type of returned values
 * 
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Sequence<I> {
	
	I next();
}
	

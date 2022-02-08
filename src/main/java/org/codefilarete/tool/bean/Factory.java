package org.codefilarete.tool.bean;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Factory<I, O> {

	O createInstance(I input);
}


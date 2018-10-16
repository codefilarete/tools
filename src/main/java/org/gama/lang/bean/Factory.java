package org.gama.lang.bean;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Factory<I, O> {

	O createInstance(I input);
}


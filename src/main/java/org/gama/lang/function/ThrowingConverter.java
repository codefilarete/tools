package org.gama.lang.function;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface ThrowingConverter<I, O, E extends Exception> {
	
	O convert(I input) throws E;
}

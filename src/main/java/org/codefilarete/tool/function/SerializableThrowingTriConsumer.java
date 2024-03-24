package org.codefilarete.tool.function;

import java.io.Serializable;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableThrowingTriConsumer<T, U, V, E extends Throwable> extends ThrowingTriConsumer<T, U, V, E>, Serializable {
	
}
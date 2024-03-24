package org.codefilarete.tool.function;

import java.io.Serializable;

/**
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface SerializableThrowingBiFunction<T, U, R, E extends Throwable> extends ThrowingBiFunction<T, U, R, E>, Serializable {
}

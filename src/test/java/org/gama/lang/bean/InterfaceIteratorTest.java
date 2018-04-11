package org.gama.lang.bean;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Function;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
public class InterfaceIteratorTest {
	
	public static Object[][] testNextMethodsData() {
		return new Object[][] {
				// test for no direct annotation
				{ Object.class, Arrays.asList() },
				// test for direct annotation
				{ String.class, Arrays.asList(Serializable.class, Comparable.class, CharSequence.class) },
				// test for far inherited annotation
				{ RuntimeException.class, Arrays.asList(Serializable.class) },
				{ ByteArrayInputStream.class, Arrays.asList(Closeable.class, AutoCloseable.class) },
				// test for many annotations by inheritance
				{ StringBuffer.class, Arrays.asList(Serializable.class, CharSequence.class, Appendable.class, CharSequence.class) },
				{ ArrayList.class, Arrays.asList(
						List.class, RandomAccess.class, Cloneable.class, Serializable.class,	// directly on ArrayList
						Collection.class, Iterable.class,	// from List of previous interface list
						List.class, Collection.class, Iterable.class,	// from AbstractList, parent of ArrayList, then List's interaces
						Collection.class, Iterable.class	// from AbstractCollection, parent of AbstractList
				) },
		};
	}
	
	@ParameterizedTest
	@MethodSource("testNextMethodsData")
	public void testNextMethods(Class clazz, List<Class> expectedInterfaces) {
		InterfaceIterator testInstance = new InterfaceIterator(clazz);
		assertEquals(expectedInterfaces, Iterables.collectToList(() -> testInstance, Function.identity()));
	}
}
package org.codefilarete.tool.bean;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Function;

import org.codefilarete.tool.collection.Arrays;
import org.codefilarete.tool.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
public class InterfaceIteratorTest {
	
	public static Object[][] testNextData() {
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
	@MethodSource("testNextData")
	public void testNext(Class clazz, List<Class> expectedInterfaces) {
		InterfaceIterator testInstance = new InterfaceIterator(clazz);
		assertThat(Iterables.collectToList(() -> testInstance, Function.identity())).isEqualTo(expectedInterfaces);
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		InterfaceIterator testInstance = new InterfaceIterator(RuntimeException.class);
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invokation
		testInstance = new InterfaceIterator(RuntimeException.class);
		testInstance.next();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// with no more element right from the beginning
		testInstance = new InterfaceIterator(Object.class);
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invokation
		testInstance = new InterfaceIterator(Object.class);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
}
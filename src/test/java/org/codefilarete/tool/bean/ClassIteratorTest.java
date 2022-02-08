package org.codefilarete.tool.bean;

import java.util.List;
import java.util.NoSuchElementException;

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
public class ClassIteratorTest {
	
	public static Object[][] testNextMethodsData() {
		return new Object[][] {
				{ X.class, Arrays.asList((Class) X.class) },
				{ Y.class, Arrays.asList((Class) Y.class, X.class) }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testNextMethodsData")
	public void testNext(Class clazz, List<Class> expectedClasses) {
		ClassIterator testInstance = new ClassIterator(clazz);
		assertThat(Iterables.copy(testInstance)).isEqualTo(expectedClasses);
	}
	
	@Test
	public void testNext_stopClass() {
		ClassIterator testInstance = new ClassIterator(Z.class, X.class);
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList((Class) Z.class, Y.class));
	}
	
	@Test
	public void testHasNext_false() {
		ClassIterator testInstance = new ClassIterator(X.class, X.class);
		assertThat(testInstance.hasNext()).isFalse();
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		ClassIterator testInstance = new ClassIterator(Object.class);
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invokation
		testInstance = new ClassIterator(Object.class);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
	static class X { }
	
	static class Y extends X { }
	
	static class Z extends Y { }
}
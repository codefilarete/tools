package org.codefilarete.tool.bean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
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
public class FieldIteratorTest {
	
	public static Object[][] testNextData() throws NoSuchFieldException {
		Field xf1 = X.class.getDeclaredField("f1");
		Field yf2 = Y.class.getDeclaredField("f2");
		Field zf2 = Z.class.getDeclaredField("f2");
		return new Object[][] {
				{ X.class, Arrays.asList(xf1) },
				{ Y.class, Arrays.asList(yf2, xf1) },
				{ Z.class, Arrays.asList(zf2, yf2, xf1) },
				{ NoField.class, Arrays.asList(xf1) }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testNextData")
	public void testNext(Class clazz, List<Field> expectedFields) {
		FieldIterator testInstance = new FieldIterator(clazz);
		assertThat(Iterables.collectToList(() -> testInstance, Function.identity())).isEqualTo(expectedFields);
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invocation
		FieldIterator testInstance = new FieldIterator(X.class);
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invocation
		testInstance = new FieldIterator(X.class);
		testInstance.next();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// with no more element right from the beginning
		testInstance = new FieldIterator(Object.class);
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invocation
		testInstance = new FieldIterator(Object.class);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
	static class X {
		private Object f1;
	}
	
	static class Y extends X {
		private Object f2;
	}
	
	static class Z extends Y {
		private Object f2;
	}
	
	static class NoField extends X {
	}
}
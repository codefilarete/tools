package org.gama.lang.bean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertEquals(expectedFields, Iterables.collectToList(() -> testInstance, Function.identity()));
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		FieldIterator testInstance = new FieldIterator(X.class);
		assertTrue(testInstance.hasNext());
		testInstance.next();
		assertFalse(testInstance.hasNext());
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// without hasNext() invokation
		testInstance = new FieldIterator(X.class);
		testInstance.next();
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// with no more element right from the beginning
		testInstance = new FieldIterator(Object.class);
		assertFalse(testInstance.hasNext());
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// without hasNext() invokation
		testInstance = new FieldIterator(Object.class);
		assertThrows(NoSuchElementException.class, testInstance::next);
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
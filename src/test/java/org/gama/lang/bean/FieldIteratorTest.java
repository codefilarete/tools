package org.gama.lang.bean;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
public class FieldIteratorTest {
	
	public static Object[][] testNextMethodsData() throws NoSuchFieldException {
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
	@MethodSource("testNextMethodsData")
	public void testNextMethods(Class clazz, List<Field> expectedFields) throws Exception {
		FieldIterator testInstance = new FieldIterator(clazz);
		assertEquals(expectedFields, Iterables.collectToList(() -> testInstance, Function.identity()));
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
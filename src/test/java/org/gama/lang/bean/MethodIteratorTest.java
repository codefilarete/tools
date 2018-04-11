package org.gama.lang.bean;

import java.lang.reflect.Method;
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
public class MethodIteratorTest {
	
	public static Object[][] testNextMethodsData() throws NoSuchMethodException {
		Method xm1 = X.class.getDeclaredMethod("m1");
		Method ym2 = Y.class.getDeclaredMethod("m2");
		Method zm2 = Z.class.getDeclaredMethod("m2");
		return new Object[][] {
				{ X.class, Arrays.asList(xm1) },
				{ Y.class, Arrays.asList(ym2, xm1) },
				{ Z.class, Arrays.asList(zm2, ym2, xm1) },
				{ NoMethod.class, Arrays.asList(xm1) }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testNextMethodsData")
	public void testNextMethods(Class clazz, List<Method> expectedMethods) {
		MethodIterator testInstance = new MethodIterator(clazz, Object.class);
		assertEquals(expectedMethods, Iterables.collectToList(() -> testInstance, Function.identity()));
	}
	
	static class X {
		private void m1() {
		}
	}
	
	static class Y extends X {
		private void m2() {
		}
	}
	
	static class Z extends Y {
		private void m2() {
		}
	}
	
	static class NoMethod extends X {
	}
}
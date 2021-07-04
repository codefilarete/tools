package org.gama.lang.bean;

import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
		assertThat(Iterables.collectToList(() -> testInstance, Function.identity())).isEqualTo(expectedMethods);
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		MethodIterator testInstance = new MethodIterator(X.class, Object.class);
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invokation
		testInstance = new MethodIterator(X.class, Object.class);
		testInstance.next();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// with no more element right from the beginning
		testInstance = new MethodIterator(Object.class, Object.class);
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invokation
		testInstance = new MethodIterator(Object.class, Object.class);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
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
package org.codefilarete.tool.bean;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.codefilarete.tool.collection.Arrays;
import org.codefilarete.tool.collection.Iterables;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
public class InstanceMethodIteratorTest {
	
	@Test
	public void testGetElements() throws Exception {
		InstanceMethodIterator testInstance = new InstanceMethodIterator(MethodsContainer.class, Object.class);
		Set<Method> expectedResult = new HashSet<>(Arrays.asList(
				MethodsContainer.class.getDeclaredMethod("method1"),
				MethodsContainer.class.getDeclaredMethod("method2"),
				MethodsContainer.class.getDeclaredMethod("method3"),
				MethodsContainer.class.getDeclaredMethod("method4")
		));
		// NB: we use HashSet for comparison because order is not guaranteed when traversing class members, so final order neither
		assertThat(new HashSet<>(Iterables.collectToList(() -> testInstance, Function.identity()))).isEqualTo(expectedResult);
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invocation
		InstanceMethodIterator testInstance = new InstanceMethodIterator(MethodsContainer.class, Object.class);
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invocation
		testInstance = new InstanceMethodIterator(MethodsContainer.class, Object.class);
		testInstance.next();
		testInstance.next();
		testInstance.next();
		testInstance.next();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// with no more element right from the beginning
		testInstance = new InstanceMethodIterator(Object.class, Object.class);
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invocation
		testInstance = new InstanceMethodIterator(Object.class, Object.class);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
	private static class MethodsContainer {
		private static Object staticMethod1() {
			return null;
		}
		
		protected static Object staticMethod2() {
			return null;
		}
		
		public static Object staticMethod3() {
			return null;
		}
		
		static Object staticMethod4() {
			return null;
		}
		
		private String method1() {
			return null;
		}
		
		protected String method2() {
			return null;
		}
		
		public String method3() {
			return null;
		}
		
		String method4() {
			return null;
		}
		
	}
	
}
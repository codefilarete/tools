package org.gama.lang.bean;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertEquals(expectedResult, new HashSet<>(Iterables.collectToList(() -> testInstance, Function.identity())));
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		InstanceMethodIterator testInstance = new InstanceMethodIterator(MethodsContainer.class, Object.class);
		assertTrue(testInstance.hasNext());
		testInstance.next();
		assertTrue(testInstance.hasNext());
		testInstance.next();
		assertTrue(testInstance.hasNext());
		testInstance.next();
		assertTrue(testInstance.hasNext());
		testInstance.next();
		assertFalse(testInstance.hasNext());
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// without hasNext() invokation
		testInstance = new InstanceMethodIterator(MethodsContainer.class, Object.class);
		testInstance.next();
		testInstance.next();
		testInstance.next();
		testInstance.next();
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// with no more element right from the beginning
		testInstance = new InstanceMethodIterator(Object.class, Object.class);
		assertFalse(testInstance.hasNext());
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// without hasNext() invokation
		testInstance = new InstanceMethodIterator(Object.class, Object.class);
		assertThrows(NoSuchElementException.class, testInstance::next);
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
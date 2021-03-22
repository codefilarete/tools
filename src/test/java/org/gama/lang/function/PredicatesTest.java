package org.gama.lang.function;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Guillaume Mary
 */
class PredicatesTest {
	
	@Test
	void not() {
		Predicate<Object> testInstance = Predicates.not(Objects::isNull);
		assertTrue(testInstance.test(""));
		assertFalse(testInstance.test(null));
	}
	
	@Test
	void predicate() {
		Set<String> data = Arrays.asHashSet("a", "b", "c");
		Predicate<Object> testInstance = Predicates.predicate(data::contains);
		assertTrue(testInstance.test("a"));
		assertFalse(testInstance.test("d"));
	}
	
	@Test
	void predicate_withMapper() {
		Set<String> data = Arrays.asHashSet("1", "2", "3");
		Predicate<Integer> testInstance = Predicates.predicate(Object::toString, data::contains);
		assertTrue(testInstance.test(1));
		assertFalse(testInstance.test(4));
	}
	
	@Test
	void toCompartor() {
		Comparator<Object> comparator = Predicates.toComparator(Objects::equals);
		assertEquals(comparator.compare("a", "b"), -1);
		assertEquals(comparator.compare("b", "a"), -1);
		assertEquals(comparator.compare("a", "a"), 0);
	}
	
	@Test
	void acceptAll() {
		assertTrue(Predicates.acceptAll().test(""));
		assertTrue(Predicates.acceptAll().test(null));
		assertTrue(Predicates.acceptAll().test("a"));
		assertTrue(Predicates.acceptAll().test(42));
	}
	
	@Test
	void rejectAll() {
		assertFalse(Predicates.rejectAll().test(""));
		assertFalse(Predicates.rejectAll().test(null));
		assertFalse(Predicates.rejectAll().test("a"));
		assertFalse(Predicates.rejectAll().test(42));
	}
	
	@Test
	void equalsWithNull() {
		assertTrue(Predicates.equalOrNull(null, null));
		assertTrue(Predicates.equalOrNull("a", "a"));
		assertFalse(Predicates.equalOrNull("a", "b"));
		assertFalse(Predicates.equalOrNull("b", "a"));
		assertFalse(Predicates.equalOrNull(null, "b"));
		assertFalse(Predicates.equalOrNull("a", null));
	}
	
	@Test
	void equalsWithNull_predicate() {
		BiPredicate predicateMock = mock(BiPredicate.class);
		when(predicateMock.test(any(), any())).thenReturn(true);
		
		assertTrue(Predicates.equalOrNull(null, null, predicateMock));
		verifyZeroInteractions(predicateMock);

		Predicates.equalOrNull("a", "a", predicateMock);
		verify(predicateMock, times(1)).test(eq("a"), eq("a"));

		Predicates.equalOrNull("a", "b", predicateMock);
		verify(predicateMock, times(1)).test(eq("a"), eq("b"));

		Predicates.equalOrNull("b", "a", predicateMock);
		verify(predicateMock, times(1)).test(eq("a"), eq("b"));
		
		clearInvocations(predicateMock);
		
		Predicates.equalOrNull(null, "b", predicateMock);
		verifyZeroInteractions(predicateMock);
		
		Predicates.equalOrNull("a", null, predicateMock);
		verifyZeroInteractions(predicateMock);
	}
	
	@Test
	void and_functions() {
		Function<Toto, String> function1 = Toto::getProp1;
		Function<Toto, Integer> function2 = Toto::getProp2;
		
		BiPredicate<Toto, Toto> testInstance = Predicates.and(function1, function2);
		assertTrue(testInstance.test(new Toto("a", 1), new Toto("a", 1)));
		assertFalse(testInstance.test(new Toto("a", 1), new Toto("a", 2)));
		assertFalse(testInstance.test(new Toto("a", 1), new Toto("b", 1)));
		// test with null
		assertTrue(testInstance.test(new Toto(null, 1), new Toto(null, 1)));
		assertTrue(testInstance.test(new Toto("a", null), new Toto("a", null)));
	}
	
	private class Toto {
		
		private final String prop1;
		private final Integer prop2;
		
		private Toto(String prop1, Integer prop2) {
			this.prop1 = prop1;
			this.prop2 = prop2;
		}
		
		public String getProp1() {
			return prop1;
		}
		
		public Integer getProp2() {
			return prop2;
		}
	}
}
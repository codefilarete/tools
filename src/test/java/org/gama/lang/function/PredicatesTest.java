package org.gama.lang.function;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
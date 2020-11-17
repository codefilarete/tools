package org.gama.lang.collection;

import java.util.List;

import org.gama.lang.function.Predicates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Mary
 */
class FilteringIteratorTest {
	
	@Test
	void filter() {
		FilteringIterator<String> testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("b"::equals));
		List<String> copy = Iterables.copy(testInstance);
		assertEquals(Arrays.asList("a", null, "c"), copy);
	}
	
	@Test
	void nextCanBeUsedWithoutHasNext() {
		FilteringIterator<String> testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("b"::equals));
		assertEquals("a", testInstance.next());
		
		// and filter works at very first step
		new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("a"::equals));
		assertEquals(null, testInstance.next());
	}
	
	@Test
	void remove() {
		FilteringIterator<String> testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("b"::equals));
		testInstance.hasNext();
		testInstance.next();
		testInstance.hasNext();
		testInstance.next();
		testInstance.remove();
		List<String> copy = Iterables.copy(testInstance);
		// "a" was consumed, and null was removed
		assertEquals(Arrays.asList("c"), copy);
	}
}
package org.gama.lang.collection;

import java.util.List;
import java.util.Objects;

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
		
		// and works with null
		testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Objects::nonNull);
		copy = Iterables.copy(testInstance);
		assertEquals(Arrays.asList("a", "b", "c", "b"), copy);
	}
	
	@Test
	void filterWorksAtVeryFirstStep() {
		FilteringIterator<String> testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("b"::equals));
		testInstance.hasNext();
		assertEquals("a", testInstance.next());
		
		// and works with null
		testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Objects::nonNull);
		testInstance.hasNext();
		assertEquals("a", testInstance.next());
		testInstance.hasNext();
		assertEquals("b", testInstance.next());
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
package org.gama.lang.collection;

import java.util.List;
import java.util.Objects;

import org.gama.lang.function.Predicates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class FilteringIteratorTest {
	
	@Test
	void filter() {
		FilteringIterator<String> testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("b"::equals));
		List<String> copy = Iterables.copy(testInstance);
		assertThat(copy).isEqualTo(Arrays.asList("a", null, "c"));
		
		// and works with null
		testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Objects::nonNull);
		copy = Iterables.copy(testInstance);
		assertThat(copy).isEqualTo(Arrays.asList("a", "b", "c", "b"));
	}
	
	@Test
	void filterWorksAtVeryFirstStep() {
		FilteringIterator<String> testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Predicates.not("b"::equals));
		testInstance.hasNext();
		assertThat(testInstance.next()).isEqualTo("a");
		
		// and works with null
		testInstance = new FilteringIterator<>(Arrays.asList("a", null, "b", "c", "b").iterator(), Objects::nonNull);
		testInstance.hasNext();
		assertThat(testInstance.next()).isEqualTo("a");
		testInstance.hasNext();
		assertThat(testInstance.next()).isEqualTo("b");
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
		assertThat(copy).isEqualTo(Arrays.asList("c"));
	}
}
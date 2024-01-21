package org.codefilarete.tool.collection;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
class IteratorIteratorTest {
	
	static Object[][] test_data() {
		return new Object[][] {
				new Object[] { Arrays.asList(Arrays.asList("a"), Arrays.asList("b")), Arrays.asList("a", "b")},
				new Object[] { Arrays.asList(Arrays.asList("a"), Arrays.asList("b", "c")), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c")), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList("a", "b"), Arrays.asList(), Arrays.asList("c")), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList()), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList(), Arrays.asList("a", "b"), Arrays.asList("c")), Arrays.asList("a", "b", "c")},
		};
	}
	
	@ParameterizedTest
	@MethodSource("test_data")
	void copy(Collection<Iterable<String>> input, List<String> expectedResult) {
		IteratorIterator<String> testInstance = new IteratorIterator<>(input.iterator());
		assertThat(Iterables.copy(testInstance)).isEqualTo(expectedResult);
	}
	
	@Test
	void constructor() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("a", "b", "c", "d"));
	}
	
	@Test
	void noSuchElementException() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList());
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
	@Test
	void remove() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertThat(testInstance.next()).isEqualTo("a");
		testInstance.remove();
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("b", "c", "d"));
		
		testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertThat(testInstance.next()).isEqualTo("a");
		assertThat(testInstance.next()).isEqualTo("b");
		assertThat(testInstance.next()).isEqualTo("c");
		testInstance.remove();
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("d"));
	}
}
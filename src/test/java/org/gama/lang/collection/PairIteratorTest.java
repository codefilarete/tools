package org.gama.lang.collection;


import java.util.List;
import java.util.NoSuchElementException;

import org.gama.lang.Duo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
public class PairIteratorTest {
	
	@Test
	public void testHasNext() {
		PairIterator<Integer, String> testInstance = new PairIterator<>(Arrays.asList(1,2,3), Arrays.asList("a", "b"));
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(new Duo<>(1, "a")).isEqualTo(testInstance.next());
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(new Duo<>(2, "b")).isEqualTo(testInstance.next());
		assertThat(testInstance.hasNext()).isFalse();
	}
	
	public static Object[][] testNext_NoSuchElementExceptionData() {
		PairIterator<Integer, String> startedIterator = new PairIterator<>(Arrays.asList(1), Arrays.asList("a"));
		startedIterator.next();
		return new Object[][] {
				{ startedIterator },
				{ new PairIterator<>(Arrays.asList(1), Arrays.asList()) },
				{ new PairIterator<>(Arrays.asList(), Arrays.asList("a")) },
				{ new PairIterator<>(Arrays.asList(), Arrays.asList()) },
		};
	}
	
	@ParameterizedTest
	@MethodSource("testNext_NoSuchElementExceptionData")
	public void testNext_NoSuchElementException(PairIterator<Integer, String> testInstance) {
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
	@Test
	public void testRemove() {
		List<Integer> integers = Arrays.asList(1, 2, 3);
		List<String> strings = Arrays.asList("a", "b");
		PairIterator<Integer, String> testInstance = new PairIterator<>(integers, strings);
		testInstance.hasNext();
		testInstance.next();
		testInstance.remove();
		assertThat(Arrays.asList(2, 3)).isEqualTo(integers);
		assertThat(Arrays.asList("b")).isEqualTo(strings);
	}
	
	@Test
	public void testInfiniteIterator() {
		List<Integer> integers = Arrays.asList(1, 2, 3);
		List<String> strings = Arrays.asList("a");
		PairIterator<Integer, String> testInstance = new PairIterator<>(integers.iterator(), new PairIterator.InfiniteIterator<>(strings.iterator()));
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(new Duo<>(1, "a")).isEqualTo(testInstance.next());
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(new Duo<>(2, null)).isEqualTo(testInstance.next());
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(new Duo<>(3, null)).isEqualTo(testInstance.next());
		assertThat(testInstance.hasNext()).isFalse();
	}
}
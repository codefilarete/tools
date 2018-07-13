package org.gama.lang.collection;


import java.util.List;
import java.util.NoSuchElementException;

import org.gama.lang.Duo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
public class PairIteratorTest {
	
	@Test
	public void testHasNext() {
		PairIterator<Integer, String> testInstance = new PairIterator<>(Arrays.asList(1,2,3), Arrays.asList("a", "b"));
		assertTrue(testInstance.hasNext());
		assertEquals(testInstance.next(), new Duo<>(1, "a"));
		assertTrue(testInstance.hasNext());
		assertEquals(testInstance.next(), new Duo<>(2, "b"));
		assertFalse(testInstance.hasNext());
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
		assertThrows(NoSuchElementException.class, testInstance::next);
	}
	
	@Test
	public void testRemove() {
		List<Integer> integers = Arrays.asList(1, 2, 3);
		List<String> strings = Arrays.asList("a", "b");
		PairIterator<Integer, String> testInstance = new PairIterator<>(integers, strings);
		testInstance.hasNext();
		testInstance.next();
		testInstance.remove();
		assertEquals(integers, Arrays.asList(2, 3));
		assertEquals(strings, Arrays.asList("b"));
	}
	
	@Test
	public void testInfiniteIterator() {
		List<Integer> integers = Arrays.asList(1, 2, 3);
		List<String> strings = Arrays.asList("a");
		PairIterator<Integer, String> testInstance = new PairIterator<>(integers.iterator(), new PairIterator.InfiniteIterator<>(strings.iterator()));
		assertTrue(testInstance.hasNext());
		assertEquals(testInstance.next(), new Duo<>(1, "a"));
		assertTrue(testInstance.hasNext());
		assertEquals(testInstance.next(), new Duo<>(2, null));
		assertTrue(testInstance.hasNext());
		assertEquals(testInstance.next(), new Duo<>(3, null));
		assertFalse(testInstance.hasNext());
	}
}
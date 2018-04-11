package org.gama.lang.collection;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Guillaume Mary
 */
public class IteratorIteratorTest {
	
	public static Object[][] test_data() {
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
	public void test(Collection<Iterable<String>> input, List<String> expectedResult) {
		IteratorIterator<String> testInstance = new IteratorIterator<>(input.iterator());
		assertEquals(expectedResult, Iterables.copy(testInstance));
	}
	
	@Test
	public void testContructor() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertEquals(Arrays.asList("a", "b", "c", "d"), Iterables.copy(testInstance));
	}
	
	@Test
	public void testNoSuchElementException() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList());
		assertThrows(NoSuchElementException.class, testInstance::next);
	}
	
	@Test
	public void testRemove() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertEquals("a", testInstance.next());
		testInstance.remove();
		assertEquals(Arrays.asList("b", "c", "d"), Iterables.copy(testInstance));
		
		testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertEquals("a", testInstance.next());
		assertEquals("b", testInstance.next());
		assertEquals("c", testInstance.next());
		testInstance.remove();
		assertEquals(Arrays.asList("d"), Iterables.copy(testInstance));
	}
}
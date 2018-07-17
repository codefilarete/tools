package org.gama.lang.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Mary
 */
public class ReadOnlyIteratorTest {
	
	@Test
	public void testRemove_throwsException() {
		ReadOnlyIterator<String> testInstance = ReadOnlyIterator.wrap(Arrays.asList("a", "b"));
		assertThrows(UnsupportedOperationException.class, testInstance::remove);
	}
	
	@Test
	public void testIteration() {
		ReadOnlyIterator<String> testInstance = ReadOnlyIterator.wrap(Arrays.asList("a", "b"));
		assertTrue(testInstance.hasNext());
		assertEquals("a", testInstance.next());
		assertTrue(testInstance.hasNext());
		assertEquals("b", testInstance.next());
	}
	
}
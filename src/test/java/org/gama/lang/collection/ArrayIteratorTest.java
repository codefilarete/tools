package org.gama.lang.collection;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
class ArrayIteratorTest {
	
	@Test
	public void testNext() {
		ArrayIterator<String> testInstance = new ArrayIterator<>("a", "b", "c");
		assertEquals(Arrays.asList("a", "b", "c"), Iterables.copy(testInstance));
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		ArrayIterator<String> testInstance = new ArrayIterator<>("a");
		assertTrue(testInstance.hasNext());
		testInstance.next();
		assertFalse(testInstance.hasNext());
		assertThrows(NoSuchElementException.class, testInstance::next);
		
		// without hasNext() invokation
		testInstance = new ArrayIterator<>("a");
		testInstance.next();
		assertThrows(NoSuchElementException.class, testInstance::next);
	}
}
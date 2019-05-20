package org.gama.lang.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.gama.lang.exception.Exceptions.ExceptionCauseIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
class ExceptionsTest {
	
	@Nested
	class ExceptionCauseIteratorTest {
		
		@Test
		public void testNext() {
			RuntimeException root = new RuntimeException("root", 
					new RuntimeException("cause", 
					new RuntimeException("second cause")));
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(root);
			List<String> stackMessages = Iterables.collect(() -> testInstance, Throwable::getMessage, ArrayList::new);
			assertEquals(Arrays.asList("root", "cause", "second cause"), stackMessages);
		}
		
		@Test
		public void testNext_throwsNoSuchElementException() {
			RuntimeException root = new RuntimeException("root", new RuntimeException("cause"));
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(root);
			assertTrue(testInstance.hasNext());
			testInstance.next();
			assertTrue(testInstance.hasNext());
			testInstance.next();
			assertFalse(testInstance.hasNext());
			assertThrows(NoSuchElementException.class, testInstance::next);
		}
		
		@Test
		public void testNext_throwsNoSuchElementException_onNullException() {
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(null);
			assertFalse(testInstance.hasNext());
			assertThrows(NoSuchElementException.class, testInstance::next);
		}
	}
	
}
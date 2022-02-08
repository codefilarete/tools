package org.codefilarete.tool.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.codefilarete.tool.collection.Arrays;
import org.codefilarete.tool.collection.Iterables;
import org.codefilarete.tool.exception.Exceptions.ExceptionCauseIterator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
			assertThat(stackMessages).isEqualTo(Arrays.asList("root", "cause", "second cause"));
		}
		
		@Test
		public void testNext_throwsNoSuchElementException() {
			RuntimeException root = new RuntimeException("root", new RuntimeException("cause"));
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(root);
			assertThat(testInstance.hasNext()).isTrue();
			testInstance.next();
			assertThat(testInstance.hasNext()).isTrue();
			testInstance.next();
			assertThat(testInstance.hasNext()).isFalse();
			assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		}
		
		@Test
		public void testNext_throwsNoSuchElementException_onNullException() {
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(null);
			assertThat(testInstance.hasNext()).isFalse();
			assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		}
	}
	
}
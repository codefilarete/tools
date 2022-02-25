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
	
	@Test
	void findExceptionInCauses_string() {
		NullPointerException cause = new NullPointerException("This exception contains toto in its message");
		NullPointerException foundException = Exceptions.findExceptionInCauses(new RuntimeException(cause), NullPointerException.class,
																			   // testing ignore case sensitivity
																			   "THIS exception contains TOTO in its message");
		assertThat(foundException).isSameAs(cause);
	}
	
	@Test
	void findExceptionInCauses_predicate() {
		NullPointerException cause = new NullPointerException("This exception contains toto in its message");
		NullPointerException foundException = Exceptions.findExceptionInCauses(new RuntimeException(cause), NullPointerException.class,
																	 m -> m.contains("toto"));
		assertThat(foundException).isSameAs(cause);
	}
	
	@Nested
	class ExceptionCauseIteratorTest {
		
		@Test
		void next() {
			RuntimeException root = new RuntimeException("root", 
					new RuntimeException("cause", 
					new RuntimeException("second cause")));
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(root);
			List<String> stackMessages = Iterables.collect(() -> testInstance, Throwable::getMessage, ArrayList::new);
			assertThat(stackMessages).isEqualTo(Arrays.asList("root", "cause", "second cause"));
		}
		
		@Test
		void next_throwsNoSuchElementException() {
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
		void next_throwsNoSuchElementException_onNullException() {
			ExceptionCauseIterator testInstance = new ExceptionCauseIterator(null);
			assertThat(testInstance.hasNext()).isFalse();
			assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		}
	}
	
}
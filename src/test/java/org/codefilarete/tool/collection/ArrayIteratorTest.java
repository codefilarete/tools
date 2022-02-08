package org.codefilarete.tool.collection;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
class ArrayIteratorTest {
	
	@Test
	public void testNext() {
		ArrayIterator<String> testInstance = new ArrayIterator<>("a", "b", "c");
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("a", "b", "c"));
	}
	
	@Test
	public void testNext_throwsNoSuchElementException() {
		// with intermediary hasNext() invokation
		ArrayIterator<String> testInstance = new ArrayIterator<>("a");
		assertThat(testInstance.hasNext()).isTrue();
		testInstance.next();
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
		
		// without hasNext() invokation
		testInstance = new ArrayIterator<>("a");
		testInstance.next();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
}
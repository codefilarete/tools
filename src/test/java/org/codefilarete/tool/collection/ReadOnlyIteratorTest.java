package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
public class ReadOnlyIteratorTest {
	
	@Test
	public void testRemove_throwsException() {
		ReadOnlyIterator<String> testInstance = ReadOnlyIterator.wrap(Arrays.asList("a", "b"));
		assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(testInstance::remove);
	}
	
	@Test
	public void testIteration() {
		ReadOnlyIterator<String> testInstance = ReadOnlyIterator.wrap(Arrays.asList("a", "b"));
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(testInstance.next()).isEqualTo("a");
		assertThat(testInstance.hasNext()).isTrue();
		assertThat(testInstance.next()).isEqualTo("b");
	}
	
}
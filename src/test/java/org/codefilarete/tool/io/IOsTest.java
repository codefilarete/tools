package org.codefilarete.tool.io;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.codefilarete.tool.io.IOs.InputStreamIterator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
public class IOsTest {
	
	@Test
	public void testByteIterator() {
		List<Byte> result = new ArrayList<>();
		Iterable<byte[]> iterable = InputStreamIterator.iterable(new ByteArrayInputStream(new byte[] { 0, 1, 2, 3, 4 }), 2);
		for (byte[] bytes : iterable) {
			for (Byte aByte : bytes) {
				result.add(aByte);
			}
		}
		assertThat(result.toArray(new Byte[5])).isEqualTo(new Byte[] { 0, 1, 2, 3, 4 });
	}
	
	@Test
	public void testByteIterator_noSuchElementException() {
		InputStreamIterator testInstance = new InputStreamIterator(new ByteArrayInputStream(new byte[] {}), 2);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
}
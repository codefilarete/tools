package org.gama.lang;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Guillaume Mary
 */
public class ThreadLocalsTest {
	
	@Test
	public void testFoWithThreadLocal() {
		ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();
		String result = ThreadLocals.doWithThreadLocal(targetThreadLocal, () -> "Hello ", (Supplier<String>) () -> targetThreadLocal.get() + "world !");
		assertEquals("Hello world !", result);
		assertNull(targetThreadLocal.get());
	}
}
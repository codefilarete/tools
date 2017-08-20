package org.gama.lang;

import java.util.function.Supplier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
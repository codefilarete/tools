package org.gama.lang;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Guillaume Mary
 */
public class ThreadLocalsTest {
	
	@Test
	void withThreadLocal_function_noValuePresent() {
		ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();
		String result = ThreadLocals.doWithThreadLocal(targetThreadLocal, () -> "Hello ", (Function<String, String>) s -> s + "world !");
		assertEquals("Hello world !", result);
		assertNull(targetThreadLocal.get());
	}
	
	@Test
	void withThreadLocal_function_valueAlreadyPresent() {
		ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();
		targetThreadLocal.set("Hi ");
		String result = ThreadLocals.doWithThreadLocal(targetThreadLocal, () -> "Hello ", (Function<String, String>) s -> s + "world !");
		assertEquals("Hi world !", result);
		assertNull(targetThreadLocal.get());
	}
}
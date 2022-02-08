package org.codefilarete.tool;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class ThreadLocalsTest {
	
	@Test
	void withThreadLocal_function_noValuePresent() {
		ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();
		String result = ThreadLocals.doWithThreadLocal(targetThreadLocal, () -> "Hello ", (Function<String, String>) s -> s + "world !");
		assertThat(result).isEqualTo("Hello world !");
		assertThat(targetThreadLocal.get()).isNull();
	}
	
	@Test
	void withThreadLocal_function_valueAlreadyPresent() {
		ThreadLocal<String> targetThreadLocal = new ThreadLocal<>();
		targetThreadLocal.set("Hi ");
		String result = ThreadLocals.doWithThreadLocal(targetThreadLocal, () -> "Hello ", (Function<String, String>) s -> s + "world !");
		assertThat(result).isEqualTo("Hi world !");
		assertThat(targetThreadLocal.get()).isNull();
	}
}
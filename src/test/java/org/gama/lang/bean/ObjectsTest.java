package org.gama.lang.bean;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Guillaume Mary
 */
class ObjectsTest {
	
	@Test
	void not() {
		assertFalse(Arrays.asList("a", "a", "a").stream().allMatch(Objects.not("a"::equals)));
	}
	
	@Test
	void decode() {
		assertEquals("B", Objects.fallback("A", "A", "B"));
		assertEquals("C", Objects.fallback("C", "A", "B"));
		assertEquals("B", Objects.fallback(null, null, "B"));
		assertEquals("A", Objects.fallback("A", null, "B"));
		assertEquals(null, Objects.fallback(null, "A", "B"));
	}
}
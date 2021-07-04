package org.gama.lang.bean;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	
	@Test
	void equals() {
		assertTrue(Objects.equals("a", "a"));
		assertFalse(Objects.equals("a", "b"));
		assertTrue(Objects.equals(new String[] { "a" }, new String[] { "a" }));
		assertFalse(Objects.equals(new String[] { "a" }, new String[] { "b" }));
		assertTrue(Objects.equals(new String[][] { new String[] { "a" }, new String[] { "b" } }, new String[][] { new String[] { "a" }, new String[] { "b" } }));
		assertFalse(Objects.equals(new String[][] { new String[] { "a" }, new String[] { "b" } }, new String[][] { new String[] { "a" }, new String[] { "c" } }));
	}
	
	@Test
	void hashcode() {
		assertEquals(Objects.hashCode("a"), Objects.hashCode("a"));
		assertNotEquals(Objects.hashCode("a"), Objects.hashCode("b"));
		
		assertEquals(Objects.hashCode("a", "b"), Objects.hashCode("a", "b"));
		assertNotEquals(Objects.hashCode("a", "b"), Objects.hashCode("b", "b"));
		assertEquals(Objects.hashCode("a", null), Objects.hashCode("a", null));
		assertNotEquals(Objects.hashCode("a", null), Objects.hashCode("b", "b"));
		assertEquals(Objects.hashCode(null, "a"), Objects.hashCode(null, "a"));
		assertNotEquals(Objects.hashCode(null, "a"), Objects.hashCode("b", "b"));
		// checking with arrays
		assertEquals(Objects.hashCode(new String[] { "a" }), Objects.hashCode(new String[] { "a" }));
		assertNotEquals(Objects.hashCode(new String[] { "a" }), Objects.hashCode(new String[] { "b" }));
		// checking with arrays of arrays
		assertEquals(Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "b" } }), Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "b" } }));
		assertNotEquals(Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "b" } }), Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "c" } }));
		
		// not a strong belief, just to explicit null cases
		assertEquals(0, Objects.hashCode(null));
		assertEquals(0, Objects.hashCode(null, null));
		assertEquals(0, Objects.hashCode(new String[] { null }));
		assertEquals(992, Objects.hashCode(new String[] { null }, new String[] { null }));
	}
}
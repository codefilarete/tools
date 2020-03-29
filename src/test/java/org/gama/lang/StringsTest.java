package org.gama.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author Guillaume Mary
 */
public class StringsTest {
	
	@Test
	public void testIsEmpty() {
		assertFalse(Strings.isEmpty("a"));
		assertTrue(Strings.isEmpty(""));
		assertTrue(Strings.isEmpty(null));
	}
	
	@Test
	public void testCapitalize() {
		assertEquals("Bonjour", Strings.capitalize("bonjour"));
		assertEquals("BONJOUR", Strings.capitalize("BONJOUR"));
		assertEquals("", Strings.capitalize(""));
		assertEquals(null, Strings.capitalize(null));
	}
	
	@Test
	public void testHead() {
		assertEquals("sn", Strings.head("snake", 2));
		assertEquals("sna", Strings.head("snake", 3));
		assertEquals("snake", Strings.head("snake", 42));
		assertEquals(null, Strings.head(null, 2));
	}
	
	@Test
	public void testHead_stringBoundary() {
		assertEquals("sn", Strings.head("snake", "a"));
		assertEquals("sna", Strings.head("snake", "k"));
		assertEquals("", Strings.head("snake", "w"));
		assertEquals(null, Strings.head(null, "z"));
	}
	
	@Test
	public void testCutHead() {
		assertEquals("ake", Strings.cutHead("snake", 2));
		assertEquals("ke", Strings.cutHead("snake", 3));
		assertEquals("", Strings.cutHead("snake", 42));
		assertEquals(null, Strings.cutHead(null, 2));
	}
	
	@Test
	public void testTail() {
		assertEquals("ke", Strings.tail("snake", 2));
		assertEquals("ake", Strings.tail("snake", 3));
		assertEquals("snake", Strings.tail("snake", 42));
		assertEquals(null, Strings.tail(null, 2));
	}
	
	@Test
	public void testCutTail() {
		assertEquals("sna", Strings.cutTail("snake", 2));
		assertEquals("sn", Strings.cutTail("snake", 3));
		assertEquals("", Strings.cutTail("snake", 42));
		assertEquals(null, Strings.cutTail(null, 2));
	}
	
	@Test
	public void ellipsis() {
		assertEquals("a too long...", Strings.ellipsis("a too long sentense", 10));
		assertEquals("0123456789", Strings.ellipsis("0123456789", 10));
		assertEquals("a word", Strings.ellipsis("a word", 10));
		assertEquals(null, Strings.ellipsis(null, 10));
	}
	
	@Test
	public void testRepeat() {
		String s10 = "aaaaaaaaaa";
		String s5 = "bbbbb";
		String s = "c";
		// we know if the optimization is good as we give pre-concatenated Strings different from each other
		assertEquals("ccc", Strings.repeat(3, s).toString());
		assertEquals("cccccccc", Strings.repeat(8, s).toString());
		assertEquals("ccc", Strings.repeat(3, s, s10, s5).toString());
		assertEquals("bbbbb", Strings.repeat(5, s, s10, s5).toString());
		assertEquals("bbbbbccc", Strings.repeat(8, s, s10, s5).toString());
		assertEquals("aaaaaaaaaacc", Strings.repeat(12, s, s10, s5).toString());
		assertEquals("aaaaaaaaaabbbbbcc", Strings.repeat(17, s, s10, s5).toString());
		assertEquals("aaaaaaaaaaaaaaaaaaaabbbbbcc", Strings.repeat(27, s, s10, s5).toString());
		// inverting order of pre-concatenated Strings may work also but lessly optimized
		assertEquals("bbbbbbbbbbbbbbbbbbbbbbbbbcc", Strings.repeat(27, s, s5, s10).toString());
	}
	
	@Test
	void foorPrint() {
		assertEquals("42", Strings.footPrint(new FootPrintCandidate().setValue1(42), FootPrintCandidate::getValue1));
		assertEquals("42, a", Strings.footPrint(new FootPrintCandidate().setValue1(42).setValue2("a"), FootPrintCandidate::getValue1, FootPrintCandidate::getValue2));
	}
	
	private class FootPrintCandidate {
		
		private int value1;
		private String value2;
		
		private int getValue1() {
			return value1;
		}
		
		private FootPrintCandidate setValue1(int value1) {
			this.value1 = value1;
			return this;
		}
		
		private String getValue2() {
			return value2;
		}
		
		private FootPrintCandidate setValue2(String value2) {
			this.value2 = value2;
			return this;
		}
	}
}
package org.gama.lang;

import org.gama.lang.bean.Randomizer;
import org.gama.lang.collection.Arrays;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Guillaume Mary
 */
public class StringAppenderTest {
	
	@Test
	public void testConstructor_array() {
		// test all kind of cat signature
		StringAppender testInstance = new StringAppender("a", "b", "c", "d");
		assertEquals("abcd", testInstance.toString());
	}
	
	@Test
	public void testCat() {
		// test all kind of cat signature
		StringAppender testInstance = new StringAppender();
		testInstance
				.cat("a")
				.cat("b")
				.cat("c", "d")
				.cat("e", "f", "g")
				.cat("h", "i", "j", "k", "l")
				.cat(Arrays.asList("m", "n", "o", "p"));
		assertEquals("abcdefghijklmnop", testInstance.toString());
	}
	
	@Test
	public void testCatIf() {
		StringAppender testInstance = new StringAppender();
		testInstance
				.catIf(true, "a")
				.catIf(false, "b")
				.catIf(true, "c", "d")
				.catIf(false, "e", "f", "g")
				.catIf(true, "h", "i", "j", "k", "l");
		assertEquals("acdhijkl", testInstance.toString());
	}
	
	@Test
	public void testCat_overriden() {
		// every Number appended to the StringAppender will be replaced by "999" (stupid !)
		StringAppender testInstance = new StringAppender() {
			@Override
			public StringAppender cat(Object o) {
				return o instanceof Number ? cat("999") : super.cat(o);
			}
		};
		
		// we gonna test some usual methods
		// cat
		testInstance.cat(Randomizer.INSTANCE.drawInt(), " expresso please !");
		assertEquals("999 expresso please !", testInstance.toString());
		
		testInstance.cat(" And ", Randomizer.INSTANCE.drawInt(), " chocolates !");
		assertEquals("999 expresso please ! And 999 chocolates !", testInstance.toString());
		
		testInstance.catIf(true, " ", Randomizer.INSTANCE.drawInt(), " teas !");
		assertEquals("999 expresso please ! And 999 chocolates ! 999 teas !", testInstance.toString());
		
		// catAt
		testInstance.catAt(0, Randomizer.INSTANCE.drawInt());
		assertEquals("999999 expresso please ! And 999 chocolates ! 999 teas !", testInstance.toString());
		
		// wrap
		testInstance.wrap(Randomizer.INSTANCE.drawInt(), Randomizer.INSTANCE.drawInt());
		assertEquals("999999999 expresso please ! And 999 chocolates ! 999 teas !999", testInstance.toString());
	}
	
	@Test
	public void testWrap() {
		// simple use case
		StringAppender testInstance = new StringAppender();
		testInstance.cat("a").wrap("#", "$");
		assertEquals("#a$", testInstance.toString());
		
		// test on empty appender
		testInstance = new StringAppender();
		testInstance.wrap("#", "$");
		assertEquals("#$", testInstance.toString());
	}
	
	@Test
	public void testCcat() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat("a", 1, "b", 2, ",");
		assertEquals("a,1,b,2", testInstance.toString());
	}
	
	@Test
	public void testCcat_iterable() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat(Arrays.asList("a", 1, "b", 2), ",");
		assertEquals("a,1,b,2", testInstance.toString());
	}
	
	@Test
	public void testCcat_array() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat(new Object[] { "a", 1, "b", 2, "c", 3, "d", 4 }, ",");
		assertEquals("a,1,b,2,c,3,d,4", testInstance.toString());
	}
	
	@Test
	public void testCcat_array_bounded() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat(new Object[] { "a", 1, "b", 2, "c", 3, "d", 4 }, ",", 2);
		assertEquals("a,1", testInstance.toString());
		
		// clear for another test
		testInstance.getAppender().setLength(0);
		testInstance.ccat(new Object[] { "a", 1, "b", 2, "c", 3, "d", 4 }, ",", 5);
		assertEquals("a,1,b,2,c", testInstance.toString());
	}
	
	@Test
	public void testCutTail() {
		StringAppender testInstance = new StringAppender();
		testInstance.cat("snake tail").cutTail(5);
		assertEquals("snake", testInstance.toString());
	}
	
	@Test
	public void testCutHead() {
		StringAppender testInstance = new StringAppender();
		testInstance.cat("headache").cutHead(4);
		assertEquals("ache", testInstance.toString());
	}
	
	@Test
	public void testCutAndInsert() {
		StringAppender testInstance = new StringAppender();
		testInstance.cat("headache").cutHead(4).catAt(0, "sstom").cutHead(1);
		assertEquals("stomache", testInstance.toString());
	}
}
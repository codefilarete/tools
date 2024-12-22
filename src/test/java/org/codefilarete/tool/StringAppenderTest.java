package org.codefilarete.tool;

import org.codefilarete.tool.bean.Randomizer;
import org.codefilarete.tool.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class StringAppenderTest {
	
	@Test
	void constructor_array() {
		StringAppender testInstance = new StringAppender("a", "b", "c", "d");
		assertThat(testInstance.toString()).isEqualTo("abcd");
	}
	
	@Test
	void constructor_StringBuilder() {
		StringAppender testInstance = new StringAppender(new StringBuilder("Hello"));
		testInstance.cat(" World !");
		assertThat(testInstance.toString()).isEqualTo("Hello World !");
	}
	
	@Test
	void cat() {
		// test all kind of cat signature
		StringAppender testInstance = new StringAppender();
		testInstance
				.cat("a")
				.cat("b")
				.cat("c", "d")
				.cat("e", "f", "g")
				.cat("h", "i", "j", "k", "l")
				.cat(Arrays.asList("m", "n", "o", "p"));
		assertThat(testInstance.toString()).isEqualTo("abcdefghijklmnop");
	}
	
	@Test
	void catIf() {
		StringAppender testInstance = new StringAppender();
		testInstance
				.catIf(true, "a")
				.catIf(false, "b")
				.catIf(true, "c", "d")
				.catIf(false, "e", "f", "g")
				.catIf(true, "h", "i", "j", "k", "l");
		assertThat(testInstance.toString()).isEqualTo("acdhijkl");
	}
	
	@Test
	void cat_overridden() {
		// every Number appended to the StringAppender will be replaced by "999" (stupid !)
		StringAppender testInstance = new StringAppender() {
			@Override
			public StringAppender cat(Object o) {
				return o instanceof Number ? super.cat("999") : super.cat(o);
			}
		};
		
		// we gonna test some usual methods
		// cat
		testInstance.cat(Randomizer.INSTANCE.drawInt(), " expresso please !");
		assertThat(testInstance.toString()).isEqualTo("999 expresso please !");
		
		testInstance.cat(" And ", Randomizer.INSTANCE.drawInt(), " chocolates !");
		assertThat(testInstance.toString()).isEqualTo("999 expresso please ! And 999 chocolates !");
		
		testInstance.catIf(true, " ", Randomizer.INSTANCE.drawInt(), " teas !");
		assertThat(testInstance.toString()).isEqualTo("999 expresso please ! And 999 chocolates ! 999 teas !");
		
		// catAt
		testInstance.catAt(0, Randomizer.INSTANCE.drawInt());
		assertThat(testInstance.toString()).isEqualTo("999999 expresso please ! And 999 chocolates ! 999 teas !");
		
		// wrap
		testInstance.wrap(Randomizer.INSTANCE.drawInt(), Randomizer.INSTANCE.drawInt());
		assertThat(testInstance.toString()).isEqualTo("999999999 expresso please ! And 999 chocolates ! 999 teas !999");
	}
	
	@Test
	void wrap() {
		// simple use case
		StringAppender testInstance = new StringAppender();
		testInstance.cat("a").wrap("#", "$");
		assertThat(testInstance.toString()).isEqualTo("#a$");
		
		// test on empty appender
		testInstance = new StringAppender();
		testInstance.wrap("#", "$");
		assertThat(testInstance.toString()).isEqualTo("#$");
	}
	
	@Test
	void ccat() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat("a", 1, "b", 2, ",");
		assertThat(testInstance.toString()).isEqualTo("a,1,b,2");
	}
	
	@Test
	void ccat_iterable() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat(Arrays.asList("a", 1, "b", 2), ",");
		assertThat(testInstance.toString()).isEqualTo("a,1,b,2");
	}
	
	@Test
	void ccat_array() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat(new Object[] { "a", 1, "b", 2, "c", 3, "d", 4 }, ",");
		assertThat(testInstance.toString()).isEqualTo("a,1,b,2,c,3,d,4");
	}
	
	@Test
	void ccat_array_bounded() {
		StringAppender testInstance = new StringAppender();
		testInstance.ccat(new Object[] { "a", 1, "b", 2, "c", 3, "d", 4 }, ",", 2);
		assertThat(testInstance.toString()).isEqualTo("a,1");
		
		// clear for another test
		testInstance.getAppender().setLength(0);
		testInstance.ccat(new Object[] { "a", 1, "b", 2, "c", 3, "d", 4 }, ",", 5);
		assertThat(testInstance.toString()).isEqualTo("a,1,b,2,c");
	}
	
	@Test
	void cutTail() {
		StringAppender testInstance = new StringAppender();
		testInstance.cat("snake tail").cutTail(5);
		assertThat(testInstance.toString()).isEqualTo("snake");
	}
	
	@Test
	void cutHead() {
		StringAppender testInstance = new StringAppender();
		testInstance.cat("headache").cutHead(4);
		assertThat(testInstance.toString()).isEqualTo("ache");
	}
	
	@Test
	void cutAndInsert() {
		StringAppender testInstance = new StringAppender();
		testInstance.cat("headache").cutHead(4).catAt(0, "sstom").cutHead(1);
		assertThat(testInstance.toString()).isEqualTo("stomache");
	}
	
	@Test
	void charAt() {
		StringAppender testInstance = new StringAppender("Hello World !");
		assertThat(testInstance.charAt(6)).isEqualTo('W');
	}
	
	@Test
	void subSequence() {
		StringAppender testInstance = new StringAppender("Hello World !");
		assertThat(testInstance.subSequence(6, 11)).isEqualTo("World");
	}
}
package org.codefilarete.tool;

import org.codefilarete.tool.collection.Arrays;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.params.provider.Arguments.arguments;


/**
 * @author Guillaume Mary
 */
class StringsTest {
	
	@Test
	void isEmpty() {
		assertThat(Strings.isEmpty("a")).isFalse();
		assertThat(Strings.isEmpty("")).isTrue();
		assertThat(Strings.isEmpty(null)).isTrue();
	}
	
	@Test
	void capitalize() {
		assertThat(Strings.capitalize("bonjour")).isEqualTo("Bonjour");
		assertThat(Strings.capitalize("BONJOUR")).isEqualTo("BONJOUR");
		assertThat(Strings.capitalize("")).isEqualTo("");
		assertThat(Strings.capitalize(null)).isEqualTo(null);
	}
	
	static Iterable<Arguments> snakeCaseData() {
		return Arrays.asList(
				arguments("convertCamelCase", "convert_camel_case"),
				arguments("convertCCamelCase", "convert_ccamel_case"),
				arguments("snake_case", "snake_case"),
				arguments("snakecase", "snakecase"),
				arguments("SNAKECASE", "snakecase"),
				arguments("Partially_snake_CASED", "partially_snake_cased"),
				arguments("sn@keCase#", "sn@ke_case#"),
				arguments("", "")
		);
	}
	
	@ParameterizedTest
	@MethodSource("snakeCaseData")
	void snakeCase(String input, String expected) {
		assertThat(Strings.snakeCase(input)).isEqualTo(expected);
	}
	
	@Test
	void head() {
		assertThat(Strings.head("snake", 2)).isEqualTo("sn");
		assertThat(Strings.head("snake", 3)).isEqualTo("sna");
		assertThat(Strings.head("snake", 42)).isEqualTo("snake");
		assertThat(Strings.head(null, 2)).isEqualTo(null);
	}
	
	@Test
	void head_stringBoundary() {
		assertThat(Strings.head("snake", "a")).isEqualTo("sn");
		assertThat(Strings.head("snake", "k")).isEqualTo("sna");
		assertThat(Strings.head("snake", "w")).isEqualTo("");
		assertThat(Strings.head(null, "z")).isEqualTo(null);
	}
	
	@Test
	void split() {
		assertThat(Strings.split("snake", 'a', true)).isEqualTo(Arrays.asList("sna", "ke"));
		assertThat(Strings.split("snake eye", 'e', true)).isEqualTo(Arrays.asList("snake", " e", "ye"));
		assertThat(Strings.split("snake", 'a', false)).isEqualTo(Arrays.asList("sn", "ke"));
		assertThatCode(() -> Strings.split(null, ' ', true)).isInstanceOf(NullPointerException.class);
	}
	
	@Test
	void cutHead() {
		assertThat(Strings.cutHead("snake", 2)).isEqualTo("ake");
		assertThat(Strings.cutHead("snake", 3)).isEqualTo("ke");
		assertThat(Strings.cutHead("snake", 42)).isEqualTo("");
		assertThat(Strings.cutHead(null, 2)).isEqualTo(null);
	}
	
	@Test
	void tail() {
		assertThat(Strings.tail("snake", 2)).isEqualTo("ke");
		assertThat(Strings.tail("snake", 3)).isEqualTo("ake");
		assertThat(Strings.tail("snake", 42)).isEqualTo("snake");
		assertThat(Strings.tail(null, 2)).isEqualTo(null);
	}
	
	@Test
	void tail_stringBoundary() {
		assertThat(Strings.tail("snake", "a")).isEqualTo("ke");
		assertThat(Strings.tail("snake", "n")).isEqualTo("ake");
		assertThat(Strings.tail("snake", "x")).isEqualTo("snake");
		assertThat(Strings.tail(null, "x")).isEqualTo(null);
	}
	
	@Test
	void cutTail() {
		assertThat(Strings.cutTail("snake", 2)).isEqualTo("sna");
		assertThat(Strings.cutTail("snake", 3)).isEqualTo("sn");
		assertThat(Strings.cutTail("snake", 42)).isEqualTo("");
		assertThat(Strings.cutTail(null, 2)).isEqualTo(null);
	}
	
	@Test
	void ellipsis() {
		assertThat(Strings.ellipsis("a too long sentense", 10)).isEqualTo("a too long...");
		assertThat(Strings.ellipsis("0123456789", 10)).isEqualTo("0123456789");
		assertThat(Strings.ellipsis("a word", 10)).isEqualTo("a word");
		assertThat(Strings.ellipsis(null, 10)).isEqualTo(null);
	}
	
	@Test
	void testRepeat() {
		String s10 = "aaaaaaaaaa";
		String s5 = "bbbbb";
		String s = "c";
		// we know if the optimization is good as we give pre-concatenated Strings different from each other
		assertThat(Strings.repeat(3, s).toString()).isEqualTo("ccc");
		assertThat(Strings.repeat(8, s).toString()).isEqualTo("cccccccc");
		assertThat(Strings.repeat(3, s, s10, s5).toString()).isEqualTo("ccc");
		assertThat(Strings.repeat(5, s, s10, s5).toString()).isEqualTo("bbbbb");
		assertThat(Strings.repeat(8, s, s10, s5).toString()).isEqualTo("bbbbbccc");
		assertThat(Strings.repeat(12, s, s10, s5).toString()).isEqualTo("aaaaaaaaaacc");
		assertThat(Strings.repeat(17, s, s10, s5).toString()).isEqualTo("aaaaaaaaaabbbbbcc");
		assertThat(Strings.repeat(27, s, s10, s5).toString()).isEqualTo("aaaaaaaaaaaaaaaaaaaabbbbbcc");
		// inverting order of pre-concatenated Strings may work also but lessly optimized
		assertThat(Strings.repeat(27, s, s5, s10).toString()).isEqualTo("bbbbbbbbbbbbbbbbbbbbbbbbbcc");
	}
	
	@Test
	void footPrint() {
		assertThat(Strings.footPrint(new FootPrintCandidate().setValue1(42), FootPrintCandidate::getValue1)).isEqualTo("42");
		assertThat(Strings.footPrint(new FootPrintCandidate().setValue1(42).setValue2("a"), FootPrintCandidate::getValue1,
				FootPrintCandidate::getValue2)).isEqualTo("42, a");
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
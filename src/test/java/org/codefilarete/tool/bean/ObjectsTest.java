package org.codefilarete.tool.bean;

import org.codefilarete.tool.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class ObjectsTest {
	
	@Test
	void not() {
		assertThat(Arrays.asList("a", "a", "a").stream().allMatch(Objects.not("a"::equals))).isFalse();
	}
	
	@Test
	void decode() {
		assertThat(Objects.fallback("A", "A", "B")).isEqualTo("B");
		assertThat(Objects.fallback("C", "A", "B")).isEqualTo("C");
		assertThat(Objects.fallback(null, null, "B")).isEqualTo("B");
		assertThat(Objects.fallback("A", null, "B")).isEqualTo("A");
		assertThat(Objects.fallback(null, "A", "B")).isEqualTo(null);
	}
	
	@Test
	void equals() {
		assertThat(Objects.equals("a", "a")).isTrue();
		assertThat(Objects.equals("a", "b")).isFalse();
		assertThat(Objects.equals(new String[] { "a" }, new String[] { "a" })).isTrue();
		assertThat(Objects.equals(new String[] { "a" }, new String[] { "b" })).isFalse();
		assertThat(Objects.equals(new String[][] { new String[] { "a" }, new String[] { "b" } }, new String[][] { new String[] { "a" },
				new String[] { "b" } })).isTrue();
		assertThat(Objects.equals(new String[][] { new String[] { "a" }, new String[] { "b" } }, new String[][] { new String[] { "a" },
				new String[] { "c" } })).isFalse();
	}
	
	@Test
	void hashcode() {
		assertThat(Objects.hashCode("a")).isEqualTo(Objects.hashCode("a"));
		assertThat(Objects.hashCode("b")).isNotEqualTo(Objects.hashCode("a"));
		
		assertThat(Objects.hashCode("a", "b")).isEqualTo(Objects.hashCode("a", "b"));
		assertThat(Objects.hashCode("b", "b")).isNotEqualTo(Objects.hashCode("a", "b"));
		assertThat(Objects.hashCode("a", null)).isEqualTo(Objects.hashCode("a", null));
		assertThat(Objects.hashCode("b", "b")).isNotEqualTo(Objects.hashCode("a", null));
		assertThat(Objects.hashCode(null, "a")).isEqualTo(Objects.hashCode(null, "a"));
		assertThat(Objects.hashCode("b", "b")).isNotEqualTo(Objects.hashCode(null, "a"));
		// checking with arrays
		assertThat(Objects.hashCode(new String[] { "a" })).isEqualTo(Objects.hashCode(new String[] { "a" }));
		assertThat(Objects.hashCode(new String[] { "b" })).isNotEqualTo(Objects.hashCode(new String[] { "a" }));
		// checking with arrays of arrays
		assertThat(Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "b" } })).isEqualTo(Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "b" } }));
		assertThat(Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "c" } })).isNotEqualTo(Objects.hashCode(new String[][] { new String[] { "a" }, new String[] { "b" } }));
		
		// not a strong belief, just to explicit null cases
		assertThat(Objects.hashCode(null)).isEqualTo(0);
		assertThat(Objects.hashCode(null, null)).isEqualTo(0);
		assertThat(Objects.hashCode(new String[] { null })).isEqualTo(0);
		assertThat(Objects.hashCode(new String[] { null }, new String[] { null })).isEqualTo(992);
	}
}
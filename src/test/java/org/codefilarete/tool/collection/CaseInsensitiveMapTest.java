package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CaseInsensitiveMapTest {
	
	@Test
	public void addString_stringIsFoundWithoutCaseSensitivity() {
		CaseInsensitiveMap<Object> testInstance = new CaseInsensitiveMap<>();
		
		testInstance.put("Hello", new Object());
		
		assertThat(testInstance.containsKey("Hello")).isTrue();
		assertThat(testInstance.containsKey("HELLO")).isTrue();
		assertThat(testInstance.containsKey("hello")).isTrue();
		
		testInstance.put("hellO", new Object());
		assertThat(testInstance.size()).isEqualTo(1);
	}
	
	// edge case
	@Test
	public void addNull_throwsException() {
		CaseInsensitiveMap<Object> testInstance = new CaseInsensitiveMap<>();
		assertThatCode(() -> testInstance.put(null, new Object()))
				.isInstanceOf(NullPointerException.class);
	}

}
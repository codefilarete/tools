package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentitySetTest {
	
	@Test
	void add() {
		IdentitySet<Object> testInstance = new IdentitySet<>();
		String firstA = "a";
		testInstance.add(firstA);
		String aClone = new String("a");
		testInstance.add(aClone);
		assertThat(testInstance.size()).isEqualTo(2);
	}
	
	@Test
	void contains() {
		IdentitySet<Object> testInstance = new IdentitySet<>();
		String firstA = "a";
		testInstance.add(firstA);
		String aClone = new String("a");
		testInstance.add(aClone);
		assertThat(testInstance.contains("a")).isTrue();
		assertThat(testInstance.contains(aClone)).isTrue();
	}
	
	@Test
	void remove() {
		IdentitySet<Object> testInstance = new IdentitySet<>();
		String firstA = "a";
		testInstance.add(firstA);
		String aClone = new String("a");
		testInstance.add(aClone);
		assertThat(testInstance.remove("a")).isTrue();
		assertThat(testInstance.contains("a")).isFalse();
		assertThat(testInstance.contains(aClone)).isTrue();
		assertThat(testInstance.remove("b")).isFalse();
	}
	
	@Test
	void clear() {
		IdentitySet<Object> testInstance = new IdentitySet<>();
		String firstA = "a";
		testInstance.add(firstA);
		String aClone = new String("a");
		testInstance.add(aClone);
		testInstance.clear();
		assertThat(testInstance.size()).isEqualTo(0);
		assertThat(testInstance.contains("a")).isFalse();
		assertThat(testInstance.contains(aClone)).isFalse();
	}
	
}
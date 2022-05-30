package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class KeepOrderSetTest {
	
	@Test
	void add_emptyConstructor() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>();
		testInstance.add("g");
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("g"));
	}
	
	@Test
	void add_notEmptyConstructor() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		testInstance.add("g");
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("b", "a", "g"));
	}
	
	@Test
	void add_notEmptyConstructor_uniquenessIsMade() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		testInstance.add("b");
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("b", "a"));
	}
	
	@Test
	void addAll_emptyConstructor() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>();
		testInstance.addAll(Arrays.asList("g", "z", "q"));
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("g", "z", "q"));
	}
	
	@Test
	void addAll_notEmptyConstructor() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		testInstance.addAll(Arrays.asList("g", "z", "q"));
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("b", "a", "g", "z", "q"));
	}
	
	@Test
	void addAll_notEmptyConstructor_uniquenessIsAsserted() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		testInstance.addAll(Arrays.asList("a", "b", "q"));
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("b", "a", "q"));
	}
	
	@Test
	void size() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		assertThat(testInstance.size()).isEqualTo(2);
		testInstance.addAll(Arrays.asList("a", "b", "q"));
		assertThat(testInstance.size()).isEqualTo(3);
	}
	
	@Test
	void contains() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		assertThat(testInstance.contains("b")).isTrue();
		
		KeepOrderSet<String> testInstance2 = new KeepOrderSet<>();
		assertThat(testInstance2.contains("b")).isFalse();
		
		KeepOrderSet testInstance3 = new KeepOrderSet();
		assertThat(testInstance3.contains("b")).isFalse();
	}
	
	@Test
	void remove() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		boolean removed = testInstance.remove("b");
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("a"));
		assertThat(removed).isTrue();
		
		KeepOrderSet<String> testInstance2 = new KeepOrderSet<>();
		boolean removed2 = testInstance2.remove("b");
		assertThat(removed2).isFalse();
		
		KeepOrderSet testInstance3 = new KeepOrderSet();
		boolean removed3 = testInstance3.remove("b");
		assertThat(removed3).isFalse();
	}
	
	@Test
	void getAt() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		assertThat(testInstance.getAt(0)).isEqualTo("b");
		assertThat(testInstance.getAt(1)).isEqualTo("a");
		
		KeepOrderSet<String> testInstance2 = new KeepOrderSet<>();
		assertThat(testInstance2.getAt(1)).isNull();
	}
	
	@Test
	void removeAt() {
		KeepOrderSet<String> testInstance = new KeepOrderSet<>("b", "a");
		testInstance.removeAt(0);
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList("a"));
		
		KeepOrderSet<String> testInstance2 = new KeepOrderSet<>();
		testInstance2.removeAt(0);
	}
	
}
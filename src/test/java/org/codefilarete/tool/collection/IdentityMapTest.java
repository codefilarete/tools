package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityMapTest {
	
	@Test
	void put() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		String firstA = "a";
		testInstance.put(firstA, 1);
		String aClone = new String("a");
		testInstance.put(aClone, 2);
		assertThat(testInstance.size()).isEqualTo(2);
	}
	
	@Test
	void containsKey() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		String firstA = "a";
		testInstance.put(firstA, 1);
		String aClone = new String("a");
		testInstance.put(aClone, 2);
		assertThat(testInstance.containsKey("a")).isTrue();
		assertThat(testInstance.containsKey(aClone)).isTrue();
	}
	
	@Test
	void remove() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		String firstA = "a";
		testInstance.put(firstA, 1);
		String aClone = new String("a");
		testInstance.put(aClone, 2);
		assertThat(testInstance.remove("a")).isEqualTo(1);
		assertThat(testInstance.containsKey("a")).isFalse();
		assertThat(testInstance.containsKey(aClone)).isTrue();
		assertThat(testInstance.remove("b")).isEqualTo(null);
	}
	
	@Test
	void clear() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		String firstA = "a";
		testInstance.put(firstA, 1);
		String aClone = new String("a");
		testInstance.put(aClone, 2);
		testInstance.clear();
		assertThat(testInstance.size()).isEqualTo(0);
		assertThat(testInstance.containsKey("a")).isFalse();
		assertThat(testInstance.containsKey(aClone)).isFalse();
	}
	
	@Test
	void putIfAbsent_value() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		assertThat(testInstance.putIfAbsent("a", 1)).isNull();
		assertThat(testInstance.putIfAbsent("a", 2)).isEqualTo(1);
	}
	
	@Test
	void putIfAbsent_supplier() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		assertThat(testInstance.putIfAbsent("a", () -> 1)).isNull();
		assertThat(testInstance.putIfAbsent("a", () -> 2)).isEqualTo(1);
	}
	
	@Test
	void computeIfAbsent() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		assertThat(testInstance.computeIfAbsent("a", k -> 1)).isEqualTo(1);
		assertThat(testInstance.computeIfAbsent("a", k -> 2)).isEqualTo(1);
	}
	
	@Test
	void equals() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		testInstance.put("a", 1);
		testInstance.put("b", 2);
		IdentityMap<String, Object> other = new IdentityMap<>();
		other.put("a", 1);
		
		assertThat(testInstance.equals(other)).isFalse();
		
		other.put("b", 2);
		assertThat(testInstance.equals(other)).isTrue();
	}
	
	@Test
	void hashcode() {
		IdentityMap<String, Object> testInstance = new IdentityMap<>();
		testInstance.put("a", 1);
		testInstance.put("b", 2);
		IdentityMap<String, Object> other = new IdentityMap<>();
		other.put("a", 1);
		
		assertThat(testInstance.hashCode()).isNotEqualTo(other.hashCode());
		
		other.put("b", 2);
		assertThat(testInstance.hashCode()).isEqualTo(other.hashCode());
	}
}
package org.codefilarete.tool;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class DuoTest {
	
	@Test
	void equals_contentAreEqual_equalsIsTrue() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertThat(testInstance).isEqualTo(new Duo<>("a", 1));
	}
	
	@Test
	void equals_contentAreNotEqual_equalsIsFalse() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertThat(testInstance).isNotEqualTo(new Duo<>("a", 2));
		assertThat(testInstance).isNotEqualTo(new Duo<>("b", 1));
	}
	
	@Test
	void equals_worksWithArrayContent() {
		Duo<String[], Integer[]> testInstance = new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 });
		assertThat(testInstance).isEqualTo(new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 }));
		assertThat(testInstance).isNotEqualTo(new Duo<>(new String[] { "a", "c" }, new Integer[] { 1, 2 }));
	}
	
	@Test
	void hashCode_contentAreEqual_hashCodeAreEquals() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertThat(testInstance.hashCode()).isEqualTo(new Duo<>("a", 1).hashCode());
	}
	
	@Test
	void hashCode_contentAreNotEqual_hashCode_contentAreEqual_hashCodeAreNotEqual() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertThat(testInstance.hashCode()).isNotEqualTo(new Duo<>("a", 2).hashCode());
		assertThat(testInstance.hashCode()).isNotEqualTo(new Duo<>("b", 1).hashCode());
	}

	@Test
	void hashCode_worksWithArrayContent() {
		Duo<String[], Integer[]> testInstance = new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 });
		assertThat(testInstance.hashCode()).isEqualTo(new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 }).hashCode());
		assertThat(testInstance.hashCode()).isNotEqualTo(new Duo<>(new String[] { "a", "c" }, new Integer[] { 1, 2 }).hashCode());
	}
	
}
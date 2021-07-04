package org.gama.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Mary
 */
class DuoTest {
	
	@Test
	void equals_contentAreEqual_equalsIsTrue() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertEquals(new Duo<>("a", 1), testInstance);
	}
	
	@Test
	void equals_contentAreNotEqual_equalsIsFalse() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertNotEquals(new Duo<>("a", 2), testInstance);
		assertNotEquals(new Duo<>("b", 1), testInstance);
	}
	
	@Test
	void equals_worksWithArrayContent() {
		Duo<String[], Integer[]> testInstance = new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 });
		assertEquals(new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 }), testInstance);
		assertNotEquals(new Duo<>(new String[] { "a", "c" }, new Integer[] { 1, 2 }), testInstance);
	}
	
	@Test
	void hashCode_contentAreEqual_hashCodeAreEquals() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertEquals(new Duo<>("a", 1).hashCode(), testInstance.hashCode());
	}
	
	@Test
	void hashCode_contentAreNotEqual_hashCode_contentAreEqual_hashCodeAreNotEqual() {
		Duo<String, Integer> testInstance = new Duo<>("a", 1);
		assertNotEquals(new Duo<>("a", 2).hashCode(), testInstance.hashCode());
		assertNotEquals(new Duo<>("b", 1).hashCode(), testInstance.hashCode());
	}

	@Test
	void hashCode_worksWithArrayContent() {
		Duo<String[], Integer[]> testInstance = new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 });
		assertEquals(new Duo<>(new String[] { "a", "b" }, new Integer[] { 1, 2 }).hashCode(), testInstance.hashCode());
		assertNotEquals(new Duo<>(new String[] { "a", "c" }, new Integer[] { 1, 2 }).hashCode(), testInstance.hashCode());
	}
	
}
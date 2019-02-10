package org.gama.lang.bean;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Guillaume Mary
 */
class ObjectsTest {
	
	@Test
	void not() {
		assertFalse(Arrays.asList("a", "a", "a").stream().allMatch(Objects.not("a"::equals)));
	}
}
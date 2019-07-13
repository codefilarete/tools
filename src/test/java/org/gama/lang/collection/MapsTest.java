package org.gama.lang.collection;

import java.util.Map;

import org.gama.lang.test.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Guillaume Mary
 */
class MapsTest {
	
	@Test
	void innerJoin() {
		Map<Integer, Integer> result = Maps.innerJoin(Maps.asHashMap("a", 1).add("b", 2).add("c", 3), Maps.asHashMap("b", 4).add("c", 5).add("d", 6));
		Assertions.assertEquals(Maps.asHashMap(2, 4).add(3, 5), result);
	}
}
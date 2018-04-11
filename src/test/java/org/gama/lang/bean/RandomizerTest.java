package org.gama.lang.bean;

import java.util.List;
import java.util.TreeSet;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
public class RandomizerTest {
	
	@Test
	public void testGetElementsByIndex_listInput() {
		TreeSet<Integer> indexes = new TreeSet<>(Arrays.asList(0, 2, 8));
		List<String> elementsByIndex = Randomizer.getElementsByIndex(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"), indexes);
		assertEquals(Arrays.asList("a", "c", "i"), elementsByIndex);
	}
	
	@Test
	public void testGetElementsByIndex_setInput() {
		TreeSet<Integer> indexes = new TreeSet<>(Arrays.asList(0, 2, 8));
		List<String> elementsByIndex = Randomizer.getElementsByIndex(Arrays.asHashSet("a", "b", "c", "d", "e", "f", "g", "h", "i"), indexes);
		assertEquals(Arrays.asList("a", "c", "i"), elementsByIndex);
	}
	
}
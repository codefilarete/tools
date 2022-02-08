package org.codefilarete.tool.bean;

import java.util.List;
import java.util.TreeSet;

import org.codefilarete.tool.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class RandomizerTest {
	
	@Test
	public void testGetElementsByIndex_listInput() {
		TreeSet<Integer> indexes = new TreeSet<>(Arrays.asList(0, 2, 8));
		List<String> elementsByIndex = Randomizer.getElementsByIndex(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"), indexes);
		assertThat(elementsByIndex).isEqualTo(Arrays.asList("a", "c", "i"));
	}
	
	@Test
	public void testGetElementsByIndex_setInput() {
		TreeSet<Integer> indexes = new TreeSet<>(Arrays.asList(0, 2, 8));
		List<String> elementsByIndex = Randomizer.getElementsByIndex(Arrays.asHashSet("a", "b", "c", "d", "e", "f", "g", "h", "i"), indexes);
		assertThat(elementsByIndex).isEqualTo(Arrays.asList("a", "c", "i"));
	}
	
}
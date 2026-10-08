package org.codefilarete.tool.collection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class CollectionsTest {
	
	@Test
	public void cat() {
		assertThat(Collections.cat(() -> new KeepOrderSet<>(), Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)))
				.isInstanceOf(KeepOrderSet.class)
				.containsExactly(1, 2, 3, 4, 5, 6);
		assertThat(Collections.cat(() -> new ArrayList<>(), Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(6, 7, 8)))
				.isInstanceOf(ArrayList.class)
				.containsExactly(1, 2, 3, 4, 5, 6, 6, 7, 8);
	}
}
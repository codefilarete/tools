package org.codefilarete.tool.collection;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class CollectionsTest {
	
	public static Object[][] testParcelData() {
		return new Object[][] {
				{ Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), 3, Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8)) },
				{ Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), 4, Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(5, 6, 7, 8)) },
		};
	}
	
	@ParameterizedTest
	@MethodSource("testParcelData")
	public void testParcel(List<Integer> integers, int blockSize, List<List<Integer>> expected) throws Exception {
		List<List<Integer>> blocks = Collections.parcel(integers, blockSize);
		assertThat(blocks).isEqualTo(expected);
	}
	
}
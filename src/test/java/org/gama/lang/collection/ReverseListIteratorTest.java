package org.gama.lang.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gama.lang.Duo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Guillaume Mary
 */
public class ReverseListIteratorTest {
	
	@Test
	public void testIteration() {
		List<String> toReverse = Arrays.asList("a", "b", "c");
		List<String> expected = Arrays.asList("c", "b", "a");
		Iterator<String> iterator = new ReverseListIterator<>(toReverse);
		PairIterator<String, String> pairIterator = new PairIterator<>(iterator, expected.iterator());
		while (pairIterator.hasNext()) {
			Duo<String, String> next = pairIterator.next();
			assertEquals(next.getLeft(), next.getRight());
		}
	}
	
	@Test
	public void testIteration_empty() {
		Iterator<?> iterator = new ReverseListIterator<>(Arrays.asList());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testRemove() {
		ArrayList<String> toModify = new ArrayList<>(Arrays.asList("a", "b", "c"));
		Iterator<String> iterator = new ReverseListIterator<>(toModify);
		iterator.next();
		iterator.remove();
		assertEquals(Arrays.asList("a", "b"), toModify);
		assertEquals("b", iterator.next());
	}
}
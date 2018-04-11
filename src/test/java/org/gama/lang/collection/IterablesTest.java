package org.gama.lang.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.gama.lang.collection.Arrays.*;
import static org.gama.lang.collection.Iterables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Guillaume Mary
 */
public class IterablesTest {
	
	@Test
	public void testFirst_iterable() {
		List<String> strings = asList("a", "b");
		assertEquals(first(strings), "a");
		strings = asList("a");
		assertEquals(first(strings), "a");
		// test against null
		assertNull(first(asList()));
		assertNull(first((Iterable) null));
	}
	
	@Test
	public void testFirst_map() {
		Map<String, Integer> aMap = new LinkedHashMap<>();
		aMap.put("d", 25);
		aMap.put("a", 14);
		assertEquals(first(aMap), new LinkedHashMap.SimpleEntry<>("d", 25));
		// test against null
		assertNull(first(new HashMap()));
		assertNull(first((Map) null));
	}
	
	@Test
	public void testFirstValue_map() {
		Map<String, Integer> aMap = new LinkedHashMap<>();
		aMap.put("d", 25);
		aMap.put("a", 14);
		assertEquals((int) firstValue(aMap), 25);
		// test against null
		assertNull(first(new HashMap()));
		assertNull(first((Map) null));
	}
	
	@Test
	public void testLast() {
		List<String> strings = asList("a", "b");
		assertEquals(last(strings), "b");
		strings = asList("a");
		assertEquals(last(strings), "a");
		// test against null
		assertNull(last(emptyList()));
		assertNull(last((List) null));
	}
	
	@Test
	public void testCopy() {
		// test with content
		Set<String> aSet = new LinkedHashSet<>();
		aSet.add("d");
		aSet.add("a");
		assertEquals(asList("d", "a"), copy(aSet));
		// test that copy is not the same instance than the original
		assertNotSame(aSet, copy(aSet));
		// test on corner case: empty content
		assertEquals(asList(), copy(asList()));
	}
	
	@Test
	public void testIntersect() {
		assertEquals(asHashSet(2, 3), intersect(asList(1, 2, 3, 4, 5), asList(2, 3, 6)));
		assertEquals(asHashSet(2, 3), intersect(asList(2, 3, 6), asList(1, 2, 3, 4, 5)));
		assertEquals(emptySet(), intersect(asList(1, 2, 3, 4, 5), emptyList()));
		assertEquals(emptySet(), intersect(emptyList(), asList(1, 2, 3, 4, 5)));
		// test with striclty same instance
		List<Integer> c1 = asList(1, 2, 3, 4, 5);
		assertEquals(asHashSet(1, 2, 3, 4, 5), intersect(c1, c1));
	}
	
	@Test
	public void testMinus() {
		assertEquals(asHashSet(1, 4, 5), minus(asList(1, 2, 3, 4, 5), asList(2, 3, 6)));
		assertEquals(asHashSet(6), minus(asList(2, 3, 6), asList(1, 2, 3, 4, 5)));
		assertEquals(asHashSet(1, 2, 3, 4, 5), minus(asList(1, 2, 3, 4, 5), emptyList()));
		assertEquals(emptySet(), minus(emptyList(), asList(1, 2, 3, 4, 5)));
		// test with striclty same instance
		List<Integer> c1 = asList(1, 2, 3, 4, 5);
		assertEquals(emptySet(), minus(c1, c1));
	}
	
	@Test
	public void testCollect() {
		// test with content
		List<Integer> aSet = asList(1, 2, 1);
		assertEquals(asList("1", "2", "1"), collectToList(aSet, Object::toString));
		
		assertEquals(asHashSet("1", "2"), collect(aSet, Object::toString, HashSet::new));
	}
	
	@Test
	public void testFind() {
		List<String> strings = asList("a", "b");
		Entry<String, String> result = find(strings, String::toUpperCase, o -> o.equals("B"));
		assertEquals("b", result.getKey());
		assertEquals("B", result.getValue());
		// test against null
		result = find(strings, String::toUpperCase, o -> o.equals("c"));
		assertNull(result);
	}
}
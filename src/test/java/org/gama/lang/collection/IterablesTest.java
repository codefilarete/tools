package org.gama.lang.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gama.lang.Duo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.gama.lang.collection.Arrays.asHashSet;
import static org.gama.lang.collection.Arrays.asList;
import static org.gama.lang.collection.Arrays.asSet;
import static org.gama.lang.collection.Iterables.asIterable;
import static org.gama.lang.collection.Iterables.collect;
import static org.gama.lang.collection.Iterables.collectToList;
import static org.gama.lang.collection.Iterables.contains;
import static org.gama.lang.collection.Iterables.copy;
import static org.gama.lang.collection.Iterables.filter;
import static org.gama.lang.collection.Iterables.find;
import static org.gama.lang.collection.Iterables.first;
import static org.gama.lang.collection.Iterables.firstValue;
import static org.gama.lang.collection.Iterables.head;
import static org.gama.lang.collection.Iterables.intersect;
import static org.gama.lang.collection.Iterables.iterate;
import static org.gama.lang.collection.Iterables.last;
import static org.gama.lang.collection.Iterables.minus;
import static org.gama.lang.collection.Iterables.pair;
import static org.gama.lang.collection.Iterables.reverseIterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
public class IterablesTest {
	
	@Test
	public void testFirst_iterable() {
		Iterable<String> strings = asList("a", "b");
		assertEquals("a", first(strings));
		strings = asList("a");
		assertEquals("a", first(strings));
		// test against null
		assertNull(first(asList()));
		assertNull(first((Iterable) null));
	}
	
	@Test
	public void testFirst_iterator() {
		Iterator<String> strings = asList("a", "b").iterator();
		assertEquals("a", first(strings));
		strings = asList("a").iterator();
		assertEquals("a", first(strings));
		// test against null
		assertNull(first(asList()));
		assertNull(first((Iterable) null));
	}
	
	@Test
	public void testFirst_list() {
		List<String> strings = asList("a", "b");
		assertEquals("a", first(strings));
		strings = asList("a");
		assertEquals("a", first(strings));
		// test against null
		assertNull(first(asList()));
		assertNull(first((Iterable) null));
	}
	
	@Test
	public void testFirst_array() {
		String[] strings = new String[] { "a", "b" };
		assertEquals("a", first(strings));
		strings = new String[] { "a" };
		assertEquals("a", first(strings));
		// test against null
		assertNull(first(new Object[] {}));
		assertNull(first((Object[]) null));
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
		assertEquals(25, (int) firstValue(aMap));
		// test against null
		assertNull(first(new HashMap()));
		assertNull(first((Map) null));
	}
	
	@Test
	public void testLast() {
		List<String> strings = asList("a", "b");
		assertEquals("b", last(strings));
		strings = asList("a");
		assertEquals("a", last(strings));
		// test against null
		assertNull(last(emptyList()));
		assertNull(last(null));
	}
	
	@Test
	public void testHead() {
		List<String> strings = asList("a", "b", "c", "d");
		assertEquals(Arrays.asList("a", "b"), head(strings, "c"));
		assertEquals(Arrays.asList("a", "b", "c", "d"), head(strings, "z"));
		strings = asList("a");
		assertEquals(emptyList(), head(strings, "a"));
		// test against null
		assertEquals(emptyList(), head(emptyList(), "xx"));
	}
	
	@Test
	public void testCopy() {
		// test with content
		Set<String> aSet = Arrays.asSet("d", "a");
		assertEquals(asList("d", "a"), copy(aSet));
		// test that copy is not the same instance than the original
		assertNotSame(aSet, copy(aSet));
		// test on corner case: empty content
		assertEquals(asList(), copy(asList()));
	}
	
	@Test
	public void testCopy_iterator() {
		// test with content
		ArrayIterator<String> iterator = new ArrayIterator<>("d", "a");
		assertEquals(asList("d", "a"), copy(iterator, new ArrayList<>()));
		// test that copy is the same instance as the given one
		ArrayList<String> result = new ArrayList<>();
		assertSame(result, copy(iterator, result));
		// test on corner case: empty content
		assertEquals(asList(), copy(new ArrayIterator<>()));
	}
	
	@Test
	public void testCopy_iterable() {
		// test with content
		Iterable<String> iterable = asIterable(new ArrayIterator<>("d", "a"));
		assertEquals(asList("d", "a"), copy(iterable, new ArrayList<>()));
		// test for optimization when Iterable is a Collection
		List<String> strings = Arrays.asList("d", "a");
		Collection[] addedCollection = new Collection[1];
		assertEquals(asList("d", "a"), copy((Iterable) strings, new ArrayList<String>() {
			@Override
			public boolean addAll(Collection<? extends String> c) {
				addedCollection[0] = c;
				return super.addAll(c);
			}
		}));
		assertSame(strings, addedCollection[0]);
		// test that copy is the same instance as the given one
		ArrayList<String> result = new ArrayList<>();
		assertSame(result, copy(iterable, result));
		// test on corner case: empty content
		assertEquals(asList(), copy(new ArrayIterator<>()));
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
	public void testIntersect_withComparator() {
		// let's take a non comparable class : StringBuilder (which is simple to compare in fact)
		StringBuilder a = new StringBuilder().append("a");
		StringBuilder a$ = new StringBuilder().append("a");
		StringBuilder b = new StringBuilder().append("b");
		StringBuilder b$ = new StringBuilder().append("b");
		StringBuilder c = new StringBuilder().append("c");
		StringBuilder c$ = new StringBuilder().append("c");
		StringBuilder d = new StringBuilder().append("d");
		StringBuilder d$ = new StringBuilder().append("d");
		assertEquals(emptySet(), intersect(asList(a, b, c), asList(a$, b$)));
		
		Comparator<StringBuilder> stringBuilderComparator = Comparator.comparing(StringBuilder::toString);
		assertEquals(asHashSet(a, b), intersect(asList(a, b, c), asList(a$, b$), stringBuilderComparator));
		assertEquals(asHashSet(d), intersect(asList(a, d, c), asList(d$, b$), stringBuilderComparator));
		assertEquals(asHashSet(b, c), intersect(asList(c, b, c), asList(b$, a$, b$, c$), stringBuilderComparator));
		assertEquals(emptySet(), intersect(asList(b, c), emptyList(), stringBuilderComparator));
		assertEquals(emptySet(), intersect(emptyList(), asList(c, a, d), stringBuilderComparator));
		// test with striclty same instance
		List<StringBuilder> c1 = asList(b, c);
		assertEquals(asHashSet(b, c), intersect(c1, c1));
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
	public void testEquals() {
		assertFalse(Iterables.equals(asList(1, 2, 3, 4, 5), asList(2, 3, 6), Object::equals));
		assertFalse(Iterables.equals(asList(1, 2, 3, 4, 5), emptyList(), Object::equals));
		assertFalse(Iterables.equals(emptyList(), asList(1, 2, 3, 4, 5), Object::equals));
		List<Integer> c1 = asList(1, 2, 3, 4, 5);
		List<Integer> c2 = asList(1, 2, 3, 4, 5);
		assertTrue(Iterables.equals(c1, c2, Object::equals));
		// test with striclty same instance
		assertTrue(Iterables.equals(c1, c1, Object::equals));
		
		// check with another Predicate
		assertFalse(Iterables.equals(c1, c2, (i1, i2) -> false));
	}
	
	@Test
	public void testCollect() {
		List<Integer> aList = asList(1, 2, 1);
		assertEquals(asList("1", "2", "1"), collectToList(aList, Object::toString));
		
		assertEquals(asHashSet("1", "2"), collect(aList, Object::toString, HashSet::new));
	}
	
	@Test
	public void testCollect_mappedWithFilter() {
		List<Integer> aList = asList(1, 2, 1);
		assertEquals(asHashSet("1"), collect(aList, i -> i.equals(1), Object::toString, HashSet::new));
		assertEquals(asHashSet("2"), collect(aList, i -> i.equals(2), Object::toString, HashSet::new));
		assertEquals(asHashSet("1", "2"), collect(aList, i -> true, Object::toString, HashSet::new));
	}
	
	@Test
	public void testCollect_mappedWith2Filters() {
		List<Integer> aList = asList(1, 2, 1);
		assertEquals(asHashSet("1"), collect(aList, i -> i.equals(1), Object::toString, "1"::equals, HashSet::new));
		assertEquals(asHashSet("2"), collect(aList, i -> i.equals(2), Object::toString, "2"::equals, HashSet::new));
		assertEquals(asHashSet(), collect(aList, i -> i.equals(2), Object::toString, "x"::equals, HashSet::new));
		assertEquals(asHashSet("1", "2"), collect(aList, i -> true, Object::toString, HashSet::new));
		assertEquals(asHashSet("1"), collect(aList, i -> true, Object::toString, "1"::equals, HashSet::new));
	}
	
	@Test
	public void testFind() {
		List<String> strings = asList("a", "b");
		String result = find(strings, o -> o.equalsIgnoreCase("B"));
		assertEquals("b", result);
		// test against null
		result = find(strings, o -> o.equalsIgnoreCase("C"));
		assertNull(result);
	}
	
	@Test
	public void testFind_mapped() {
		List<String> strings = asList("a", "b");
		Duo<String, String> result = find(strings, String::toUpperCase, o -> o.equals("B"));
		assertEquals("b", result.getLeft());
		assertEquals("B", result.getRight());
		// test against null
		result = find(strings, String::toUpperCase, o -> o.equals("c"));
		assertNull(result);
	}
	
	public static Object[][] testConsume() {
		return new Object[][] {
				{ asList("a", "b"), "a", asSet(0) },
				{ asList("a", "b"), "b", asSet(1) },
				{ asList("a", "b", "b"), "b", asSet(1, 2) },
				{ asList("a", "b", "a", "b"), "b", asSet(1, 3) },
				{ asList("a", "b", "a", "b"), "a", asSet(0, 2) },
				{ asList(), "a", asSet() },
		};
	}
	
	@ParameterizedTest
	@MethodSource("testConsume")
	public void testConsume(List<String> input, String lookupElement, Set<Integer> expected) {
		Set<Integer> collectedIndexes = new HashSet<>();
		Iterables.consume(input, lookupElement::equals, (s, i) -> collectedIndexes.add(i));
		assertEquals(expected, collectedIndexes);
	}
	
	@ParameterizedTest
	@MethodSource("testConsume")
	public void testConsume_stream(List<String> input, String lookupElement, Set<Integer> expected) {
		Set<Integer> collectedIndexes = new HashSet<>();
		Iterables.consume(input.stream(), lookupElement::equals, (s, i) -> collectedIndexes.add(i));
		assertEquals(expected, collectedIndexes);
	}
	
	@Test
	public void testContains() {
		List<String> strings = asList("a", "b");
		assertTrue(contains(strings.iterator(), s -> s.equalsIgnoreCase("B")));
		assertFalse(contains(strings.iterator(), s -> s.equalsIgnoreCase("C")));
	}
	
	@Test
	public void testContains_mapped() {
		List<String> strings = asList("a", "b");
		assertTrue(contains(strings.iterator(), String::toUpperCase, s -> s.equals("B")));
		assertFalse(contains(strings.iterator(), String::toUpperCase, s -> s.equals("C")));
	}
	
	@Test
	public void testPair() {
		List<String> strings = asList("a", "b");
		List<Integer> integers = asList(1, 2);
		assertEquals(Maps.asMap("a", 1).add("b", 2), pair(strings, integers));
		
		strings = asList("a", "b", "c");
		assertEquals(Maps.asMap("a", 1).add("b", 2).add("c", null), pair(strings, integers));
		
		integers = asList(1, 2, 3, 4, 5);
		assertEquals(Maps.asMap("a", 1).add("b", 2).add("c", 3).add(null, 5), pair(strings, integers));
	}
	
	@Test
	public void testIterate() {
		List<String> strings = asList("a", "b");
		List<Object> result = new ArrayList<>();
		iterate(strings, (i, s) -> { result.add(i); result.add(s); });
		assertEquals(Arrays.asList(0, "a", 1, "b"), result);
	}
	
	@Test
	public void testFilter() {
		List<String> strings = Arrays.asList("a", "ab", "b");
		assertEquals(Arrays.asList("a", "ab"), filter(strings, s -> s.startsWith("a")));
	}
	
	@Test
	public void testReverseListIterator() {
		assertEquals(Arrays.asList("k", "z", "d", "a"), copy(reverseIterator(Arrays.asList("a", "d", "z", "k"))));
	}
}
package org.codefilarete.tool.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import org.codefilarete.tool.Duo;
import org.codefilarete.tool.collection.PairIterator.EmptyIterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codefilarete.tool.collection.Arrays.*;
import static org.codefilarete.tool.collection.Iterables.first;
import static org.codefilarete.tool.collection.Iterables.last;
import static org.codefilarete.tool.collection.Iterables.*;

/**
 * @author Guillaume Mary
 */
class IterablesTest {
	
	@Test
	void first_iterable() {
		Iterable<String> strings = asList("a", "b");
		assertThat(first(strings)).isEqualTo("a");
		strings = asList("a");
		assertThat(first(strings)).isEqualTo("a");
		// test against null
		assertThat((Object) first(asList())).isNull();
		assertThat(first((Iterable) null)).isNull();
	}
	
	@Test
	void first_iterator() {
		Iterator<String> strings = asList("a", "b").iterator();
		assertThat(first(strings)).isEqualTo("a");
		strings = asList("a").iterator();
		assertThat(first(strings)).isEqualTo("a");
		// test against null
		assertThat((Object) first(asList())).isNull();
		assertThat(first((Iterable) null)).isNull();
	}
	
	@Test
	void first_list() {
		List<String> strings = asList("a", "b");
		assertThat(first(strings)).isEqualTo("a");
		strings = asList("a");
		assertThat(first(strings)).isEqualTo("a");
		// test against null
		assertThat((Object) first(asList())).isNull();
		assertThat(first((Iterable) null)).isNull();
	}
	
	@Test
	void first_array() {
		String[] strings = new String[] { "a", "b" };
		assertThat(first(strings)).isEqualTo("a");
		strings = new String[] { "a" };
		assertThat(first(strings)).isEqualTo("a");
		// test against null
		assertThat(first(new Object[] {})).isNull();
		assertThat(first((Object[]) null)).isNull();
	}
	
	@Test
	void first_map() {
		Map<String, Integer> aMap = new LinkedHashMap<>();
		aMap.put("d", 25);
		aMap.put("a", 14);
		assertThat(new LinkedHashMap.SimpleEntry<>("d", 25)).isEqualTo(first(aMap));
		// test against null
		assertThat(first(new HashMap())).isNull();
		assertThat(first((Map) null)).isNull();
	}
	
	@Test
	void firstValue_map() {
		Map<String, Integer> aMap = new LinkedHashMap<>();
		aMap.put("d", 25);
		aMap.put("a", 14);
		assertThat((int) firstValue(aMap)).isEqualTo(25);
		// test against null
		assertThat(first(new HashMap())).isNull();
		assertThat(first((Map) null)).isNull();
	}
	
	@Test
	void last_list() {
		List<String> strings = asList("a", "b");
		assertThat(last(strings)).isEqualTo("b");
		strings = asList("a");
		assertThat(last(strings)).isEqualTo("a");
		// test against null
		assertThat((Object) last(emptyList())).isNull();
		assertThat((Object) last(null)).isNull();
	}
	
	@Test
	void last_iterable() {
		Set<String> strings = asSet("a", "b");
		assertThat(last(strings)).isEqualTo("b");
		strings = asSet("a");
		assertThat(last(strings)).isEqualTo("a");
		// test against null
		assertThat(last((Iterable) () -> new EmptyIterator<>())).isNull();
		assertThat(last((Iterable) null)).isNull();
		assertThat(last((Iterable) null, null)).isNull();
	}
	
	@Test
	void head() {
		List<String> strings = asList("a", "b", "c", "d");
		assertThat(Iterables.head(strings, "c")).isEqualTo(Arrays.asList("a", "b"));
		assertThat(Iterables.head(strings, "z")).isEqualTo(Arrays.asList("a", "b", "c", "d"));
		strings = asList("a");
		assertThat(Iterables.head(strings, "a")).isEqualTo(emptyList());
		// test against null
		assertThat(Iterables.head(emptyList(), "xx")).isEqualTo(emptyList());
	}
	
	@Test
	void copy_iterable() {
		// test with content
		Set<String> aSet = Arrays.asSet("d", "a");
		assertThat(copy(aSet)).isEqualTo(asList("d", "a"));
		// test that copy is not the same instance as the original
		HashSet<String> result = new HashSet<>();
		assertThat(copy(aSet, result)).isSameAs(result);
		// test on corner case: empty content
		assertThat(copy(asList())).isEqualTo(asList());
	}
	
	@Test
	void copy_iterator() {
		// test with content
		ArrayIterator<String> iterator = new ArrayIterator<>("d", "a");
		assertThat(copy(iterator, new ArrayList<>())).isEqualTo(asList("d", "a"));
		// test that copy is the same instance as the given one
		ArrayList<String> result = new ArrayList<>();
		assertThat(copy(iterator, result)).isSameAs(result);
		// test on corner case: empty content
		assertThat(copy(new ArrayIterator<>())).isEqualTo(asList());
	}
	
	@Test
	void copy_iterable_collector() {
		// test with content
		Iterable<String> iterable = asIterable(new ArrayIterator<>("d", "a"));
		assertThat(copy(iterable, new ArrayList<>())).isEqualTo(asList("d", "a"));
		// test for optimization when Iterable is a Collection
		List<String> strings = Arrays.asList("d", "a");
		Collection[] addedCollection = new Collection[1];
		assertThat(copy((Iterable) strings, new ArrayList<String>() {
			@Override
			public boolean addAll(Collection<? extends String> c) {
				addedCollection[0] = c;
				return super.addAll(c);
			}
		})).isEqualTo(asList("d", "a"));
		assertThat(addedCollection[0]).isSameAs(strings);
		// test that copy is the same instance as the given one
		ArrayList<String> result = new ArrayList<>();
		assertThat(copy(iterable, result)).isSameAs(result);
		// test on corner case: empty content
		assertThat(copy(new ArrayIterator<>())).isEqualTo(asList());
	}
	
	@Test
	void intersect() {
		assertThat(Iterables.intersect(asList(1, 2, 3, 4, 5), asList(2, 3, 6))).isEqualTo(asHashSet(2, 3));
		assertThat(Iterables.intersect(asList(2, 3, 6), asList(1, 2, 3, 4, 5))).isEqualTo(asHashSet(2, 3));
		assertThat(Iterables.intersect(asList(1, 2, 3, 4, 5), emptyList())).isEqualTo(emptySet());
		assertThat(Iterables.intersect(emptyList(), asList(1, 2, 3, 4, 5))).isEqualTo(emptySet());
		// test with strictly same instance
		List<Integer> c1 = asList(1, 2, 3, 4, 5);
		assertThat(Iterables.intersect(c1, c1)).isEqualTo(asHashSet(1, 2, 3, 4, 5));
	}
	
	@Test
	void intersect_withPredicate() {
		// let's take a non-Comparable class : StringBuilder (which is simple to compare in fact)
		StringBuilder a = new StringBuilder().append("a");
		StringBuilder a$ = new StringBuilder().append("a");
		StringBuilder b = new StringBuilder().append("b");
		StringBuilder b$ = new StringBuilder().append("b");
		StringBuilder c = new StringBuilder().append("c");
		StringBuilder c$ = new StringBuilder().append("c");
		StringBuilder d = new StringBuilder().append("d");
		StringBuilder d$ = new StringBuilder().append("d");
		assertThat(Iterables.intersect(asList(a, b, c), asList(a$, b$))).isEqualTo(emptySet());
		
		BiPredicate<StringBuilder, StringBuilder> stringBuilderComparator = (sb1, sb2) -> sb1.toString().equals(sb2.toString());
		assertThat(Iterables.intersect(asList(a, b, c), asList(a$, b$), stringBuilderComparator)).isEqualTo(asHashSet(a, b));
		assertThat(Iterables.intersect(asList(a, d, c), asList(d$, b$), stringBuilderComparator)).isEqualTo(asHashSet(d));
		assertThat(Iterables.intersect(asList(c, b, c), asList(b$, a$, b$, c$), stringBuilderComparator)).isEqualTo(asHashSet(b, c));
		assertThat(Iterables.intersect(asList(b, c), emptyList(), stringBuilderComparator)).isEqualTo(emptySet());
		assertThat(Iterables.intersect(emptyList(), asList(c, a, d), stringBuilderComparator)).isEqualTo(emptySet());
		// test with strictly same instance
		List<StringBuilder> c1 = asList(b, c);
		assertThat(Iterables.intersect(c1, c1, stringBuilderComparator)).isEqualTo(asHashSet(b, c));
	}
	
	@Test
	void minus() {
		assertThat(Iterables.minus(asList(1, 2, 3, 4, 5), asList(2, 3, 6))).isEqualTo(asHashSet(1, 4, 5));
		assertThat(Iterables.minus(asList(2, 3, 6), asList(1, 2, 3, 4, 5))).isEqualTo(asHashSet(6));
		assertThat(Iterables.minus(asList(1, 2, 3, 4, 5), emptyList())).isEqualTo(asHashSet(1, 2, 3, 4, 5));
		assertThat(Iterables.minus(emptyList(), asList(1, 2, 3, 4, 5))).isEqualTo(emptySet());
		// test with strictly same instance
		List<Integer> c1 = asList(1, 2, 3, 4, 5);
		assertThat(Iterables.minus(c1, c1)).isEqualTo(emptySet());
	}
	
	@Test
	void minus_withPredicate() {
		// let's take a non-Comparable class : StringBuilder (which is simple to compare in fact)
		StringBuilder a = new StringBuilder().append("a");
		StringBuilder a$ = new StringBuilder().append("a");
		StringBuilder b = new StringBuilder().append("b");
		StringBuilder b$ = new StringBuilder().append("b");
		StringBuilder c = new StringBuilder().append("c");
		StringBuilder c$ = new StringBuilder().append("c");
		StringBuilder d = new StringBuilder().append("d");
		StringBuilder d$ = new StringBuilder().append("d");
		assertThat(Iterables.intersect(asList(a, b, c), asList(a$, b$))).isEqualTo(emptySet());
		
		BiPredicate<StringBuilder, StringBuilder> stringBuilderComparator = (sb1, sb2) -> sb1.toString().equals(sb2.toString());
		
		assertThat(Iterables.minus(asList(a, b, c), asList(a$, b$), stringBuilderComparator)).isEqualTo(asHashSet(c));
		assertThat(Iterables.minus(asList(a, d, c), asList(d$, b$), stringBuilderComparator)).isEqualTo(asHashSet(a, c));
		assertThat(Iterables.minus(asList(c, b, c), asList(b$, a$, b$, c$), stringBuilderComparator)).isEmpty();
		assertThat(Iterables.minus(asList(b, c), emptyList(), stringBuilderComparator)).isEqualTo(asHashSet(b, c));
		assertThat(Iterables.minus(emptyList(), asList(c, a, d), stringBuilderComparator)).isEmpty();
		// test with strictly same instance
		List<StringBuilder> c1 = asList(b, c);
		assertThat(Iterables.minus(c1, c1, stringBuilderComparator)).isEmpty();
	}
	
	@Test
	void isEmpty() {
		assertThat(Iterables.isEmpty(Arrays.asList(1, 4, 5))).isFalse();
		assertThat(Iterables.isEmpty(Arrays.asList())).isTrue();
		assertThat(Iterables.isEmpty(null)).isTrue();
		assertThat(Iterables.isEmpty(() -> new Iterator() {
			@Override
			public boolean hasNext() {
				return false;
			}
			
			@Override
			public Object next() {
				return null;
			}
		})).isTrue();
		assertThat(Iterables.isEmpty(() -> new Iterator() {
			@Override
			public boolean hasNext() {
				return true;
			}
			
			@Override
			public Object next() {
				return null;
			}
		})).isFalse();
	}
	
	@Test
	void equals() {
		assertThat(Iterables.equals(asList(1, 2, 3, 4, 5), asList(2, 3, 6), Object::equals)).isFalse();
		assertThat(Iterables.equals(asList(1, 2, 3, 4, 5), emptyList(), Object::equals)).isFalse();
		assertThat(Iterables.equals(emptyList(), asList(1, 2, 3, 4, 5), Object::equals)).isFalse();
		List<Integer> c1 = asList(1, 2, 3, 4, 5);
		List<Integer> c2 = asList(1, 2, 3, 4, 5);
		assertThat(Iterables.equals(c1, c2, Object::equals)).isTrue();
		// test with strictly same instance
		assertThat(Iterables.equals(c1, c1, Object::equals)).isTrue();
		
		// check with another Predicate
		assertThat(Iterables.equals(c1, c2, (i1, i2) -> false)).isFalse();
	}
	
	@Test
	void collect() {
		List<Integer> aList = asList(1, 2, 1);
		assertThat(collectToList(aList, Object::toString)).isEqualTo(asList("1", "2", "1"));
		
		assertThat((Set) Iterables.collect(aList, Object::toString, HashSet::new)).isEqualTo(asHashSet("1", "2"));
	}
	
	@Test
	void collect_mappedWithFilter() {
		List<Integer> aList = asList(1, 2, 1);
		assertThat((Set<String>) Iterables.collect(aList, i1 -> i1.equals(1), Object::toString, HashSet::new)).isEqualTo(asHashSet("1"));
		assertThat((Set<String>) Iterables.collect(aList, i -> i.equals(2), Object::toString, HashSet::new)).isEqualTo(asHashSet("2"));
		assertThat((Set<String>) Iterables.collect(aList, i -> true, Object::toString, HashSet::new)).isEqualTo(asHashSet("1", "2"));
	}
	
	@Test
	void collect_mappedWith2Filters() {
		List<Integer> aList = asList(1, 2, 1);
		assertThat((Set<String>) Iterables.collect(aList, i -> i.equals(1), Object::toString, "1"::equals, HashSet::new)).isEqualTo(asHashSet("1"));
		assertThat((Set<String>) Iterables.collect(aList, i -> i.equals(2), Object::toString, "2"::equals, HashSet::new)).isEqualTo(asHashSet("2"));
		assertThat((Set<String>) Iterables.collect(aList, i -> i.equals(2), Object::toString, "x"::equals, HashSet::new)).isEqualTo(asHashSet());
		assertThat((Set<String>) Iterables.collect(aList, i -> true, Object::toString, HashSet::new)).isEqualTo(asHashSet("1", "2"));
		assertThat((Set<String>) Iterables.collect(aList, i -> true, Object::toString, "1"::equals, HashSet::new)).isEqualTo(asHashSet("1"));
	}
	
	@Test
	void filter_iterable() {
		List<String> iterable = asList("a", "ab", "b");
		Iterable<String> result = Iterables.filter(iterable, s -> s.startsWith("a"));
		assertThat(Iterables.copy(result)).isEqualTo(Arrays.asList("a", "ab"));
		// original object is left untouched
		assertThat(iterable).isEqualTo(asList("a", "ab", "b"));
	}
	
	@Test
	void filter_iterator() {
		List<String> iterable = asList("a", "ab", "b");
		Iterator<String> result = Iterables.filter(iterable.iterator(), s -> s.startsWith("a"));
		assertThat(Iterables.copy(result)).isEqualTo(Arrays.asList("a", "ab"));
		// original object is left untouched
		assertThat(iterable).isEqualTo(asList("a", "ab", "b"));
	}
	
	@Test
	void find() {
		List<String> strings = asList("a", "b");
		String result = Iterables.find(strings, o -> o.equalsIgnoreCase("B"));
		assertThat(result).isEqualTo("b");
		// test against null
		result = Iterables.find(strings, o -> o.equalsIgnoreCase("C"));
		assertThat(result).isNull();
	}
	
	@Test
	void find_mapped() {
		List<String> strings = asList("a", "b");
		Duo<String, String> result = Iterables.find(strings, String::toUpperCase, o -> o.equals("B"));
		assertThat(result.getLeft()).isEqualTo("b");
		assertThat(result.getRight()).isEqualTo("B");
		// test against null
		result = Iterables.find(strings, String::toUpperCase, o -> o.equals("c"));
		assertThat(result).isNull();
	}
	
	static Object[][] consume() {
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
	@MethodSource
	void consume(List<String> input, String lookupElement, Set<Integer> expected) {
		Set<Integer> collectedIndexes = new HashSet<>();
		Iterables.consume(input, lookupElement::equals, (s, i) -> collectedIndexes.add(i));
		assertThat(collectedIndexes).isEqualTo(expected);
	}
	
	@ParameterizedTest
	@MethodSource("consume")
	void Consume_stream(List<String> input, String lookupElement, Set<Integer> expected) {
		Set<Integer> collectedIndexes = new HashSet<>();
		Iterables.consume(input.stream(), lookupElement::equals, (s, i) -> collectedIndexes.add(i));
		assertThat(collectedIndexes).isEqualTo(expected);
	}
	
	@Test
	void contains() {
		List<String> strings = asList("a", "b");
		assertThat(Iterables.contains(strings.iterator(), s -> s.equalsIgnoreCase("B"))).isTrue();
		assertThat(Iterables.contains(strings.iterator(), s -> s.equalsIgnoreCase("C"))).isFalse();
	}
	
	@Test
	void contains_mapped() {
		List<String> strings = asList("a", "b");
		assertThat(Iterables.contains(strings.iterator(), String::toUpperCase, s -> s.equals("B"))).isTrue();
		assertThat(Iterables.contains(strings.iterator(), String::toUpperCase, s -> s.equals("C"))).isFalse();
	}
	
	@Test
	void contains_iterable() {
		List<String> strings = asList("a", "b");
		assertThat(Iterables.contains(strings, s -> s.equalsIgnoreCase("B"))).isTrue();
		assertThat(Iterables.contains(strings, s -> s.equalsIgnoreCase("C"))).isFalse();
	}
	
	@Test
	void contains_iterable_mapped() {
		List<String> strings = asList("a", "b");
		assertThat(Iterables.contains(strings, String::toUpperCase, s -> s.equals("B"))).isTrue();
		assertThat(Iterables.contains(strings, String::toUpperCase, s -> s.equals("C"))).isFalse();
	}
	
	@Test
	void pair() {
		List<String> strings = asList("a", "b");
		List<Integer> integers = asList(1, 2);
		assertThat(Iterables.pair(strings, integers)).isEqualTo(Maps.asMap("a", 1).add("b", 2));
		
		strings = asList("a", "b", "c");
		assertThat(Iterables.pair(strings, integers)).isEqualTo(Maps.asMap("a", 1).add("b", 2).add("c", null));
		
		integers = asList(1, 2, 3, 4, 5);
		assertThat(Iterables.pair(strings, integers)).isEqualTo(Maps.asMap("a", 1).add("b", 2).add("c", 3).add(null, 5));
	}
	
	@Test
	void iterate() {
		List<String> strings = asList("a", "b");
		List<Object> result = new ArrayList<>();
		Iterables.iterate(strings, (i, s) -> { result.add(i); result.add(s); });
		assertThat(result).isEqualTo(Arrays.asList(0, "a", 1, "b"));
	}
	
	@Test
	void reverseListIterator() {
		assertThat(Iterables.copy(Iterables.reverseIterator(Arrays.asList("a", "d", "z", "k")))).isEqualTo(Arrays.asList("k", "z", "d", "a"));
	}
	
	@Test
	void map() {
		List<Duo<String, Integer>> input = asList(new Duo<>("a", 1), new Duo<>("b", 2));
		assertThat(Iterables.map(input, Duo::getLeft, Duo::getRight)).isEqualTo(Maps.asMap("a", 1).add("b", 2));
	}
}
package org.gama.lang.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Mary
 */
public class ArraysTest {
	
	@Test
	public void asList() {
		List<String> newList = Arrays.asList("a", "c", "b");
		assertEquals(java.util.Arrays.asList("a", "c", "b"), newList);
		assertTrue(Arrays.asList().isEmpty());
		// the list is modifiable
		assertTrue(Arrays.asList().add("a"));
	}
	
	@Test
	public void asSet() {
		Set<String> newSet = Arrays.asSet("a", "c", "b");
		assertTrue(newSet.containsAll(java.util.Arrays.asList("a", "c", "b")));
		// order is preserved
		assertEquals(java.util.Arrays.asList("a", "c", "b"), new ArrayList<>(newSet));
		assertTrue(Arrays.asSet().isEmpty());
		// the set is modifiable
		assertTrue(Arrays.asList().add("a"));
	}
	
	@Test
	public void asHashSet() {
		Set<String> newSet = Arrays.asHashSet("a", "c", "b");
		assertEquals(java.util.Arrays.asList("a", "b", "c").toString(), newSet.toString());
		assertTrue(Arrays.asHashSet().isEmpty());
		// the set is modifiable
		assertTrue(Arrays.asHashSet().add("a"));
	}
	
	@Test
	public void asTreeSet_arrayArgument() {
		Set<String> newSet = Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER, "a", "b", "A");
		assertEquals(java.util.Arrays.asList("a", "b").toString(), newSet.toString());
		assertTrue(Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER).isEmpty());
		// the set is modifiable
		assertTrue(Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER).add("a"));
	}
	
	/**
	 * Test for Comparator<? super T> signature
	 */
	@Test
	public void asTreeSet_arrayArgument_generics() {
		Comparator<CharSequence> charSequenceComparator = (o1, o2) -> o2.hashCode() - o1.hashCode();
		Set<String> newSet = Arrays.asTreeSet(charSequenceComparator, "a", "b", "A");
		assertEquals(java.util.Arrays.asList("b", "a", "A").toString(), newSet.toString());
		assertTrue(Arrays.asTreeSet(charSequenceComparator).isEmpty());
		// the set is modifiable
		assertTrue(Arrays.asTreeSet(charSequenceComparator).add("a"));
	}
	
	@Test
	public void asTreeSet_collectionArgument() {
		Set<String> newSet = Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER, java.util.Arrays.asList("a", "b", "A"));
		assertEquals(java.util.Arrays.asList("a", "b").toString(), newSet.toString());
		// the set is modifiable
		assertTrue(Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER, new ArrayList<>()).add("a"));
	}
	
	/**
	 * Test for Comparator<? super T> signature
	 */
	@Test
	public void asTreeSet_collectionArgument_generics() {
		Comparator<CharSequence> charSequenceComparator = (o1, o2) -> o2.hashCode() - o1.hashCode();
		List<String> a = java.util.Arrays.asList("a", "b", "A");
		Set<String> newSet = Arrays.asTreeSet(charSequenceComparator, a);
		assertEquals(java.util.Arrays.asList("b", "a", "A").toString(), newSet.toString());
		// the set is modifiable
		assertTrue(Arrays.asTreeSet(charSequenceComparator, new ArrayList<>()).add("a"));
	}
	
	@Test
	public void isEmpty() {
		assertTrue(Arrays.isEmpty(new Object[0]));
		assertTrue(Arrays.isEmpty(null));
		assertFalse(Arrays.isEmpty(new Object[1]));
	}
	
	@Test
	public void testFromPrimitive() {
		assertArrayEquals(new Integer[] {1, 2, 3, 4}, Arrays.fromPrimitive(new int[] {1, 2, 3, 4}));
	}
	
	@Test
	public void get() {
		String[] testData = { "a", "b", "c" };
		assertEquals("a", Arrays.get(0).apply(testData));
		assertEquals("b", Arrays.get(1).apply(testData));
		assertEquals("c", Arrays.get(2).apply(testData));
	}
	
	@Test
	public void get_outOfBoundsAware() {
		String[] testData = { "a", "b", "c" };
		// out of bound cases
		assertEquals("toto", Arrays.get(-1, () -> "toto").apply(testData));
		assertEquals("toto", Arrays.get(3, () -> "toto").apply(testData));
		// in-bound case
		assertEquals("a", Arrays.get(0, () -> "toto").apply(testData));
		assertEquals("c", Arrays.get(2, () -> "toto").apply(testData));
	}
	
	@Test
	public void first() {
		Function<Object[], Object> first = Arrays::first;
		assertEquals("a", first.apply(new String[] { "a", "b", "c" }));
	}
	
	@Test
	public void last() {
		Function<Object[], Object> last = Arrays::last;
		assertEquals("c", last.apply(new String[] { "a", "b", "c" }));
	}
	
	@Test
	public void cat() {
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5}, Arrays.cat(new int[] { 1, 2, 3 }, new int[] { 4, 5}));
		assertArrayEquals(new String[] { "1", "2", "3", "4", "5"}, Arrays.cat(new String[] { "1", "2", "3" }, new String[] { "4", "5"}));
		// since generic type Array creation can be tricky, we ensure that array type is the good one
		assertEquals(String[].class, Arrays.cat(new String[] { "1", "2", "3" }, new String[] { "4", "5"}).getClass());
	}
	
	@Test
	public void head() {
		assertArrayEquals(new Object[] { 1, 2, 3}, Arrays.head(new Object[] { 1, 2, 3, 4, 5 }, 3));
		assertArrayEquals(new String[] { "1", "2", "3"}, Arrays.head(new String[] { "1", "2", "3", "4", "5" }, 3));
		// since generic type Array creation can be tricky, we ensure that array type is the good one
		assertEquals(String[].class, Arrays.head(new String[] { "1", "2", "3", "4", "5" }, 3).getClass());
	}
	
	@Test
	public void tail() {
		assertArrayEquals(new Object[] { 3, 4, 5}, Arrays.tail(new Object[] { 1, 2, 3, 4, 5 }, 3));
		assertArrayEquals(new String[] { "3", "4", "5"}, Arrays.tail(new String[] { "3", "4", "5" }, 3));
		// since generic type Array creation can be tricky, we ensure that array type is the good one
		assertEquals(String[].class, Arrays.tail(new String[] { "3", "4", "5" }, 3).getClass());
	}
}
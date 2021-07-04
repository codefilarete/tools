package org.gama.lang.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class ArraysTest {
	
	@Test
	public void asList() {
		List<String> newList = Arrays.asList("a", "c", "b");
		assertThat(newList).isEqualTo(java.util.Arrays.asList("a", "c", "b"));
		assertThat(Arrays.asList().isEmpty()).isTrue();
		// the list is modifiable
		assertThat(Arrays.asList().add("a")).isTrue();
	}
	
	@Test
	public void asSet() {
		Set<String> newSet = Arrays.asSet("a", "c", "b");
		assertThat(newSet.containsAll(java.util.Arrays.asList("a", "c", "b"))).isTrue();
		// order is preserved
		assertThat(new ArrayList<>(newSet)).isEqualTo(java.util.Arrays.asList("a", "c", "b"));
		assertThat(Arrays.asSet().isEmpty()).isTrue();
		// the set is modifiable
		assertThat(Arrays.asList().add("a")).isTrue();
	}
	
	@Test
	public void asHashSet() {
		Set<String> newSet = Arrays.asHashSet("a", "c", "b");
		assertThat(newSet.toString()).isEqualTo(java.util.Arrays.asList("a", "b", "c").toString());
		assertThat(Arrays.asHashSet().isEmpty()).isTrue();
		// the set is modifiable
		assertThat(Arrays.asHashSet().add("a")).isTrue();
	}
	
	@Test
	public void asTreeSet_arrayArgument() {
		Set<String> newSet = Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER, "a", "b", "A");
		assertThat(newSet.toString()).isEqualTo(java.util.Arrays.asList("a", "b").toString());
		assertThat(Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER).isEmpty()).isTrue();
		// the set is modifiable
		assertThat(Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER).add("a")).isTrue();
	}
	
	/**
	 * Test for Comparator<? super T> signature
	 */
	@Test
	public void asTreeSet_arrayArgument_generics() {
		Comparator<CharSequence> charSequenceComparator = (o1, o2) -> o2.hashCode() - o1.hashCode();
		Set<String> newSet = Arrays.asTreeSet(charSequenceComparator, "a", "b", "A");
		assertThat(newSet.toString()).isEqualTo(java.util.Arrays.asList("b", "a", "A").toString());
		assertThat(Arrays.asTreeSet(charSequenceComparator).isEmpty()).isTrue();
		// the set is modifiable
		assertThat(Arrays.asTreeSet(charSequenceComparator).add("a")).isTrue();
	}
	
	@Test
	public void asTreeSet_collectionArgument() {
		Set<String> newSet = Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER, java.util.Arrays.asList("a", "b", "A"));
		assertThat(newSet.toString()).isEqualTo(java.util.Arrays.asList("a", "b").toString());
		// the set is modifiable
		assertThat(Arrays.asTreeSet(String.CASE_INSENSITIVE_ORDER, new ArrayList<>()).add("a")).isTrue();
	}
	
	/**
	 * Test for Comparator<? super T> signature
	 */
	@Test
	public void asTreeSet_collectionArgument_generics() {
		Comparator<CharSequence> charSequenceComparator = (o1, o2) -> o2.hashCode() - o1.hashCode();
		List<String> a = java.util.Arrays.asList("a", "b", "A");
		Set<String> newSet = Arrays.asTreeSet(charSequenceComparator, a);
		assertThat(newSet.toString()).isEqualTo(java.util.Arrays.asList("b", "a", "A").toString());
		// the set is modifiable
		assertThat(Arrays.asTreeSet(charSequenceComparator, new ArrayList<>()).add("a")).isTrue();
	}
	
	@Test
	public void isEmpty() {
		assertThat(Arrays.isEmpty(new Object[0])).isTrue();
		assertThat(Arrays.isEmpty(null)).isTrue();
		assertThat(Arrays.isEmpty(new Object[1])).isFalse();
	}
	
	@Test
	public void testFromPrimitive() {
		assertThat(Arrays.fromPrimitive(new int[] { 1, 2, 3, 4 })).isEqualTo(new Integer[] { 1, 2, 3, 4 });
	}
	
	@Test
	public void get() {
		String[] testData = { "a", "b", "c" };
		assertThat(Arrays.get(0).apply(testData)).isEqualTo("a");
		assertThat(Arrays.get(1).apply(testData)).isEqualTo("b");
		assertThat(Arrays.get(2).apply(testData)).isEqualTo("c");
	}
	
	@Test
	public void get_outOfBoundsAware() {
		String[] testData = { "a", "b", "c" };
		// out of bound cases
		assertThat(Arrays.get(-1, () -> "toto").apply(testData)).isEqualTo("toto");
		assertThat(Arrays.get(3, () -> "toto").apply(testData)).isEqualTo("toto");
		// in-bound case
		assertThat(Arrays.get(0, () -> "toto").apply(testData)).isEqualTo("a");
		assertThat(Arrays.get(2, () -> "toto").apply(testData)).isEqualTo("c");
	}
	
	@Test
	public void first() {
		Function<Object[], Object> first = Arrays::first;
		assertThat(first.apply(new String[] { "a", "b", "c" })).isEqualTo("a");
	}
	
	@Test
	public void last() {
		Function<Object[], Object> last = Arrays::last;
		assertThat(last.apply(new String[] { "a", "b", "c" })).isEqualTo("c");
	}
	
	@Test
	public void cat() {
		assertThat(Arrays.cat(new int[] { 1, 2, 3 }, new int[] { 4, 5 })).isEqualTo(new int[] { 1, 2, 3, 4, 5 });
		assertThat(Arrays.cat(new String[] { "1", "2", "3" }, new String[] { "4", "5" })).isEqualTo(new String[] { "1", "2", "3", "4", "5" });
		// since generic type Array creation can be tricky, we ensure that array type is the good one
		assertThat(Arrays.cat(new String[] { "1", "2", "3" }, new String[] { "4", "5" }).getClass()).isEqualTo(String[].class);
	}
	
	@Test
	public void head() {
		assertThat(Arrays.head(new Object[] { 1, 2, 3, 4, 5 }, 3)).isEqualTo(new Object[] { 1, 2, 3 });
		assertThat(Arrays.head(new String[] { "1", "2", "3", "4", "5" }, 3)).isEqualTo(new String[] { "1", "2", "3" });
		assertThat(Arrays.head(new byte[] { 1, 2, 3, 4, 5 }, 3)).isEqualTo(new byte[] { 1, 2, 3 });
		assertThat(Arrays.head(new int[] { 1, 2, 3, 4, 5 }, 3)).isEqualTo(new int[] { 1, 2, 3 });
		assertThat(Arrays.head(new long[] { 1, 2, 3, 4, 5 }, 3)).isEqualTo(new long[] { 1, 2, 3 });
		// since generic type Array creation can be tricky, we ensure that array type is the good one
		assertThat(Arrays.head(new String[] { "1", "2", "3", "4", "5" }, 3).getClass()).isEqualTo(String[].class);
	}
	
	@Test
	public void tail() {
		assertThat(Arrays.tail(new Object[] { 1, 2, 3, 4, 5 }, 3)).isEqualTo(new Object[] { 3, 4, 5 });
		assertThat(Arrays.tail(new String[] { "3", "4", "5" }, 3)).isEqualTo(new String[] { "3", "4", "5" });
		// since generic type Array creation can be tricky, we ensure that array type is the good one
		assertThat(Arrays.tail(new String[] { "3", "4", "5" }, 3).getClass()).isEqualTo(String[].class);
	}
}
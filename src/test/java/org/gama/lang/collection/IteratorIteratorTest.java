package org.gama.lang.collection;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * @author Guillaume Mary
 */
@RunWith(DataProviderRunner.class)
public class IteratorIteratorTest {
	
	@DataProvider
	public static Object[][] test_data() {
		return new Object[][] {
				new Object[] { Arrays.asList(Arrays.asList("a"), Arrays.asList("b")), Arrays.asList("a", "b")},
				new Object[] { Arrays.asList(Arrays.asList("a"), Arrays.asList("b", "c")), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c")), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList("a", "b"), Arrays.asList(), Arrays.asList("c")), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList()), Arrays.asList("a", "b", "c")},
				new Object[] { Arrays.asList(Arrays.asList(), Arrays.asList("a", "b"), Arrays.asList("c")), Arrays.asList("a", "b", "c")},
		};
	}
	
	@Test
	@UseDataProvider("test_data")
	public void test(Collection<Iterable<String>> input, List<String> expectedResult) {
		IteratorIterator<String> testInstance = new IteratorIterator<>(input.iterator());
		assertEquals(expectedResult, Iterables.copy(testInstance));
	}
	
	@Test
	public void testContructor() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertEquals(Arrays.asList("a", "b", "c", "d"), Iterables.copy(testInstance));
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testNoSuchElementException() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList());
		testInstance.next();
	}
	
	@Test
	public void testRemove() {
		IteratorIterator<String> testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertEquals("a", testInstance.next());
		testInstance.remove();
		assertEquals(Arrays.asList("b", "c", "d"), Iterables.copy(testInstance));
		
		testInstance = new IteratorIterator<>(Arrays.asList("a"), Arrays.asList("b", "c"), Arrays.asList("d"));
		assertEquals("a", testInstance.next());
		assertEquals("b", testInstance.next());
		assertEquals("c", testInstance.next());
		testInstance.remove();
		assertEquals(Arrays.asList("d"), Iterables.copy(testInstance));
	}
}
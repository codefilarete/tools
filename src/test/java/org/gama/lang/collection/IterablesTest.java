package org.gama.lang.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gama.lang.collection.Iterables.Filter;
import org.gama.lang.collection.Iterables.Finder;
import org.gama.lang.collection.Iterables.IVisitor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Guillaume Mary
 */
public class IterablesTest {
	
	@Test
	public void testFirst_iterable() {
		List<String> strings = Arrays.asList("a", "b");
		assertEquals(Iterables.first(strings), "a");
		strings = Arrays.asList("a");
		assertEquals(Iterables.first(strings), "a");
		// test against null
		assertNull(Iterables.first(Arrays.asList()));
		assertNull(Iterables.first((Iterable) null));
	}
	
	@Test
	public void testFirst_map() {
		Map<String, Integer> aMap = new LinkedHashMap<>();
		aMap.put("d", 25);
		aMap.put("a", 14);
		assertEquals(Iterables.first(aMap), new LinkedHashMap.SimpleEntry<>("d", 25));
		// test against null
		assertNull(Iterables.first(new HashMap()));
		assertNull(Iterables.first((Map) null));
	}
	
	@Test
	public void testFirstValue_map() {
		Map<String, Integer> aMap = new LinkedHashMap<>();
		aMap.put("d", 25);
		aMap.put("a", 14);
		assertEquals((int) Iterables.firstValue(aMap), 25);
		// test against null
		assertNull(Iterables.first(new HashMap()));
		assertNull(Iterables.first((Map) null));
	}
	
	@Test
	public void testLast() {
		List<String> strings = Arrays.asList("a", "b");
		assertEquals(Iterables.last(strings), "b");
		strings = Arrays.asList("a");
		assertEquals(Iterables.last(strings), "a");
		// test against null
		assertNull(Iterables.last(Arrays.asList()));
		assertNull(Iterables.last(null));
	}
	
	@Test
	public void testCopy() {
		// test with content
		Set<String> aSet = new LinkedHashSet<>();
		aSet.add("d");
		aSet.add("a");
		assertEquals(Arrays.asList("d", "a"), Iterables.copy(aSet));
		// test that copy is not the same instance than the original
		assertNotSame(aSet, Iterables.copy(aSet));
		// test on corner case: empty content
		assertEquals(Arrays.asList(), Iterables.copy(Arrays.asList()));
	}
	
	@Test
	public void testCollect() {
		// test with content
		List<Integer> aSet = Arrays.asList(1, 2, 1);
		assertEquals(Arrays.asList("1", "2", "1"), Iterables.collectToList(aSet, Object::toString));
		
		assertEquals(Arrays.asHashSet("1", "2"), Iterables.collect(aSet, Object::toString, HashSet::new));
	}
	
	@Test
	public void testFind() {
		List<String> strings = Arrays.asList("a", "b");
		Entry<String, String> result = Iterables.find(strings, String::toUpperCase, o -> o.equals("B"));
		assertEquals("b", result.getKey());
		assertEquals("B", result.getValue());
		// test against null
		result = Iterables.find(strings, String::toUpperCase, o -> o.equals("c"));
		assertNull(result);
	}
	
	@Test
	public void testVisit_iterable() {
		// Creating a visitor that will go over all
		IVisitor iVisitorMock = mock(IVisitor.class);
		when(iVisitorMock.pursue()).thenReturn(true);
		when(iVisitorMock.visit(any())).thenReturn(new Object());
		
		// basic test that counts main method invokation
		List<String> strings = Arrays.asList("a", "b");
		List<String> visitResult = Iterables.visit(strings, iVisitorMock);
		verify(iVisitorMock, times(2)).visit(anyString());
		assertEquals(visitResult.size(), 2);
		
		// test on empty list
		reset(iVisitorMock);
		visitResult = Iterables.visit(Arrays.asList(), iVisitorMock);
		verify(iVisitorMock, times(0)).visit(anyString());
		assertEquals(visitResult.size(), 0);
		
		// test against null
		assertNull(Iterables.visit((Iterable) null, iVisitorMock));
	}
	
	@Test
	public void testVisit_iterator() {
		// Creating a visitor that will go over all
		IVisitor iVisitorMock = mock(IVisitor.class);
		when(iVisitorMock.pursue()).thenReturn(true);
		
		// basic test that counts main method invokation
		List<String> strings = Arrays.asList("a", "b");
		List<String> visitResult = Iterables.visit(strings.iterator(), iVisitorMock);
		verify(iVisitorMock, times(2)).visit(anyString());
		assertEquals(visitResult.size(), 2);
		
		// test on empty list
		reset(iVisitorMock);
		visitResult = Iterables.visit(Arrays.asList().iterator(), iVisitorMock);
		verify(iVisitorMock, times(0)).visit(anyString());
		assertEquals(visitResult.size(), 0);
	}
	
	@Test
	public void testFilter_Filter() {
		// Creating a visitor that will go over all
		Filter<String> iVisitorMock = spy(new Filter<String>() {
			@Override
			public boolean accept(String s) {
				return "b".equals(s);
			}
		});
		
		// basic test that counts main method invokation
		List<String> strings = Arrays.asList("a", "b", "c", "b", "d");
		List visitResult = Iterables.filter(strings, iVisitorMock);
		verify(iVisitorMock, times(5)).visit(any());
		assertEquals(visitResult, Arrays.asList("b", "b"));
	}
	
	@Test
	public void testFilter_Finder() {
		// Creating a visitor that will go over all
		Finder<String> iVisitorMock = spy(new Finder<String>() {
			@Override
			public boolean accept(String s) {
				return "b".equals(s);
			}
		});
		
		// basic test that counts main method invokation
		List<String> strings = Arrays.asList("a", "b", "c", "b", "d");
		String visitResult = Iterables.filter(strings, iVisitorMock);
		verify(iVisitorMock, times(2)).visit(any());
		assertEquals(visitResult, "b");
	}
}
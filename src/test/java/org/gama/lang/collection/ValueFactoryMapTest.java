package org.gama.lang.collection;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;

import org.gama.lang.trace.ModifiableInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Mary
 */
class ValueFactoryMapTest {
	
	private ValueFactoryMap<String, ModifiableInt> testInstance;
	
	@BeforeEach
	void setUp() {
		testInstance = new ValueFactoryMap<>(new HashMap<>(), k -> new ModifiableInt());
	}
	
	@Test
	void testGet_inputDoesntExist_createInstanceIsCalled() {
		testInstance.get("a").increment();
		assertEquals(1, testInstance.get("a").getValue());
		// a second call should not create a new instance
		testInstance.get("a").increment();
		assertEquals(2, testInstance.get("a").getValue());
	}
	
	@Test
	void testGet_wrongInputType_nullIsReturned() {
		assertNull(testInstance.get(new Object()));
	}
	
	@Test
	void testSize() {
		assertEquals(0, testInstance.size());
		testInstance.get("a");
		assertEquals(1, testInstance.size());
	}
	
	@Test
	void testContains() {
		assertFalse(testInstance.containsKey("a"));
		// contains() should not add any instance to the Map
		assertEquals(0, testInstance.size());
	}
	
	@Test
	void testPut() {
		ModifiableInt modifiableInt = new ModifiableInt(42);
		testInstance.put("a", modifiableInt);
		// get() should not overwrite instance put by put()
		assertEquals(42, testInstance.get("a").getValue());
	}
	
	@Test
	void testPutAll() {
		testInstance.putAll(Maps.asMap("a", new ModifiableInt(666)).add("b", new ModifiableInt(42)));
		assertEquals(666, testInstance.get("a").getValue());
		assertEquals(42, testInstance.get("b").getValue());
		assertEquals(2, testInstance.size());
	}
	
	@Test
	void testRemove() {
		testInstance.put("a", new ModifiableInt(666));
		testInstance.put("b", new ModifiableInt(42));
		testInstance.remove("a");
		assertEquals(1, testInstance.size());
		assertTrue(testInstance.containsKey("b"));
		// if we ask again for "a" then we should have a reseted instance
		assertEquals(0, testInstance.get("a").getValue());
	}
	
	@Test
	void testClear() {
		testInstance.put("a", new ModifiableInt(666));
		testInstance.put("b", new ModifiableInt(42));
		testInstance.clear();
		assertEquals(0, testInstance.size());
		assertFalse(testInstance.containsKey("a"));
		assertFalse(testInstance.containsKey("b"));
		// if we ask again for keys then we should have a reseted instance
		assertEquals(0, testInstance.get("a").getValue());
		assertEquals(0, testInstance.get("b").getValue());
	}
	
	@Test
	void testKeySet() {
		testInstance.put("a", new ModifiableInt(666));
		testInstance.put("b", new ModifiableInt(42));
		assertEquals(Arrays.asSet("a", "b"), testInstance.keySet());
	}
	
	@Test
	void testValues() {
		assertTrue(testInstance.values().isEmpty());
		ModifiableInt evil = new ModifiableInt(666);
		ModifiableInt universeAnswer = new ModifiableInt(42);
		testInstance.put("a", evil);
		testInstance.put("b", universeAnswer);
		// NB: we must wrap testInstance.value() into a Set because its implementation (HashMap.Values) doesn't implement equals(..) nor hashcode()
		assertEquals(Arrays.asHashSet(evil, universeAnswer), new HashSet<>(testInstance.values()));
	}
	
	@Test
	void testEntrySet() {
		assertTrue(testInstance.entrySet().isEmpty());
		ModifiableInt evil = new ModifiableInt(666);
		ModifiableInt universeAnswer = new ModifiableInt(42);
		testInstance.put("a", evil);
		testInstance.put("b", universeAnswer);
		assertEquals(Arrays.asHashSet(new SimpleEntry<>("a", evil), new SimpleEntry<>("b", universeAnswer)), testInstance.entrySet());
	}
}
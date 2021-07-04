package org.gama.lang.collection;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;

import org.gama.lang.trace.ModifiableInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
		assertThat(testInstance.get("a").getValue()).isEqualTo(1);
		// a second call should not create a new instance
		testInstance.get("a").increment();
		assertThat(testInstance.get("a").getValue()).isEqualTo(2);
	}
	
	@Test
	void testGet_wrongInputType_nullIsReturned() {
		assertThat(testInstance.get(new Object())).isNull();
	}
	
	@Test
	void testSize() {
		assertThat(testInstance.size()).isEqualTo(0);
		testInstance.get("a");
		assertThat(testInstance.size()).isEqualTo(1);
	}
	
	@Test
	void testContains() {
		assertThat(testInstance.containsKey("a")).isFalse();
		// contains() should not add any instance to the Map
		assertThat(testInstance.size()).isEqualTo(0);
	}
	
	@Test
	void testPut() {
		ModifiableInt modifiableInt = new ModifiableInt(42);
		testInstance.put("a", modifiableInt);
		// get() should not overwrite instance put by put()
		assertThat(testInstance.get("a").getValue()).isEqualTo(42);
	}
	
	@Test
	void testPutAll() {
		testInstance.putAll(Maps.asMap("a", new ModifiableInt(666)).add("b", new ModifiableInt(42)));
		assertThat(testInstance.get("a").getValue()).isEqualTo(666);
		assertThat(testInstance.get("b").getValue()).isEqualTo(42);
		assertThat(testInstance.size()).isEqualTo(2);
	}
	
	@Test
	void testRemove() {
		testInstance.put("a", new ModifiableInt(666));
		testInstance.put("b", new ModifiableInt(42));
		testInstance.remove("a");
		assertThat(testInstance.size()).isEqualTo(1);
		assertThat(testInstance.containsKey("b")).isTrue();
		// if we ask again for "a" then we should have a reseted instance
		assertThat(testInstance.get("a").getValue()).isEqualTo(0);
	}
	
	@Test
	void testClear() {
		testInstance.put("a", new ModifiableInt(666));
		testInstance.put("b", new ModifiableInt(42));
		testInstance.clear();
		assertThat(testInstance.size()).isEqualTo(0);
		assertThat(testInstance.containsKey("a")).isFalse();
		assertThat(testInstance.containsKey("b")).isFalse();
		// if we ask again for keys then we should have a reseted instance
		assertThat(testInstance.get("a").getValue()).isEqualTo(0);
		assertThat(testInstance.get("b").getValue()).isEqualTo(0);
	}
	
	@Test
	void testKeySet() {
		testInstance.put("a", new ModifiableInt(666));
		testInstance.put("b", new ModifiableInt(42));
		assertThat(testInstance.keySet()).isEqualTo(Arrays.asSet("a", "b"));
	}
	
	@Test
	void testValues() {
		assertThat(testInstance.values().isEmpty()).isTrue();
		ModifiableInt evil = new ModifiableInt(666);
		ModifiableInt universeAnswer = new ModifiableInt(42);
		testInstance.put("a", evil);
		testInstance.put("b", universeAnswer);
		// NB: we must wrap testInstance.value() into a Set because its implementation (HashMap.Values) doesn't implement equals(..) nor hashcode()
		assertThat(new HashSet<>(testInstance.values())).isEqualTo(Arrays.asHashSet(evil, universeAnswer));
	}
	
	@Test
	void testEntrySet() {
		assertThat(testInstance.entrySet().isEmpty()).isTrue();
		ModifiableInt evil = new ModifiableInt(666);
		ModifiableInt universeAnswer = new ModifiableInt(42);
		testInstance.put("a", evil);
		testInstance.put("b", universeAnswer);
		assertThat(testInstance.entrySet()).isEqualTo(Arrays.asHashSet(new SimpleEntry<>("a", evil), new SimpleEntry<>("b", universeAnswer)));
	}
}
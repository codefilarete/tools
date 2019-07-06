package org.gama.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.gama.lang.trace.ModifiableInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Guillaume Mary
 */
class NullableTest {
	
	private static final Supplier<String> NULL_SUPPLIER = () -> null;
	private static final Supplier<String> STRING_SUPPLIER = () -> "hello";
	
	@Test
	void testConstructors_object() {
		String nullObject = null;
		assertEquals("hello", Nullable.nullable(nullObject).getOr("hello"));
		Function<String, String> appendingWorldFunction = o -> o + " world";
		Function<String, String> shallNotCallFunction = o -> {
			fail("this code should not even be invoked");
			return o;
		};
		assertEquals("hello", Nullable.nullable(nullObject, shallNotCallFunction).getOr("hello"));
		assertEquals("hello", Nullable.nullable(nullObject, shallNotCallFunction).getOr("hello"));
		assertEquals("hello", Nullable.nullable(nullObject, shallNotCallFunction, shallNotCallFunction).getOr("hello"));
		
		assertEquals("hello world", Nullable.nullable("hello", appendingWorldFunction).getOr("john"));
		assertEquals("hello world", Nullable.nullable("hello", appendingWorldFunction).getOr("john"));
		assertEquals("hello world !", Nullable.nullable("hello", appendingWorldFunction, o -> o + " !").getOr("john"));
	}
	
	@Test
	void testConstructors_supplier() {
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER).getOr("hello"));
		Function<String, String> appendingWorldFunction = o -> o + " world";
		Function<String, String> shallNotCallFunction = o -> {
			fail("this code should not even be invoked");
			return o;
		};
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER, shallNotCallFunction).getOr("hello"));
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER, shallNotCallFunction).getOr("hello"));
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER, shallNotCallFunction, shallNotCallFunction).getOr("hello"));
		
		assertEquals("hello world", Nullable.nullable(STRING_SUPPLIER, appendingWorldFunction).getOr("john"));
		assertEquals("hello world", Nullable.nullable(STRING_SUPPLIER, appendingWorldFunction).getOr("john"));
		assertEquals("hello world !", Nullable.nullable(STRING_SUPPLIER, appendingWorldFunction, o -> o + " !").getOr("john"));
	}
	
	@Test
	void testIsPresent() {
		assertTrue(Nullable.nullable(new Object()).isPresent());
		assertFalse(Nullable.nullable((Object) null).isPresent());
	}
	
	@Test
	void testGetOr_object() {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).getOr("hello"));
		assertEquals("hello", Nullable.nullable((String) null).getOr("hello"));
	}
	
	@Test
	void testGetOr_supplier() {
		Object value = new Object();
		Supplier<Object> dummyFunction = () -> value;
		assertEquals(value, Nullable.nullable(value).getOr(dummyFunction));
		assertEquals(value, Nullable.nullable((Object) null).getOr(dummyFunction));
	}
	
	@Test
	void testElseSet_object() {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).elseSet("hello").get());
		assertEquals("hello", Nullable.nullable((String) null).elseSet("hello").get());
	}
	
	@Test
	void testElseSet_supplier() {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).elseSet(() -> "hello").get());
		assertEquals("hello", Nullable.nullable((String) null).elseSet(() -> "hello").get());
	}
	
	
	@Test
	void testMap() {
		// simple case
		assertEquals("Hello World", Nullable.nullable("Hello").map(o -> o + " World").get());
		// with null value
		assertNull(Nullable.nullable((Object) null).map(o -> {
			fail("this code should not even be invoked");
			return o + " World";
		}).get());
	}
	
	@Test
	void testTest() {
		// simple case
		assertTrue(Nullable.nullable("Hello").test(o -> o.equals("Hello")));
		// with null value
		assertNull(Nullable.nullable((Object) null).test(o -> {
			fail("this code should not even be invoked");
			return false;
		}));
	}
	
	@Test
	void testFilter() {
		// simple case
		assertEquals("Hello", Nullable.nullable("Hello").filter(o -> o.contains("ll")).get());
		// with null value
		assertNull(Nullable.nullable((Object) null).filter(o -> {
			fail("this code should not even be invoked");
			return false;
		}).get());
	}
	
	@Test
	void testInvoke() {
		String value = "hello";
		ModifiableInt isCalled = new ModifiableInt();
		Consumer<String> dummyFunction = s -> isCalled.increment();
		Nullable.nullable(value).invoke(dummyFunction);
		assertEquals(1, isCalled.getValue());
		Nullable.nullable(value).invoke(dummyFunction);
		assertEquals(2, isCalled.getValue());
		Nullable.nullable((String) null).invoke(dummyFunction);
		assertEquals(2, isCalled.getValue());
	}
	
	@Test
	void testMapThrower() {
		assertThrows(IOException.class, () -> Nullable.nullable(new ByteArrayOutputStream()).mapThrower(b -> {
			b.write(0);
			throw new IOException();
		}));
	}
	
	
	@Test
	void testInvokeThrower() {
		assertThrows(IOException.class, () -> Nullable.nullable(new ByteArrayOutputStream()).invokeThrower(b -> {
			b.write(0);
			throw new IOException();
		}));
	}
	
	@Test
	void testElseThrow() throws IOException {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).elseThrow(new IOException()).get());
	}
	
	@Test
	void testElseThrow_nullValue_exceptionIsThrown() {
		assertThrows(IOException.class, () -> Nullable.nullable((Object) null).elseThrow(new IOException()));
	}
	
	@Test
	void testGetElseThrow() throws IOException {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).getOrThrow(new IOException()));
	}
	
	@Test
	void testGetElseThrow_nullValue_exceptionIsThrown() {
		assertThrows(IOException.class, () -> Nullable.nullable((Object) null).getOrThrow(new IOException()));
	}
	
}
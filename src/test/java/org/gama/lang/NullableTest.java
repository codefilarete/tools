package org.gama.lang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.gama.lang.trace.IncrementableInt;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Guillaume Mary
 */
public class NullableTest {
	
	private static final Supplier<String> NULL_SUPPLIER = () -> null;
	private static final Supplier<String> STRING_SUPPLIER = () -> "hello";
	
	@Test
	public void testConstructors_object() {
		String nullObject = null;
		assertEquals("hello", Nullable.nullable(nullObject).orGet("hello"));
		Function<String, String> appendingWorldFunction = o -> o + " world";
		Function<String, String> shallNotCallFunction = o -> {
			fail("this code should not even be invoked");
			return o;
		};
		assertEquals("hello", Nullable.nullable(nullObject, shallNotCallFunction).orGet("hello"));
		assertEquals("hello", Nullable.nullable(nullObject, shallNotCallFunction).orGet("hello"));
		assertEquals("hello", Nullable.nullable(nullObject, shallNotCallFunction, shallNotCallFunction).orGet("hello"));
		
		assertEquals("hello world", Nullable.nullable("hello", appendingWorldFunction).orGet("john"));
		assertEquals("hello world", Nullable.nullable("hello", appendingWorldFunction).orGet("john"));
		assertEquals("hello world !", Nullable.nullable("hello", appendingWorldFunction, o -> o + " !").orGet("john"));
	}
	
	@Test
	public void testConstructors_supplier() {
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER).orGet("hello"));
		Function<String, String> appendingWorldFunction = o -> o + " world";
		Function<String, String> shallNotCallFunction = o -> {
			fail("this code should not even be invoked");
			return o;
		};
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER, shallNotCallFunction).orGet("hello"));
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER, shallNotCallFunction).orGet("hello"));
		assertEquals("hello", Nullable.nullable(NULL_SUPPLIER, shallNotCallFunction, shallNotCallFunction).orGet("hello"));
		
		assertEquals("hello world", Nullable.nullable(STRING_SUPPLIER, appendingWorldFunction).orGet("john"));
		assertEquals("hello world", Nullable.nullable(STRING_SUPPLIER, appendingWorldFunction).orGet("john"));
		assertEquals("hello world !", Nullable.nullable(STRING_SUPPLIER, appendingWorldFunction, o -> o + " !").orGet("john"));
	}
	
	@Test
	public void testIsPresent() {
		assertTrue(Nullable.nullable(new Object()).isPresent());
		assertFalse(Nullable.nullable((Object) null).isPresent());
	}
	
	@Test
	public void testOrGet_object() {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).orGet("hello"));
		assertEquals("hello", Nullable.nullable((String) null).orGet("hello"));
	}
	
	@Test
	public void testOrGet_supplier() {
		Object value = new Object();
		Supplier<Object> dummyFunction = () -> value;
		assertEquals(value, Nullable.nullable(value).orGet(dummyFunction));
		assertEquals(value, Nullable.nullable((Object) null).orGet(dummyFunction));
	}
	
	@Test
	public void testOrGet_function() {
		// exact type method input test
		Function<String, byte[]> getBytes = String::getBytes;
		assertArrayEquals(new byte[] { 'h', 'e', 'l', 'l', 'o', }, Nullable.nullable("hello").orGet(getBytes));
		assertNull(Nullable.nullable((String) null).orGet(getBytes));
		
		// super type method input test
		Function<Object, Class> getClass = Object::getClass;
		assertEquals(String.class, Nullable.nullable("hello").orGet(getClass));
		assertNull(Nullable.nullable((String) null).orGet(getClass));
		
		// implemented interface method input test
		Function<CharSequence, Integer> getLength = CharSequence::length;
		assertEquals((long) 5, (long) Nullable.nullable("hello").orGet(getLength));
		assertNull(Nullable.nullable((String) null).orGet(getLength));
	}
	
	@Test
	public void testSet_object() {
		Object value = new Object();
		assertEquals("hello", Nullable.nullable(value).set("hello").get());
		assertEquals("hello", Nullable.nullable((String) null).set("hello").get());
	}
	
	@Test
	public void testSet_supplier() {
		Object value = new Object();
		assertEquals("hello", Nullable.nullable(value).set(() -> "hello").get());
		assertEquals("hello", Nullable.nullable((String) null).set(() -> "hello").get());
	}
	
	@Test
	public void testOrSet_object() {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).orSet("hello").get());
		assertEquals("hello", Nullable.nullable((String) null).orSet("hello").get());
	}
	
	@Test
	public void testOrSet_supplier() {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).orSet(() -> "hello").get());
		assertEquals("hello", Nullable.nullable((String) null).orSet(() -> "hello").get());
	}
	
	
	@Test
	public void testApply() {
		// simple case
		assertEquals("Hello World", Nullable.nullable("Hello").apply(o -> o + " World").get());
		// with null value
		assertNull(Nullable.nullable((Object) null).apply(o -> {
			fail("this code should not even be invoked");
			return o + " World";
		}).get());
	}
	
	@Test
	public void testTest() {
		// simple case
		assertTrue(Nullable.nullable("Hello").test(o -> o.equals("Hello")).get());
		// with null value
		Nullable.nullable((Object) null).test(o -> {
			fail("this code should not even be invoked");
			return false;
		});
	}
	
	@Test
	public void testAccept() {
		String value = "hello";
		IncrementableInt isCalled = new IncrementableInt();
		Consumer<String> dummyFunction = s -> isCalled.increment();
		Nullable.nullable(value).accept(dummyFunction);
		assertEquals(1, isCalled.getValue());
		Nullable.nullable(value).accept(dummyFunction);
		assertEquals(2, isCalled.getValue());
	}
	
	@Test(expected = IOException.class)
	public void testApplyThrowing() throws IOException {
		Nullable.nullable(new ByteArrayOutputStream()).applyThrowing(b -> {
			b.write(0);
			throw new IOException();
		});
	}
	
	
	@Test(expected = IOException.class)
	public void testAcceptThrowing() throws IOException {
		Nullable.nullable(new ByteArrayOutputStream()).acceptThrowing(b -> {
			b.write(0);
			throw new IOException();
		});
	}
	
	@Test
	public void testOrThrow() throws IOException {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).orThrow(new IOException()).get());
	}
	
	@Test(expected = IOException.class)
	public void testOrThrow_nullValue_exceptionIsThrown() throws IOException {
		Nullable.nullable((Object) null).orThrow(new IOException());
	}
	
	@Test
	public void testGetOrThrow() throws IOException {
		Object value = new Object();
		assertEquals(value, Nullable.nullable(value).getOrThrow(new IOException()));
	}
	
	@Test(expected = IOException.class)
	public void testGetOrThrow_nullValue_exceptionIsThrown() throws IOException {
		Nullable.nullable((Object) null).getOrThrow(new IOException());
	}
	
}
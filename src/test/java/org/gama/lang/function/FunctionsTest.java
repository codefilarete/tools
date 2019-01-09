package org.gama.lang.function;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
public class FunctionsTest {
	
	@Test
	public void testToFunction_predicateArg() {
		Function<Object, Boolean> predicateAsFunction = Functions.toFunction("hello"::equals);
		assertTrue(predicateAsFunction.apply("hello"));
		assertFalse(predicateAsFunction.apply("coucou"));
	}
	
	@Test
	public void testToFunction_methodArg() throws NoSuchMethodException {
		Function<Object, Object> methodAsFunction = Functions.toFunction(Integer.class.getDeclaredMethod("toString"));
		assertEquals("1", methodAsFunction.apply(1));
	}
	
	@Test
	public void testToBiConsumer_methodArg() throws NoSuchMethodException {
		BiConsumer<Object, Object> methodAsBiConsumer = Functions.toBiConsumer(StringBuilder.class.getDeclaredMethod("append", int.class));
		StringBuilder target = new StringBuilder();
		methodAsBiConsumer.accept(target, 1);
		assertEquals("1", target.toString());
	}
	
	@Test
	public void testAsPredicate() {
		Predicate<StringBuilder> methodAsBiConsumer = Functions.asPredicate(StringBuilder::toString, "1"::equals);
		StringBuilder target = new StringBuilder();
		assertFalse(methodAsBiConsumer.test(target));
		target.append(1);
		assertTrue(methodAsBiConsumer.test(target));
	}
	
	@Test
	public void testAsPredicate_givenMethod() {
		Predicate<StringBuilder> methodAsBiConsumer = Functions.asPredicate(StringBuilder::toString, "1"::equals);
		StringBuilder target = new StringBuilder();
		assertFalse(methodAsBiConsumer.test(target));
		target.append(1);
		assertTrue(methodAsBiConsumer.test(target));
	}
	
	@Test
	public void testLink() {
		// StringBuffer is transformed to a "2" String, then parsed to a 2 int
		assertEquals(2, (int) Functions.link(Object::toString, Integer::parseInt).apply(new StringBuffer("2")));
		// test againt null
		assertNull(Functions.link(Object::toString, Integer::parseInt).apply(null));
		// the first function may return null, the second doesn't support null, the whole chain will return a null value
		assertNull(Functions.link(Object::toString, Integer::parseInt).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		}));
	}
	
	@Test
	public void testLink_andThen() {
		assertEquals("2", Functions.link(Object::toString, Integer::parseInt).andThen(i -> String.valueOf(i+1)).apply(new Object() {
			@Override
			public String toString() {
				return "1";
			}
		}));
		// Testing the andThen(..) method that must be null-pointer-proof too
		assertNull(Functions.link(Object::toString, Integer::parseInt).andThen(String::valueOf).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		}));
	}
	
	@Test
	public void testChain() {
		// StringBuffer is transformed to a "2" String, then parsed to a 2 int
		assertEquals(2, (int) Functions.chain(Object::toString, Integer::parseInt).apply(new StringBuffer("2")));
	}
	
	@Test
	public void testChain_throwNPE1() {
		assertThrows(NullPointerException.class, () -> Functions.chain(Object::toString, Object::toString).apply(null));
	}
	
	@Test
	public void testChain_throwNPE2() {
		assertThrows(NullPointerException.class, () -> Functions.chain(Object::toString, Object::toString).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		}));
	}
	
	@Test
	public void testChain_andThen_throwNPE() {
		assertThrows(NullPointerException.class, () -> Functions.chain(Object::toString, Object::toString).andThen(String::valueOf).apply(null));
	}
	
}
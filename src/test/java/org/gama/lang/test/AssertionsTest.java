package org.gama.lang.test;

import java.util.function.BiPredicate;
import java.util.function.Function;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static org.gama.lang.test.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
class AssertionsTest {
	
	/* How to test an assertion without relying on another assertion framework ?
	 * By checking that failing cases throw an exception, and others don't throw anything 
	 */
	
	@Test
	void assertEquals_sameInstance() {
		Object expected = new Object();
		Object actual = expected;
		assertEquals(expected, actual);
	}
	
	@Test
	void assertAllEquals_null_null() {
		Assertions.assertAllEquals(null, null);
	}
	
	@Test
	void assertAllEquals_Set_List() {
		Assertions.assertAllEquals(Arrays.asSet("a", "b", "c"), Arrays.asList("a", "b", "c"));
		Assertions.assertAllEquals(Arrays.asHashSet("b", "c", "a"), Arrays.asList("a", "b", "c"));
		assertAssertion(() -> Assertions.assertAllEquals(Arrays.asSet("b", "c", "a"), Arrays.asList("a", "b", "c")),
				"expected: <[b, c, a]> but was: <[a, b, c]>");
	}
	
	@Test
	void assertAllEquals_Set_List_false() {
		assertAssertion(() -> Assertions.assertAllEquals(Arrays.asSet("a", "b", "c"), Arrays.asList("1", "2", "3")), "expected: <[a, b, c]> but was: <[1, 2, 3]>");
		assertAssertion(() -> Assertions.assertAllEquals(Arrays.asSet("a", "b", "c"), Arrays.asList("a", "b")), "expected: <[a, b, c]> but was: <[a, b]>");
	}
	
	@Test
	void assertEquals_nonNull_null_fails() {
		Object expected = new Object();
		assertAssertion(() -> assertEquals(expected, null), "expected: <" + expected + "> but was: <null>");
	}
	
	@Test
	void assertEquals_null_nonNull_fails() {
		Object actual = new Object();
		assertAssertion(() -> assertEquals(null, actual), "expected: <null> but was: <" + actual + ">");
	}
	
	@Test
	void assertEquals_inputHasSameToString_failureMssageContainsDistinguishNames() {
		final String toStringMessage = "my dummy toString";
		Object expected = new Object() {
			@Override
			public String toString() {
				return toStringMessage;
			}
		};
		Object actual = new Object() {
			@Override
			public String toString() {
				return toStringMessage;
			}
		};
		assertAssertion(() -> assertEquals(expected, actual),
				"expected: " + Assertions.systemToString(expected) + "<" + toStringMessage + ">"
				+ " but was: " + Assertions.systemToString(actual) + "<" + toStringMessage + ">");
	}
	
	@Test
	void assertEquals_comparator() {
		assertEquals("A", "a", String.CASE_INSENSITIVE_ORDER);
	}
	
	@Test
	void assertEquals_comparator_failureMessage() {
		assertAssertion(() -> assertEquals("b", "a", String.CASE_INSENSITIVE_ORDER),
				"expected: <b> but was: <a>");
	}
	
	@Test
	void assertEquals_mapper() {
		assertEquals("b", "a", s -> 1);
	}
	
	@Test
	void assertEquals_mapper_failureMessage() {
		assertAssertion(() -> assertEquals("b", "a", (Function<String, String>) String::toUpperCase),
				"expected: <B> but was: <A>");
	}
	
	@Test
	void assertEquals_predicate() {
		assertEquals("b", "a", (BiPredicate) (e, a) -> true);
	}
	
	@Test
	void assertEquals_predicate_failureMessage() {
		BiPredicate biPredicate = (e, a) -> false;
		assertAssertion(() -> assertEquals("b", "a", biPredicate),
				"expected: <b> but was: <a>");
	}
	
	@Test
	void assertEquals_mapper_predicate() {
		assertEquals("b", "a", String::toUpperCase, (e, a) -> true);
	}
	
	@Test
	void assertEquals_mapper_predicate_failureMessage() {
		BiPredicate biPredicate = (e, a) -> false;
		assertAssertion(() -> assertEquals("b", "a", String::toUpperCase, biPredicate),
				"expected: <B> but was: <A>");
	}
	
	@Test
	void assertEquals_iterable_mapper() {
		Assertions.assertAllEquals(Arrays.asList("a", "b"), Arrays.asHashSet("a", "b"), (Function<String, String>) String::toUpperCase);
	}
	
	@Test
	void assertEquals_iterable_mapper_failureMessage() {
		assertAssertion(() -> Assertions.assertAllEquals(Arrays.asList("b", "a"), Arrays.asHashSet("c", "b"), (Function<String, String>) String::toUpperCase),
				"expected: <[B, A]> but was: <[B, C]>");
	}
	
	@Test
	void systemToString() {
		Object o = new Object();
		assertEquals("java.lang.Object@" + Integer.toHexString(System.identityHashCode(o)), Assertions.systemToString(o));
		// works on toString()-overriding classes
		assertEquals("java.lang.String@" + Integer.toHexString(System.identityHashCode("a")), Assertions.systemToString("a"));
	}
	
	@Test
	void assertAllEquals() {
		Assertions.assertAllEquals(Arrays.asList("1", "2", "3"), Arrays.asList(1, 2, 3), Integer::valueOf, Function.identity());
	}
	
	@Test
	void assertAllEquals_failureMessage() {
		assertAssertion(() -> Assertions.assertAllEquals(Arrays.asList("0", "2", "3"), Arrays.asList(1, 2, 3), Integer::valueOf, Function.identity()),
				"expected: <[0, 2, 3]> but was: <[1, 2, 3]>");
	}
	
	@Test
	void assertAllEquals_predicate() {
		Assertions.assertAllEquals(Arrays.asList("1", "2", "3"), Arrays.asList(1, 2, 3), (s, i) -> Integer.valueOf(s).equals(i));
	}
	
	@Test
	void assertAllEquals_predicate_failureMessage() {
		assertAssertion(() -> Assertions.assertAllEquals(Arrays.asList("0", "2", "3"), Arrays.asList(1, 2, 3), (s, i) -> Integer.valueOf(s).equals(i)),
				"expected: <[0, 2, 3]> but was: <[1, 2, 3]>");
	}
	
	private static void assertAssertion(Runnable assertion, String message) {
		AssertionFailedError thrownException = null;
		try {
			assertion.run();
		} catch (AssertionFailedError assertionFailedError) {
			thrownException = assertionFailedError;
		}
		if (thrownException == null) {
			throw new AssertionFailedError("assertion failed");
		} else {
			if (!message.equals(thrownException.getMessage())) {
				throw new AssertionFailedError("wrong message", message, thrownException.getMessage());
			}
		}
	}
}
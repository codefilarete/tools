package org.gama.lang.function;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Guillaume Mary
 */
public class FunctionsTest {
	
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
	public void testChain() {
		// StringBuffer is transformed to a "2" String, then parsed to a 2 int
		assertEquals(2, (int) Functions.chain(Object::toString, Integer::parseInt).apply(new StringBuffer("2")));
	}
	
	@Test(expected = NullPointerException.class)
	public void testChain_throwNPE1() {
		assertNull(Functions.chain(Object::toString, Object::toString).apply(null));
	}
	
	@Test(expected = NullPointerException.class)
	public void testChain_throwNPE2() {
		assertNull(Functions.chain(Object::toString, Object::toString).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		}));
	}
	
}
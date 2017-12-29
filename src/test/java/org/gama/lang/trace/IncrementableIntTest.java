package org.gama.lang.trace;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Guillaume Mary
 */
public class IncrementableIntTest {
	
	@Test
	public void testIncrement() {
		IncrementableInt testInstance = new IncrementableInt();
		
		testInstance.increment();
		assertEquals(1, testInstance.getValue());
		
		assertEquals(2, testInstance.increment());
		
		assertEquals(6, testInstance.increment(4));
		
		assertEquals(-6, testInstance.increment(-12));
	}
	
	@Test
	public void testConstructor() {
		IncrementableInt testInstance = new IncrementableInt(2);
		
		assertEquals(2, testInstance.getValue());
		
		testInstance.increment();
		assertEquals(3, testInstance.getValue());
	}
}
package org.gama.lang.trace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
public class ModifiableIntTest {
	
	@Test
	public void testIncrement() {
		ModifiableInt testInstance = new ModifiableInt();
		
		testInstance.increment();
		assertEquals(1, testInstance.getValue());
		
		assertEquals(2, testInstance.increment());
		
		assertEquals(6, testInstance.increment(4));
		
		assertEquals(-6, testInstance.increment(-12));
	}
	
	@Test
	public void testDecrement() {
		ModifiableInt testInstance = new ModifiableInt();
		
		testInstance.decrement();
		assertEquals(-1, testInstance.getValue());
		
		assertEquals(-2, testInstance.decrement());
		
		assertEquals(-6, testInstance.decrement(4));
		
		assertEquals(6, testInstance.decrement(-12));
	}
	
	@Test
	public void testReset() {
		ModifiableInt testInstance = new ModifiableInt();
		
		testInstance.reset(3);
		assertEquals(3, testInstance.getValue());
	}
	
	@Test
	public void testConstructor() {
		ModifiableInt testInstance = new ModifiableInt(2);
		
		assertEquals(2, testInstance.getValue());
		
		testInstance.increment();
		assertEquals(3, testInstance.getValue());
	}
}
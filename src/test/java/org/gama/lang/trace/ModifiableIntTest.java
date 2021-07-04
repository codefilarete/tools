package org.gama.lang.trace;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
public class ModifiableIntTest {
	
	@Test
	public void testIncrement() {
		ModifiableInt testInstance = new ModifiableInt();
		
		testInstance.increment();
		assertThat(testInstance.getValue()).isEqualTo(1);
		
		assertThat(testInstance.increment()).isEqualTo(2);
		
		assertThat(testInstance.increment(4)).isEqualTo(6);
		
		assertThat(testInstance.increment(-12)).isEqualTo(-6);
	}
	
	@Test
	public void testDecrement() {
		ModifiableInt testInstance = new ModifiableInt();
		
		testInstance.decrement();
		assertThat(testInstance.getValue()).isEqualTo(-1);
		
		assertThat(testInstance.decrement()).isEqualTo(-2);
		
		assertThat(testInstance.decrement(4)).isEqualTo(-6);
		
		assertThat(testInstance.decrement(-12)).isEqualTo(6);
	}
	
	@Test
	public void testReset() {
		ModifiableInt testInstance = new ModifiableInt();
		
		testInstance.reset(3);
		assertThat(testInstance.getValue()).isEqualTo(3);
	}
	
	@Test
	public void testConstructor() {
		ModifiableInt testInstance = new ModifiableInt(2);
		
		assertThat(testInstance.getValue()).isEqualTo(2);
		
		testInstance.increment();
		assertThat(testInstance.getValue()).isEqualTo(3);
	}
}
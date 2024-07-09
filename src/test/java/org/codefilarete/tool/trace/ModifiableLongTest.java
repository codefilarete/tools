package org.codefilarete.tool.trace;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class ModifiableLongTest {
	
	@Test
	void increment() {
		ModifiableLong testInstance = new ModifiableLong();
		
		testInstance.increment();
		assertThat(testInstance.getValue()).isEqualTo(1);
		
		assertThat(testInstance.increment()).isEqualTo(2);
		
		assertThat(testInstance.increment(4)).isEqualTo(6);
		
		assertThat(testInstance.increment(-12)).isEqualTo(-6);
	}
	
	@Test
	void decrement() {
		ModifiableLong testInstance = new ModifiableLong();
		
		testInstance.decrement();
		assertThat(testInstance.getValue()).isEqualTo(-1);
		
		assertThat(testInstance.decrement()).isEqualTo(-2);
		
		assertThat(testInstance.decrement(4)).isEqualTo(-6);
		
		assertThat(testInstance.decrement(-12)).isEqualTo(6);
	}
	
	@Test
	void reset() {
		ModifiableLong testInstance = new ModifiableLong();
		
		testInstance.reset(3);
		assertThat(testInstance.getValue()).isEqualTo(3);
	}
	
	@Test
	void constructor() {
		ModifiableLong testInstance = new ModifiableLong(2);
		
		assertThat(testInstance.getValue()).isEqualTo(2);
		
		testInstance.increment();
		assertThat(testInstance.getValue()).isEqualTo(3);
	}
}
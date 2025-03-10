package org.codefilarete.tool.trace;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class MutableIntTest {
	
	@Test
	void increment() {
		MutableInt testInstance = new MutableInt();
		
		testInstance.increment();
		assertThat(testInstance.getValue()).isEqualTo(1);
		
		assertThat(testInstance.increment()).isEqualTo(2);
		
		assertThat(testInstance.increment(4)).isEqualTo(6);
		
		assertThat(testInstance.increment(-12)).isEqualTo(-6);
	}
	
	@Test
	void decrement() {
		MutableInt testInstance = new MutableInt();
		
		testInstance.decrement();
		assertThat(testInstance.getValue()).isEqualTo(-1);
		
		assertThat(testInstance.decrement()).isEqualTo(-2);
		
		assertThat(testInstance.decrement(4)).isEqualTo(-6);
		
		assertThat(testInstance.decrement(-12)).isEqualTo(6);
	}
	
	@Test
	void reset() {
		MutableInt testInstance = new MutableInt();
		
		testInstance.reset(3);
		assertThat(testInstance.getValue()).isEqualTo(3);
	}
	
	@Test
	void constructor() {
		MutableInt testInstance = new MutableInt(2);
		
		assertThat(testInstance.getValue()).isEqualTo(2);
		
		testInstance.increment();
		assertThat(testInstance.getValue()).isEqualTo(3);
	}
}
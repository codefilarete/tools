package org.codefilarete.tool.collection;

import java.util.Iterator;

import org.codefilarete.tool.trace.MutableInt;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Guillaume Mary
 */
class SteppingIteratorTest {
	
	@Test
	void hasNext_mustCallDelegateHasNext() {
		Iterator<String> delegate = mock(Iterator.class);
		SteppingIterator testInstance = new SteppingIterator<String>(delegate, 10) {
			@Override
			protected void onStep() {
				
			}
		};
		testInstance.hasNext();
		verify(delegate).hasNext();
		testInstance.hasNext();
		verify(delegate, Mockito.times(2)).hasNext();
		testInstance.hasNext();
		verify(delegate, Mockito.times(3)).hasNext();
	}
	
	@Test
	void next_mustCallDelegateNext() {
		Iterator<String> iterator = mock(Iterator.class);
		SteppingIterator testInstance = new SteppingIterator<String>(iterator, 10) {
			@Override
			protected void onStep() {
				
			}
		};
		testInstance.next();
		verify(iterator).next();
		testInstance.next();
		verify(iterator, Mockito.times(2)).next();
		testInstance.next();
		verify(iterator, Mockito.times(3)).next();
	}
	
	@Test
	void remove_mustCallDelegateRemove() {
		Iterator<String> iterator = mock(Iterator.class);
		SteppingIterator testInstance = new SteppingIterator<String>(iterator, 10) {
			@Override
			protected void onStep() {
				
			}
		};
		testInstance.remove();
		verify(iterator).remove();
		testInstance.remove();
		verify(iterator, Mockito.times(2)).remove();
		testInstance.remove();
		verify(iterator, Mockito.times(3)).remove();
	}
	
	@Test
	void onStep() {
		Iterator<String> iterator = mock(Iterator.class);
		when(iterator.hasNext()).thenReturn(true);
		MutableInt counter = new MutableInt();
		SteppingIterator testInstance = new SteppingIterator<String>(iterator, 2) {
			@Override
			protected void onStep() {
				counter.increment();
			}
		};
		// very first call should not call onStep()
		testInstance.hasNext();
		assertThat(counter.getValue()).isEqualTo(0);
		
		// forcing to go to step 1, still, should not call onStep()
		testInstance.next();
		testInstance.hasNext();
		assertThat(counter.getValue()).isEqualTo(0);
		
		// forcing to go to step 2, should call onStep() because we reach chunk size of 2
		testInstance.next();
		testInstance.hasNext();
		assertThat(counter.getValue()).isEqualTo(1);
		
		// forcing to go to step 3, should not call onStep() because previous call reset step count
		testInstance.next();
		testInstance.hasNext();
		assertThat(counter.getValue()).isEqualTo(1);
		
		// forcing to go to step 4, should call onStep() because we reach chunk size of 2 (again)
		testInstance.next();
		testInstance.hasNext();
		assertThat(counter.getValue()).isEqualTo(2);
		
		// faking that underlying iterator has no more items to make test instance end, should call onStep() due to remaining items clearance
		when(iterator.hasNext()).thenReturn(false);
		testInstance.next();
		testInstance.hasNext();
		assertThat(counter.getValue()).isEqualTo(3);
	}
}
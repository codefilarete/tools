package org.codefilarete.tool.collection;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Guillaume Mary
 */
public class SteppingIteratorTest {
	
	@Test
	public void testHasNext() {
		Iterator<String> iterator = mock(Iterator.class);
		SteppingIterator testInstance = new SteppingIterator<String>(iterator, 10) {
			@Override
			protected void onStep() {
				
			}
		};
		testInstance.hasNext();
		verify(iterator).hasNext();
		testInstance.hasNext();
		verify(iterator, Mockito.times(2)).hasNext();
		testInstance.hasNext();
		verify(iterator, Mockito.times(3)).hasNext();
	}
	
	@Test
	public void testNext() {
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
	public void testRemove() {
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
	public void testOnStep() {
		Iterator<String> iterator = mock(Iterator.class);
		when(iterator.hasNext()).thenReturn(true);
		final int[] i= new int[1];
		SteppingIterator testInstance = new SteppingIterator<String>(iterator, 2) {
			@Override
			protected void onStep() {
				i[0]++;
			}
		};
		testInstance.hasNext();
		assertThat(i[0]).isEqualTo(0);
		testInstance.next();
		testInstance.hasNext();
		assertThat(i[0]).isEqualTo(0);
		testInstance.next();
		testInstance.hasNext();
		assertThat(i[0]).isEqualTo(1);
		testInstance.next();
		testInstance.hasNext();
		assertThat(i[0]).isEqualTo(1);
		testInstance.next();
		testInstance.hasNext();
		assertThat(i[0]).isEqualTo(2);
		
		when(iterator.hasNext()).thenReturn(false);
		testInstance.next();
		testInstance.hasNext();
		assertThat(i[0]).isEqualTo(3);
	}
}
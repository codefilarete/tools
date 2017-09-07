package org.gama.lang.function;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Very close to {@link Sequence} but with a seed on each {@link #next(Object)} : {@link Serie}s doesn't have to keep their state.
 * 
 * @author Guillaume Mary
 */
@FunctionalInterface
public interface Serie<I> {
	
	/** Default instance for an integer serie */
	IntegerSerie INTEGER_SERIE = new IntegerSerie();
	
	/** Default instance for an integer serie, long-typed */
	LongSerie LONG_SERIE = new LongSerie();
	
	/** Every {@link #next(Object)} returns a new {@link Date} */
	NowSerie NOW_SERIE = new NowSerie();
	
	I next(I input);
	
	/** An integer serie */
	class IntegerSerie implements Serie<Integer> {
		
		@Override
		public Integer next(Integer input) {
			return ++input;
		}
	}
	
	/** An integer serie, long-typed */
	class LongSerie implements Serie<Long> {
		
		@Override
		public Long next(Long input) {
			return ++input;
		}
	}
	
	/** Every {@link #next(Object)} returns a new {@link Date} */
	class NowSerie implements Serie<LocalDateTime> {
		
		@Override
		public LocalDateTime next(LocalDateTime input) {
			return LocalDateTime.now();
		}
	}
}

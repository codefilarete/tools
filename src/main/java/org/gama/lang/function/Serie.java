package org.gama.lang.function;

import java.util.Date;

/**
 * Very close of {@link Sequence} but with a seed on each {@link #next(Object)} : {@link Serie}s doesn't have to keep their own state.
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
	class NowSerie implements Serie<Date> {
		
		@Override
		public Date next(Date input) {
			return new Date();
		}
	}
}

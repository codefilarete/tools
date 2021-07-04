package org.gama.lang.trace;

import javax.annotation.Nonnegative;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.gama.lang.Duo;
import org.gama.lang.StringAppender;
import org.gama.lang.VisibleForTesting;

/**
 * Class aimed at mesuring time between 2 events.
 * Basic implementation and use case : start it (done at instanciation time), then printing throught {@link #toString()}.
 * One can reset it with {@link #start()}.
 * 
 * @author Guillaume Mary
 */
public class Chrono {
	
	@VisibleForTesting
	static final int MILLIS_MAX = 1000;
	@VisibleForTesting
	static final int SEC_MAX = 60 * MILLIS_MAX;
	@VisibleForTesting
	static final int MIN_MAX = 60 * SEC_MAX;
	@VisibleForTesting
	static final int H_MAX = 24 * MIN_MAX;

	private enum TimeConstant {
		DAY(H_MAX, "d"),
		HOUR(MIN_MAX, "h"),
		MINUTE(SEC_MAX, "min"),
		SECOND(MILLIS_MAX, "s"),
		MILLIS(1, "ms");
		
		private int millisCount;
		private String timeUnit;
		
		TimeConstant(int millisCount, String timeUnit) {
			this.millisCount = millisCount;
			this.timeUnit = timeUnit;
		}
		
		private static final Set<TimeConstant> MILLIS_TIME = Collections.unmodifiableSet(EnumSet.of(MILLIS));
		private static final Set<TimeConstant> SECOND_TIME = Collections.unmodifiableSet(EnumSet.of(SECOND, MILLIS));
		private static final Set<TimeConstant> MINUTE_TIME = Collections.unmodifiableSet(EnumSet.of(MINUTE, SECOND, MILLIS));
		private static final Set<TimeConstant> HOUR_TIME = Collections.unmodifiableSet(EnumSet.of(HOUR, MINUTE, SECOND, MILLIS));
		private static final Set<TimeConstant> DAY_TIME = Collections.unmodifiableSet(EnumSet.of(DAY, HOUR, MINUTE, SECOND, MILLIS));
		
		/**
		 * Gives constants (in decreasing order) necessary to format given milliseconds.
		 * 
		 * @param millis any (non null) millisecond
		 * @return a set of {@link TimeConstant} in decreasing order (from biggest unit to smallest)
		 */
		private static Set<TimeConstant> getTimeConstantsToUse(@Nonnegative long millis) {
			Set<TimeConstant> constantsToUse;
			if (millis < MILLIS_MAX) {
				constantsToUse = TimeConstant.MILLIS_TIME;
			} else if (millis < SEC_MAX) {
				constantsToUse = TimeConstant.SECOND_TIME;
			} else if (millis < MIN_MAX) {
				constantsToUse = TimeConstant.MINUTE_TIME;
			} else if (millis < H_MAX) {
				constantsToUse = TimeConstant.HOUR_TIME;
			} else {
				constantsToUse = TimeConstant.DAY_TIME;
			}
			return constantsToUse;
		}
	
	}
	
	/**
	 * Formats an instant such as "1:02min 24s 103ms" (each value is optional)
	 * 
	 * @param millis any (non null) millisecond
	 * @return a printable representation of {@code millis} 
	 */
	public static String format(long millis) {
		StringAppender result = new StringAppender();
		Set<TimeConstant> constantsToUse = TimeConstant.getTimeConstantsToUse(millis);
		for (TimeConstant timeConstant : constantsToUse) {
			Duo<Long, Long> divide = divide(millis, timeConstant.millisCount);
			long quotient = divide.getLeft();
			// we print only what's necessary
			result.catIf(quotient != 0, Long.toString(quotient), timeConstant.timeUnit, " ");
			millis = divide.getRight();
		}
		// removing last space before returning
		return result.cutTail(1).toString();
	}
	
	private static Duo<Long, Long> divide(long millis, int divisor) {
		return new Duo<>(millis / divisor, millis%divisor);
	}
	
	/**
	 * Gives current instant
	 * 
	 * @return current time in milliscond (see {@link System#currentTimeMillis()}
	 */
	public static long now() {
		return System.currentTimeMillis();
	}

	private long startTime;
	
	/** Creates new Chrono */
	public Chrono () {
		start();
	}
	
	/**
	 * Returns start time
	 * @return start time (in milliseconds)
	 */
	public long getStartTime() {
		return this.startTime;
	}
	
	/**
	 * Starts this chronometer
	 * @return start time (which means now !)
	 */
	public long start() {
		return startTime = now();
	}
	
	/**
	 * Gives spent time since this chronometer starts
	 * @return the difference between start time and now, in millis
	 */
	public long getElapsedTime() {
		return now() - startTime;
	}
	
	/**
	 * Format spent time since this chronometer's start time
	 * @see Chrono#format
	 * @see Chrono#getElapsedTime
	 */
	@Override
	public String toString() {
		return format(getElapsedTime());
	}
}

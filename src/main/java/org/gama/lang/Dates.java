package org.gama.lang;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Tool class around Date concept.
 * 
 * @author Guillaume Mary
 */
public class Dates {
	
	/** System clock, for clean code */
	private static final Clock SYSTEM_CLOCK = Clock.systemDefaultZone();
	
	/** The clock to be used to give referent dates and times, default is system one */
	private static Clock clock = Clock.systemDefaultZone();
	
	/**
	 * Executes some code at a given {@link LocalDateTime}. Made to test some code in past or future to ensures that some code doesn't depend on
	 * system current millis.
	 * Be aware that this method will ask for a lock on a static-shared instance, hence will block until the lock is released.
	 * 
	 * Prefer {@link #doWithClock(Clock, Runnable)} for a more fine-grained method
	 * 
	 * @param runtime the instant to be used when running code
	 * @param runnable the code to be executed
	 * @see #doWithClock(Clock, Runnable) 
	 */
	public static void runAtTime(LocalDateTime runtime, Runnable runnable) {
		ZoneId zoneId = ZoneId.systemDefault();
		doWithClock(Clock.fixed(runtime.toInstant(zoneId.getRules().getOffset(runtime)), zoneId), runnable);
	}
	
	/**
	 * Executes some code at a given {@link Clock}. Made to test some code in past or future to ensures that some code doesn't depend on system clock.
	 * Be aware that this method will ask for a lock on a static-shared instance, hence will block until the lock is released.
	 * 
	 * Prefer {@link #runAtTime(LocalDateTime, Runnable)} for a simplier use case.
	 * 
	 * @param newClock the clock to be used when running code
	 * @param runnable the code to be executed
	 * @see #runAtTime(LocalDateTime, Runnable) 
	 */
	public static void doWithClock(Clock newClock, Runnable runnable) {
		synchronized (SYSTEM_CLOCK) {
			clock = newClock;
			try {
				runnable.run();
			} catch (RuntimeException e) {
				clock = SYSTEM_CLOCK;
			}
		}
	}
	
	/**
	 * Gives "now instant". Should be used in preference to {@link LocalDateTime#now()} for code that must be tested in future or past because this
	 * method will take {@link Clock} set on {@link Dates} class which can be changed when using {@link #doWithClock(Clock, Runnable)}.
	 * 
	 * @return "now instant" according to clock defined in this class, which is the system one by default
	 */
	public static LocalDateTime now() {
		return LocalDateTime.now(clock);
	}
}

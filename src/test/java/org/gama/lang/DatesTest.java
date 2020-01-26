package org.gama.lang;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
class DatesTest {
	
	@Test
	void doWithClock() {
		LocalDateTime expectedInstant = LocalDateTime.of(2018, Month.DECEMBER, 10, 15, 50);
		ZoneId zoneId = ZoneId.systemDefault();
		Instant fixedInstant = expectedInstant.toInstant(zoneId.getRules().getOffset(expectedInstant));
		Clock clock = Clock.fixed(fixedInstant, zoneId);
		LocalDateTime[] nowAtAtime = new LocalDateTime[1];
		Dates.doWithClock(clock, () -> nowAtAtime[0] = Dates.now());
		assertEquals(expectedInstant, nowAtAtime[0]);
		
		Dates.doWithClock(clock, () -> nowAtAtime[0] = Dates.now().plusHours(1));
		assertEquals(expectedInstant.plusHours(1), nowAtAtime[0]);
	}
	
	@Test
	void runAtTime() {
		LocalDateTime runtime = LocalDateTime.of(2018, Month.DECEMBER, 10, 15, 50);
		LocalDateTime[] nowAtAtime = new LocalDateTime[1];
		Dates.runAtTime(runtime, () -> nowAtAtime[0] = Dates.now());
		assertEquals(runtime, nowAtAtime[0]);
		
		Dates.runAtTime(runtime, () -> nowAtAtime[0] = Dates.now().plusHours(1));
		assertEquals(runtime.plusHours(1), nowAtAtime[0]);
		
	}
}
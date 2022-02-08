package org.codefilarete.tool;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

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
		assertThat(nowAtAtime[0]).isEqualTo(expectedInstant);
		
		Dates.doWithClock(clock, () -> nowAtAtime[0] = Dates.now().plusHours(1));
		assertThat(nowAtAtime[0]).isEqualTo(expectedInstant.plusHours(1));
	}
	
	@Test
	void doWithClock_revertOnExceptionAndNormalCase() {
		LocalDateTime expectedInstant = LocalDateTime.of(2018, Month.DECEMBER, 10, 15, 50);
		ZoneId zoneId = ZoneId.systemDefault();
		Instant fixedInstant = expectedInstant.toInstant(zoneId.getRules().getOffset(expectedInstant));
		Clock clock = Clock.fixed(fixedInstant, zoneId);
		LocalDateTime[] nowAtAtime = new LocalDateTime[1];
		Dates.doWithClock(clock, () -> nowAtAtime[0] = Dates.now());
		assertThat(nowAtAtime[0]).isEqualTo(expectedInstant);
		
		assertThat((double) LocalDateTime.now().getNano()).isCloseTo(Dates.now().getNano(), offset(10_000_000d));	// almost equal with 10ms gap
		
		try {
			Dates.doWithClock(clock, () -> {
				throw new IllegalArgumentException();
			});
		} catch (IllegalArgumentException e) {
			// nothing to do here : we only catch exception thrown by runnable given to doWithClock
		}
		
		assertThat((double) LocalDateTime.now().getNano()).isCloseTo(Dates.now().getNano(), offset(10_000_000d));	// almost equal with 10ms gap
	}
	
	@Test
	void runAtTime() {
		LocalDateTime runtime = LocalDateTime.of(2018, Month.DECEMBER, 10, 15, 50);
		LocalDateTime[] nowAtAtime = new LocalDateTime[1];
		Dates.runAtTime(runtime, () -> nowAtAtime[0] = Dates.now());
		assertThat(nowAtAtime[0]).isEqualTo(runtime);
		
		Dates.runAtTime(runtime, () -> nowAtAtime[0] = Dates.now().plusHours(1));
		assertThat(nowAtAtime[0]).isEqualTo(runtime.plusHours(1));
	}
	
	@Test
	void now() {
		assertThat((double) LocalDateTime.now().getNano()).isCloseTo(Dates.now().getNano(), offset(10_000_000d));	// almost equal with 10ms gap 
	}
	
	@Test
	void nowAsDate() {
		assertThat((double) new Date().getTime()).isCloseTo((double) Dates.nowAsDate().getTime(), offset(10_000_000d));	// almost equal with 10ms gap 
	}
	
	@Test
	void today() {
		assertThat(LocalDate.now()).isEqualTo(Dates.today()); 
	}
}
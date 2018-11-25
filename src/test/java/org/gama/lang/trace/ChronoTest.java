package org.gama.lang.trace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Guillaume Mary
 */
class ChronoTest {
	
	@Test
	void testFormat() {
		assertEquals("1ms", Chrono.format(1));
		assertEquals("1s", Chrono.format(Chrono.MILLIS_MAX));
		assertEquals("1min", Chrono.format(Chrono.SEC_MAX));
		assertEquals("1h", Chrono.format(Chrono.MIN_MAX));
		assertEquals("1d", Chrono.format(Chrono.H_MAX));
		
		// testing omission : minutes are ommitted because there aren't any
		assertEquals("1h 1s", Chrono.format(Chrono.MIN_MAX + Chrono.MILLIS_MAX));
		
		// corner cases
		assertEquals("", Chrono.format(0));
	}
}
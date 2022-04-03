package org.codefilarete.tool.trace;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class ChronoTest {
	
	@Test
	void format() {
		assertThat(Chrono.format(1)).isEqualTo("1ms");
		assertThat(Chrono.format(Chrono.MILLIS_MAX)).isEqualTo("1s");
		assertThat(Chrono.format(Chrono.SEC_MAX)).isEqualTo("1min");
		assertThat(Chrono.format(Chrono.MIN_MAX)).isEqualTo("1h");
		assertThat(Chrono.format(Chrono.H_MAX)).isEqualTo("1d");
		
		// testing omission : minutes are omitted because there aren't any
		assertThat(Chrono.format(Chrono.MIN_MAX + Chrono.MILLIS_MAX)).isEqualTo("1h 1s");
		
		// corner case
		assertThat(Chrono.format(0)).isEqualTo("0ms");
	}
}
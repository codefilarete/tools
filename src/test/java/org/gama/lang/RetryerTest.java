package org.gama.lang;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Guillaume Mary
 */
public class RetryerTest {
	
	@Test
	public void testExecute_neverWorks() throws Throwable {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Throwable t) {
				return true;
			}
		};
		
		final int[] callTimes = new int[1];
		try {
			testInstance.execute(() -> {
				callTimes[0]++;
				throw new RuntimeException("Never works !");
			}, "test");
		} catch (Throwable t) {
			assertThat(t.getMessage()).isEqualTo("Action \"test\" has been executed 3 times every 5ms and always failed");
			assertThat(t.getCause().getMessage()).isEqualTo("Never works !");
		}
		assertThat(callTimes[0]).isEqualTo(3);
	}
	
	@Test
	public void testExecute_worksLastAttempt() throws Throwable {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Throwable t) {
				return true;
			}
		};
		
		final int[] callTimes = new int[1];
		try {
			testInstance.execute(() -> {
				callTimes[0]++;
				if (callTimes[0] < 2) {
					throw new RuntimeException("Never works !");
				}
				return null;
			}, "test");
		} catch (Throwable t) {
			fail("No exception should be thrown");
		}
		assertThat(callTimes[0]).isEqualTo(2);
	}
	
	@Test
	public void testExecute_worksFirstAttempt() throws Throwable {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Throwable t) {
				return true;
			}
		};
		
		final int[] callTimes = new int[1];
		try {
			testInstance.execute(() -> {
				callTimes[0]++;
				return null;
			}, "test");
		} catch (Throwable t) {
			fail("No exception should be thrown");
		}
		assertThat(callTimes[0]).isEqualTo(1);
	}
	
	@Test
	public void testExecute_throwUnexpected() throws Throwable {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Throwable t) {
				return t.getMessage().equals("retry");
			}
		};
		
		final int[] callTimes = new int[1];
		try {
			testInstance.execute(() -> {
				callTimes[0]++;
				if (callTimes[0] < 3) {
					throw new RuntimeException("retry");
				} else {
					throw new RuntimeException("Unepected error");
				}
			}, "test");
		} catch (Throwable t) {
			assertThat(t.getMessage()).isEqualTo("Unepected error");
		}
		assertThat(callTimes[0]).isEqualTo(3);
	}
	
	@Test
	public void testExecute_noRetryAsked() throws Throwable {
		Retryer testInstance = Retryer.NO_RETRY;
		
		final int[] callTimes = new int[1];
		String result = null;
		try {
			result = testInstance.execute(() -> {
				callTimes[0]++;
				return "OK";
			}, "test");
		} catch (Throwable t) {
			fail("No exception should be thrown");
		}
		assertThat(callTimes[0]).isEqualTo(1);
		assertThat(result).isEqualTo("OK");
	}
}
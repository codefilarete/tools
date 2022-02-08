package org.codefilarete.tool;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Guillaume Mary
 */
class RetryerTest {
	
	@Test
	void execute_neverWorks() {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Result result) {
				return result instanceof Failure;
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
	void execute_worksLastAttempt() {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Result result) {
				return result instanceof Failure;
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
	void execute_worksFirstAttempt() {
		Retryer testInstance = new Retryer(3, 5) {
			@Override
			protected boolean shouldRetry(Result result) {
				return result instanceof Failure;
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
	void execute_throwUnexpected() {
		Retryer testInstance = new Retryer(3, 5) {
			
			@Override
			protected boolean shouldRetry(Result result) {
				if (result instanceof Failure) {
					return ((Failure<?>) result).getError().getMessage().equals("retry");
				} else {
					return false;
				}
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
	void execute_noRetryAsked() {
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
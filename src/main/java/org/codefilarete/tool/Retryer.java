package org.codefilarete.tool;

import javax.annotation.Nullable;

import org.codefilarete.tool.function.ThrowingExecutable;

/**
 * Basic implementation of a retryer processor.
 * User has to override {@link #shouldRetry(Result)} method to decide whether a retry must happen or not.
 * If max attempts have been reached then a {@link RetryException} will be thrown.
 * 
 * @author Guillaume Mary
 */
public abstract class Retryer {
	
	public static final Retryer NO_RETRY = new NoRetryer();
	
	private final int maxRetries;
	private final long retryDelay;
	
	/**
	 * Constructor with necessary parameters.
	 * 
	 * @param maxRetries maximum attempts before action is not retried
	 * @param retryDelay delay between each attempt, in millisecond
	 */
	public Retryer(int maxRetries, long retryDelay) {
		this.maxRetries = maxRetries;
		this.retryDelay = retryDelay;
	}
	
	public <T, E extends Throwable> T execute(ThrowingExecutable<T, E> delegate, String description) throws E, RetryException {
		Executor<T, E> executor = new Executor<>(delegate, description);
		return executor.execute();
	}
	
	/**
	 * To be implemented to determine if a retry must happen or not.
	 * Parameter is either of type {@link Success} or {@link Failure} depending on code execution. Therefore, implementor may base its algorithm on
	 * "instanceof" checking, and some more "business" one
	 *
	 * @param result a {@link Success} or {@link Failure} instance containing details of code execution
	 * @return true if a retry must be triggered
	 */
	protected abstract boolean shouldRetry(Result result);
	
	public static class RetryException extends Exception {
		
		public RetryException(String action, int tryCount, long retryDelay, Throwable cause) {
			super("Action \"" + action + "\" has been executed " + tryCount + " times every " + retryDelay + "ms and always failed", cause);
		}
	}
	
	/**
	 * Internal method for execution in order to make "tryCount" thread-safe
	 */
	private final class Executor<R, E extends Throwable> {
		private int tryCount = 0;
		private final ThrowingExecutable<R, E> delegateWithResult;
		private final String description;
		
		private Executor(ThrowingExecutable<R, E> delegateWithResult, String description) {
			this.delegateWithResult = delegateWithResult;
			this.description = description;
		}
		
		@SuppressWarnings("java:S1181" /* Throwable caught voluntarily to let caller handle what he wants */)
		public R execute() throws E, RetryException {
			try {
				tryCount++;
				R result = delegateWithResult.execute();
				if (shouldRetry(new Success<>(result))) {
					return retry(null);
				} else {
					return result;
				}
			} catch (Throwable t) {
				if (shouldRetry(new Failure<>(t))) {
					return retry(t);
				} else {
					throw t;
				}
			}
		}
		
		private R retry(@Nullable Throwable t) throws E, RetryException {
			if (tryCount < maxRetries) {
				waitRetryDelay();
				return execute();
			} else {
				throw new RetryException(description, tryCount, retryDelay, t);
			}
		}
		
		private void waitRetryDelay() {
			try {
				Thread.sleep(retryDelay);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	private static final class NoRetryer extends Retryer {
		public NoRetryer() {
			super(0, 0);
		}
		
		@Override
		protected boolean shouldRetry(Result result) {
			return false;
		}
	}
	
	/**
	 * Super type of {@link Failure} and {@link Success} for {@link Retryer#shouldRetry(Result)} method signature
	 */
	public interface Result {
		
	}
	
	/**
	 * Dedicated holder of exception when an error happen while {@link Retryer} executes caller code
	 *
	 * @param <T> error type
	 */
	public static class Failure<T extends Throwable> implements Result {
		
		private final T error;
		
		public Failure(T error) {
			this.error = error;
		}
		
		public T getError() {
			return error;
		}
	}
	
	/**
	 * Dedicated holder of result when {@link Retryer} successfully executed caller code
	 *
	 * @param <T> error type
	 */
	public static class Success<T> implements Result {
		
		private final T value;
		
		public Success(T value) {
			this.value = value;
		}
		
		public T getValue() {
			return value;
		}
	}
}

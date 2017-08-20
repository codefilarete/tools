package org.gama.lang;

import java.util.function.Supplier;

import org.gama.lang.function.Holder;
import org.gama.lang.function.ThrowingRunnable;
import org.gama.lang.function.ThrowingSupplier;

/**
 * @author Guillaume Mary
 */
public class ThreadLocals {
	
	/**
	 * Runs some code with a {@link ThreadLocal} and cleans it afterwards by removing the instance of the {@link ThreadLocal}
	 * through {@link ThreadLocal#remove()}.
	 * 
	 * @param threadLocal the {@link ThreadLocal} to be used
	 * @param factory the supplier of the instance assigned to {@link ThreadLocal}
	 * @param runnable some code to be run
	 * @param <T> type of bean assigned to the ThreadLocal
	 * @see AutoRemoveThreadLocal
	 */
	public static <T> void doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, Runnable runnable) {
		doWithThreadLocal(threadLocal, factory, (ThrowingRunnable<RuntimeException>) runnable::run);
	}
	
	/**
	 * Runs some code with a {@link ThreadLocal} and cleans it afterwards by removing the instance of the {@link ThreadLocal}
	 * through {@link ThreadLocal#remove()}.
	 *
	 * @param threadLocal the {@link ThreadLocal} to be used
	 * @param factory the supplier of the instance assigned to {@link ThreadLocal}
	 * @param runnable some code to be run
	 * @param <T> type of bean assigned to the ThreadLocal
	 * @see AutoRemoveThreadLocal
	 */
	public static <T, E extends Throwable> void doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, ThrowingRunnable<E> runnable) 
			throws E {
		doWithThreadLocal(threadLocal, factory, (ThrowingSupplier<Void, E>) () -> { runnable.run(); return null; });
	}
	
	/**
	 * Runs some code with a {@link ThreadLocal} and cleans it afterwards by removing the instance of the {@link ThreadLocal}
	 * through {@link ThreadLocal#remove()}.
	 *
	 * @param threadLocal the {@link ThreadLocal} to be used
	 * @param factory the supplier of the instance assigned to {@link ThreadLocal}
	 * @param runnable some code to be run
	 * @param <T> type of bean assigned to the ThreadLocal
	 * @see AutoRemoveThreadLocal
	 */
	public static <T, O> O doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, Supplier<O> runnable) {
		return doWithThreadLocal(threadLocal, factory, (ThrowingSupplier<O, RuntimeException>) runnable::get);
	}
	
	/**
	 * Runs some code with a {@link ThreadLocal} and cleans it afterwards by removing the instance of the {@link ThreadLocal}
	 * through {@link ThreadLocal#remove()}.
	 *
	 * @param threadLocal the {@link ThreadLocal} to be used
	 * @param factory the supplier of the instance assigned to {@link ThreadLocal}
	 * @param runnable some code to be run
	 * @param <T> type of bean assigned to the ThreadLocal
	 * @see AutoRemoveThreadLocal
	 */
	public static <T, O, E extends Throwable> O doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, ThrowingSupplier<O, E> runnable) 
			throws E {
		threadLocal.set(factory.get());
		
		try (AutoRemoveThreadLocal<T> ignored = new AutoRemoveThreadLocal<>(threadLocal)) {
			return runnable.get();
		}
	}
	
	/**
	 * A {@link ThreadLocal} that will be cleared ({@link ThreadLocal#remove()} method called) after usage as a try-with-resource.
	 */
	public static class AutoRemoveThreadLocal<T> implements AutoCloseable, Supplier<T>, Holder<T> {
		
		private final ThreadLocal<T> surrogate;
		
		public AutoRemoveThreadLocal(ThreadLocal<T> surrogate) {
			this.surrogate = surrogate;
		}
		
		@Override
		public T get() {
			return surrogate.get();
		}
		
		@Override
		public void set(T value) {
			surrogate.set(value);
		}
		
		@Override
		public void close() {
			surrogate.remove();
		}
	}
}

package org.codefilarete.tool;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.codefilarete.tool.function.Hanger;
import org.codefilarete.tool.function.ThrowingConsumer;
import org.codefilarete.tool.function.ThrowingFunction;
import org.codefilarete.tool.function.ThrowingRunnable;
import org.codefilarete.tool.function.ThrowingSupplier;

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
		doWithThreadLocal(threadLocal, factory, (ThrowingFunction<T, Void, E>) t -> { runnable.run(); return null; });
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
	public static <T> void doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, Consumer<T> runnable) {
		doWithThreadLocal(threadLocal, factory, (ThrowingConsumer<T, RuntimeException>) runnable::accept);
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
	public static <T, E extends Throwable> void doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, ThrowingConsumer<T, E> runnable) 
			throws E {
		doWithThreadLocal(threadLocal, factory, (ThrowingFunction<T, Void, E>) t -> { runnable.accept(t); return null; });
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
		return doWithThreadLocal(threadLocal, factory, (ThrowingFunction<T, O, E>) t -> runnable.get());
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
	public static <T, O> O doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, Function<T, O> runnable) {
		return doWithThreadLocal(threadLocal, factory, (ThrowingFunction<T, O, RuntimeException>) runnable::apply);
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
	public static <T, O, E extends Throwable> O doWithThreadLocal(ThreadLocal<T> threadLocal, Supplier<T> factory, ThrowingFunction<T, O, E> runnable)
			throws E {
		T value = threadLocal.get();
		if (value == null) {
			value = factory.get();
			threadLocal.set(value);
		}
		
		try (AutoRemoveThreadLocal<T> ignored = new AutoRemoveThreadLocal<>(threadLocal)) {
			return runnable.apply(value);
		}
	}
	
	/**
	 * A {@link ThreadLocal} that will be cleared ({@link ThreadLocal#remove()} method called) after usage as a try-with-resource.
	 */
	public static class AutoRemoveThreadLocal<T> implements AutoCloseable, Supplier<T>, Hanger<T> {
		
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

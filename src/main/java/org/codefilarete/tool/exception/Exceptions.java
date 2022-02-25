package org.codefilarete.tool.exception;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.codefilarete.tool.collection.Iterables;
import org.codefilarete.tool.collection.ReadOnlyIterator;

/**
 * 
 * @author Guillaume Mary 
 */
public interface Exceptions {
	
	/**
	 * Convert a {@link Throwable} into a {@link RuntimeException}. Do nothing if the {@link Throwable} is already a {@link RuntimeException},
	 * else instanciate a {@link RuntimeException} with the exception as the init cause.
	 * <b>Please use with caution because doing this can be considered as a bad practice.</b>
	 *
	 * @param t any kinf of exception
	 * @return the {@link Throwable} itself if it's already a {@link RuntimeException},
	 * 			else a {@link RuntimeException} which cause is the {@link Throwable} argument
	 */
	static RuntimeException asRuntimeException(Throwable t) {
		if (t instanceof RuntimeException) {
			return  (RuntimeException) t;
		} else {
			return new RuntimeException(t);
		}
	}
	
	static <T> T findExceptionInCauses(Throwable t, Class<T> throwableClass) {
		return (T) findExceptionInCauses(t, new ClassExceptionFilter<>(throwableClass));
	}
	
	static <T> T findExceptionInCauses(Throwable t, Class<T> throwableClass, String message) {
		return (T) findExceptionInCauses(t, new ClassAndMessageExceptionFilter<>(throwableClass, message));
	}
	
	static <T> T findExceptionInCauses(Throwable t, Class<T> throwableClass, Predicate<String> messageAccepter) {
		return (T) findExceptionInCauses(t, new ClassAndMessageExceptionFilter<>(throwableClass, messageAccepter));
	}
	
	/**
	 * Look up a {@link Throwable} in the causes hierarchy of the {@link Throwable} argument according to a {@link ExceptionFilter} 
	 *
	 * @param t the initial stack error
	 * @param filter a filter
	 * @return null if not found
	 */
	static Throwable findExceptionInCauses(Throwable t, final ExceptionFilter filter) {
		return Iterables.stream(new ExceptionCauseIterator(t)).filter(filter::accept).findAny().orElse(null);
	}
	
	/**
	 * Iterator on {@link Throwable} causes (and itself)
	 */
	class ExceptionCauseIterator extends ReadOnlyIterator<Throwable> {
		
		private Throwable currentThrowable;
		
		private boolean hasNext = false;
		
		public ExceptionCauseIterator(Throwable throwable) {
			this.currentThrowable = throwable;
		}
		
		@Override
		public boolean hasNext() {
			return hasNext = currentThrowable != null;
		}
		
		@Override
		public Throwable next() {
			if (!hasNext) {
				// this is necessary to be compliant with Iterator#next(..) contract
				throw new NoSuchElementException();
			}
			Throwable next = currentThrowable;
			currentThrowable = currentThrowable.getCause();
			return next;
		}
	}
	
	interface ExceptionFilter {
		boolean accept(Throwable t);
	}
	
	class ClassExceptionFilter<T> implements ExceptionFilter {
		
		private final Class<T> targetClass;
		
		public ClassExceptionFilter(Class<T> c) {
			this.targetClass = c;
		}
		
		public boolean accept(Throwable t) {
			return targetClass.isAssignableFrom(t.getClass());
		}
	}
	
	class ClassAndMessageExceptionFilter<T> extends ClassExceptionFilter<T> {
		
		private final Predicate<String> messageAccepter;
		
		public ClassAndMessageExceptionFilter(Class<T> c, String message) {
			super(c);
			this.messageAccepter = message::equalsIgnoreCase;
		}
		
		public ClassAndMessageExceptionFilter(Class<T> c, Predicate<String> messageAccepter) {
			super(c);
			this.messageAccepter = messageAccepter;
		}
		
		@Override
		public boolean accept(Throwable t) {
			return super.accept(t) && this.messageAccepter.test(t.getMessage());
		}
	}
	
}

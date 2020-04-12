package org.gama.lang.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.gama.lang.Duo;
import org.gama.lang.collection.Iterables;
import org.gama.lang.collection.PairIterator;
import org.gama.lang.exception.Exceptions;
import org.gama.lang.function.Predicates;
import org.gama.lang.function.ThrowingRunnable;
import org.gama.lang.reflect.MemberPrinter;
import org.junit.platform.commons.util.StringUtils;
import org.opentest4j.AssertionFailedError;

import static org.gama.lang.Nullable.nullable;

/**
 * Assertions class.
 * Seems quite redundant with JUnit assertions class, and it's true, but since I mainly use assertEquals(..) which lacks some simple cases (such
 * as comparing Long without casting ...) or assertThrows(..) which can't check hierarchy, I decided to try to make my own (simple) class.
 * Quite experimental.
 * 
 * @author Guillaume Mary
 */
public class Assertions {
	
	/**
	 * <em>Asserts</em> that {@code expected} and {@code actual} are equal.
	 * <p>If both are {@code null}, they are considered equal.
	 * 
	 * @see Object#equals(Object)
	 */
	public static <E> void assertEquals(E expected, E actual) {
		assertPredicate(new ExpectedPredicate<E, E>(expected, Predicates::equalOrNull) {
			@Override
			public E giveExpectedFromActual(E actual) {
				return actual;
			}
		}, actual);
	}
	
	public static <E, M> void assertEquals(E expected, E actual, Function<E, M> mapper) {
		assertPredicate(new ExpectedPredicate<M, M>(mapper.apply(expected), Predicates::equalOrNull, new FailureMessageBuilder() {
			@Override
			protected String build(Duo<String, String> messageParameters) {
				return super.build(new Duo<>(wrap(toString(mapper.apply(expected))), wrap(toString(mapper.apply(actual)))));
			}
		}) {

			@Override
			public M giveExpectedFromActual(M actual) {
				return actual;
			}
		}, mapper.apply(actual));
	}
	
	public static <E> void assertEquals(E expected, E actual, BiPredicate<E, E> predicate) {
		assertPredicate(new ExpectedPredicate<E, E>(expected, predicate, new FailureMessageBuilder()) {
			
			@Override
			public E giveExpectedFromActual(E actual) {
				return actual;
			}
		}, actual);
	}
	
	public static <E> void assertEquals(E expected, E actual, BiPredicate<E, E> predicate, Function<E, String> printingFunction) {
		assertPredicate(new ExpectedPredicate<E, E>(expected, predicate, new FailureMessageBuilder() {
			@Override
			protected String build(Duo<String, String> messageParameters) {
				return super.build(new Duo<>(printingFunction.apply(expected), printingFunction.apply(actual)));
			}
		}) {
			
			@Override
			public E giveExpectedFromActual(E actual) {
				return actual;
			}
		}, actual);
	}
	
	public static <E> void assertEquals(E expected, E actual, Comparator<E> comparator) {
		assertPredicate(new ExpectedPredicate<E, E>(expected, (e, a) -> comparator.compare(e, a) == 0, new FailureMessageBuilder()) {
			
			@Override
			public E giveExpectedFromActual(E actual) {
				return actual;
			}
		}, actual);
	}
	
	public static <E, M> void assertEquals(E expected, E actual, Function<E, M> mapper, BiPredicate<M, M> predicate) {
		assertPredicate(new ExpectedPredicate<E, E>(expected, (e, a) -> predicate.test(mapper.apply(e), mapper.apply(a)), new FailureMessageBuilder() {
			@Override
			protected String build(Duo<String, String> messageParameters) {
				return super.build(new Duo<>(wrap(toString(mapper.apply(expected))), wrap(toString(mapper.apply(actual)))));
			}
		}) {
			
			@Override
			public E giveExpectedFromActual(E actual) {
				return actual;
			}
		}, actual);
	}
	
	private static <E, EXP> void assertPredicate(ExpectedPredicate<E, EXP> predicate, E actual) {
		if (!predicate.test(actual)) {
			throw new AssertionFailedError(predicate.giveMessage(actual), predicate.getExpected(), predicate.giveExpectedFromActual(actual));
		}
	}
	
	public static <E> void assertAllEquals(Iterable<E> expected, Iterable<E> actual) {
		IterableTester<E, E> iterableTester = new IterableTester<>(Predicates::equalOrNull);
		boolean areEquals = iterableTester.test(expected, actual);
		if (!areEquals) {
			FailureMessageBuilder failureMessageBuilder = new FailureMessageBuilder();
			String failureMessage = failureMessageBuilder.build(expected, actual);
			throw new AssertionFailedError(failureMessage, expected, actual);
		}
	}
	
	public static <E, M> void assertAllEquals(Iterable<E> expected, Iterable<E> actual, Function<E, M> mapper) {
		final List<M> input = Iterables.collectToList(expected, mapper);
		assertPredicate(new ExpectedPredicate<Iterable<M>, Iterable<M>>(input,
				Predicates::equalOrNull,
				new FailureMessageBuilder() {
					@Override
					protected String build(Duo<String, String> messageParameters) {
						return super.build(new Duo<>(wrap(input.toString()),
								wrap(Iterables.collectToList(actual, mapper).toString())));
					}
				}) {
			
			@Override
			public Iterable<M> giveExpectedFromActual(Iterable<M> actual) {
				return actual;
			}
		}, Iterables.collectToList(actual, mapper));
	}
	
	public static <A, B> void assertAllEquals(Iterable<A> expected, Iterable<B> actual, BiPredicate<A, B> biPredicate) {
		IterableTester<A, B> iterableTester = new IterableTester<>(biPredicate);
		boolean areEquals = iterableTester.test(expected, actual);
		if (!areEquals) {
			FailureMessageBuilder failureMessageBuilder = new FailureMessageBuilder();
			String failureMessage = failureMessageBuilder.build(expected, actual);
			throw new AssertionFailedError(failureMessage, expected, actual);
		}
	}
	
	public static <A, B, M> void assertAllEquals(Iterable<A> expected, Iterable<B> actual, Function<A, M> mapperForAs, Function<B, M> mapperForBs) {
		List<M> as = new ArrayList<>();
		List<M> bs = new ArrayList<>();
		IterableTester<A, B> iterableTester = new IterableTester<>((a, b) -> {
			M mappedA = mapperForAs.apply(a);
			as.add(mappedA);
			M mappedB = mapperForBs.apply(b);
			bs.add(mappedB);
			return Predicates.equalOrNull(mappedA, mappedB);
		});
		if (!iterableTester.test(expected, actual)) {
			FailureMessageBuilder failureMessageBuilder = new FailureMessageBuilder();
			String failureMessage = failureMessageBuilder.build(expected, actual);
			throw new AssertionFailedError(failureMessage, as, bs);
		}
	}
	
	public static <E extends Throwable> void assertThrows(ThrowingRunnable<E> executable, Predicate<Throwable> throwablePredicate) {
		try {
			executable.run();
		} catch (Throwable actualException) {
			if (!throwablePredicate.test(actualException)) {
				Object expected = null;
				Object expectedFromActual = null;
				if (throwablePredicate instanceof ExpectationPredicate) {
					expected = ((ExpectationPredicate) throwablePredicate).getExpected();
					expectedFromActual = ((ExpectationPredicate) throwablePredicate).giveExpectedFromActual(actualException);
				}
				String message = "Unexpected exception thrown";
				if (throwablePredicate instanceof PrintablePredicate) {
					message = ((PrintablePredicate<Throwable>) throwablePredicate).giveMessage(actualException);
				}
				throw new AssertionFailedError(message, expected, expectedFromActual, actualException);
			} else {
				return;
			}
		}
		throw new AssertionFailedError("Expected exception to be thrown, but nothing was thrown.");
	}
	
	private static void failNotEqual(Object expected, Object actual) {
		throw new AssertionFailedError(null, expected, actual);
	}
	
	/**
	 * Must be implemented by {@link Predicate}s that have an argument 
	 * @param <E> {@link Predicate}'s input type
	 * @param <EXP> expectation type
	 */
	public interface ExpectationPredicate<E, EXP> extends Predicate<E> {
		
		EXP getExpected();
		
		EXP giveExpectedFromActual(E actual);
	}
	
	/**
	 * Must be implemented by {@link Predicate}s that have a message : will be in the {@link AssertionFailedError} message
	 * @param <E> {@link Predicate}'s input type
	 */
	public interface PrintablePredicate<E> {
		
		String giveMessage(E actual);
	}
	
	/**
	 * Must be implemented by {@link Predicate}s that has a temporary result which could be check by chaining it with another {@link Predicate}
	 * @param <E> the predicate orginal tested type
	 * @param <P> type of the emitted result 
	 */
	public interface ProjectingPredicate<E, P> {
		
		<EXP> Predicate<E> andProjection(ExpectationPredicate<P, EXP> projectionPredicate);
		
		P getProjection();
	}
	
	private static abstract class ExpectedPredicate<E, EXP> implements PrintablePredicate<E>, ExpectationPredicate<E, EXP> {
		
		private final EXP expectation;
		private final BiPredicate<EXP, E> predicate;
		private final FailureMessageBuilder messageBuilder;
		
		public ExpectedPredicate(EXP expectation, BiPredicate<EXP, E> predicate) {
			this(expectation, predicate, new FailureMessageBuilder());
		}
		
		public ExpectedPredicate(EXP expectation, BiPredicate<EXP, E> predicate, FailureMessageBuilder messageBuilder) {
			this.expectation = expectation;
			this.predicate = predicate;
			this.messageBuilder = messageBuilder;
		}
		
		@Override
		public String giveMessage(E actual) {
			return messageBuilder.build(getExpected(), giveExpectedFromActual(actual));
		}
		
		@Override
		public EXP getExpected() {
			return expectation;
		}
		
		@Override
		public boolean test(E e) {
			return predicate.test(getExpected(), e);
		}
	}
	
	public static <T extends Throwable, X extends ExpectationPredicate<T, String> & PrintablePredicate<T>> X hasMessage(String message) {
		return (X) new ExpectedPredicate<T, String>(message, (expectedMessage, actualException) -> expectedMessage.equals(actualException.getMessage())) {
			
			@Override
			public String giveMessage(T actual) {
				return "Messages are different";
			}
			
			@Override
			public String giveExpectedFromActual(T actual) {
				return actual.getMessage();
			}
		};
	}
	
	public static <T extends Throwable, X extends ExpectationPredicate<Throwable, Class<T>> & PrintablePredicate<Throwable> & ProjectingPredicate<Throwable, T>>
	X hasExceptionInCauses(Class<T> throwableClass) {
		return (X) new ThrowableClassExpectedPredicate<>(throwableClass);
	}
	
	private static class ThrowableClassExpectedPredicate<T extends Throwable> extends ExpectedPredicate<Throwable, Class<T>>
			implements ProjectingPredicate<Throwable, T> {
		
		private T projection;
		
		public ThrowableClassExpectedPredicate(Class<T> throwableClass) {
			super(throwableClass, null);	// null because we override test(..) method because we can't refer this.projection is constructor
		}
		
		@Override
		public Class<T> giveExpectedFromActual(Throwable actual) {
			return (Class<T>) actual.getClass();
		}
		
		@Override
		public String giveMessage(Throwable actual) {
			return "Types are different";
		}
		
		@Override
		public boolean test(Throwable throwable) {
			T foundException = Exceptions.findExceptionInCauses(throwable, getExpected());
			if (foundException != null) {
				this.projection = foundException;
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public <EXP> Predicate<Throwable> andProjection(ExpectationPredicate<T, EXP> projectionPredicate) {
			return new ProjectingPredicateSupport(this, projectionPredicate);
		}
		
		@Override
		public T getProjection() {
			return projection;
		}
	}
	
	/**
	 * Class that combines a first predicate that emit a temporary result which is then tested by another one
	 * @param <T>
	 */
	private static class ProjectingPredicateSupport<T extends Throwable> implements ExpectationPredicate<Throwable, Object>, PrintablePredicate<Throwable> {
		
		/** The source predicate */
		private final ExpectationPredicate<Throwable, ?> projectingPredicate;
		/** The secondary predicate */
		private final ExpectationPredicate<T, ?> projectionPredicate;
		/** Indicator that the first predicate matched, or not */
		private boolean firstTestMatched;
		
		public <
				X extends ExpectationPredicate<Throwable, ?> & ProjectingPredicate<Throwable, T> & PrintablePredicate<Throwable>,
				Y extends ExpectationPredicate<T, ?> & ProjectingPredicate<Throwable, T> & PrintablePredicate<Throwable>
				>
		ProjectingPredicateSupport(X projectingPredicate, Y projectionPredicate) {
			this.projectingPredicate = projectingPredicate;
			this.projectionPredicate = projectionPredicate;
		}
		
		@Override
		public boolean test(Throwable throwable) {
			firstTestMatched = projectingPredicate.test(throwable);
			if (firstTestMatched) {
				return projectionPredicate.test(getProjection());
			} else {
				return false;
			}
		}
		
		private T getProjection() {
			// NB : can be casted because constructor ensures it
			return ((ProjectingPredicate<Throwable, T>) projectingPredicate).getProjection();
		}
		
		@Override
		public Object getExpected() {
			if (!firstTestMatched) {
				return projectingPredicate.getExpected();
			} else {
				return projectionPredicate.getExpected();
			}
		}
		
		@Override
		public Object giveExpectedFromActual(Throwable actual) {
			if (!firstTestMatched) {
				return projectingPredicate.giveExpectedFromActual(actual);
			} else {
				return projectionPredicate.giveExpectedFromActual(getProjection());
			}
		}
		
		@Override
		public String giveMessage(Throwable actual) {
			if (!firstTestMatched) {
				// NB : can be casted because constructor ensures it
				return ((PrintablePredicate<Throwable>) projectingPredicate).giveMessage(actual);
			} else {
				// NB : can be casted because constructor ensures it
				return ((PrintablePredicate<Throwable>) projectionPredicate).giveMessage(getProjection());
			}
		}
	}
	
	private static class FailureMessageBuilder {
		
		public String build(Object expected, Object actual) {
			String expectedString = toString(expected);
			String actualString = toString(actual);
			Duo<String, String> messageParameters;
			if (expectedString.equals(actualString)) {
				// undistinguishable objects by their String => adding class + hashcode distinction
				messageParameters = new Duo<>(formatClassAndValue(expected, expectedString), formatClassAndValue(actual, actualString));
			} else {
				messageParameters = new Duo<>(wrap(expectedString), wrap(actualString));
			}
			return build(messageParameters);
		}
		
		protected String build(Duo<String, String> messageParameters) {
			return "expected: " + messageParameters.getLeft() + " but was: " + messageParameters.getRight();
		}
		
		protected String wrap(String value) {
			return "<" + value + ">";
		}
		
		protected String formatClassAndValue(Object value, String valueString) {
			String classAndHash = nullable(value).map(Assertions::systemToString).getOr("null");
			// if it's a class, there's no need to repeat the class name contained in the valueString.
			return value instanceof Class
					? wrap(classAndHash)
					: classAndHash + wrap(valueString);
		}
		
		protected String toString(Object obj) {
			if (obj instanceof Class) {
				// we enforce full package print instead of relying on Reflections.toString(..)
				// because JUnit prints like this and we want to be close to it
				return MemberPrinter.FULL_PACKAGE_PRINTER.toString((Class<?>) obj);
			} else {
				return StringUtils.nullSafeToString(obj);
			}
		}
		
	}
	
	/**
	 * @param o any non null {@link Object}
	 * @return the original, default JDK implementation, result of the {@link Object#toString()} method
	 */
	static String systemToString(Object o) {
		return o.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(o));
	}
	
	private static class IterableTester<A, B> {
		private BiPredicate<A, B> biPredicate;
		
		public IterableTester(BiPredicate<A, B> biPredicate) {
			this.biPredicate = biPredicate;
		}
		
		public boolean test(Iterable<A> expected, Iterable<B> actual) {
			if (expected == null && actual == null) return true;
			boolean areEquals = true;
			PairIterator<A, B> duoIterator = new PairIterator<>(expected, actual);
			while (duoIterator.hasNext()) {
				Duo<A, B> next = duoIterator.next();
				areEquals &= biPredicate.test(next.getLeft(), next.getRight());
			}
			return areEquals;
		}
	}
}

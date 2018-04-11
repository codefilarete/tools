package org.gama.lang.reflect;

import java.sql.SQLException;
import java.util.function.Supplier;

import org.gama.lang.function.Hanger;
import org.gama.lang.trace.IncrementableInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
public class MethodDispatcherTest {
	
	@Test
	public void testBuild_defaultCase() {
		IncrementableInt hanger = new IncrementableInt();
		Holder1 testInstance = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !")
				.redirect(Hanger.class, (Hanger<Integer>) hanger::increment)
				.fallbackOn(666)
				.build(Holder1.class);
		
		assertEquals("Hello world !", testInstance.get());
		testInstance.set(42);
		assertEquals(42, hanger.getValue());
		assertEquals("666", testInstance.toString());
	}
	
	@Test
	public void testBuild_noFallbackInstance_toStringReflectBuiltClass() {
		MethodDispatcher methodDispatcher = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !");
		
		methodDispatcher.fallbackOn(null);
		Holder1 builtInstance = methodDispatcher.build(Holder1.class);
		assertTrue(builtInstance.toString().contains(Holder1.class.getName()));
	}
	
	@Test
	public void testBuild_noFallbackInstance_throwsException2() throws Exception {
		Holder3 testInstance = new MethodDispatcher()
				.redirect(AutoCloseable.class, () -> { throw new SQLException(); })
				.build(Holder3.class);
		
		Throwable thrownThrowable = null;
		try {
			testInstance.close();
		} catch (Throwable e) {
			thrownThrowable = e;
		}

		assertTrue(thrownThrowable instanceof SQLException);
	}
	
	
	@Test
	public void testWhenInvokedMethodThrowsAnException_exceptionMustBeThrownWithoutWrapping() {
		IncrementableInt hanger = new IncrementableInt();
		Holder2 testInstance = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !")
				// this will produce ClassCastException because Holder2 implements Hanger<String> whereas we used Hanger accepts Integer
				.redirect(Hanger.class, (Hanger<Integer>) hanger::increment)
				.build(Holder2.class);
		
		assertEquals("Hello world !", testInstance.get());
		Throwable thrownThrowable = null;
		try {
			// This can't be successfull because hanger instance expects an Integer so it will throw a ClassCastException
			// This test checks that the exception is not wrapped in a UndeclaredThrowableException or InvocationTargetException
			testInstance.set("sqds");
		} catch (ClassCastException e) {
			thrownThrowable = e;
		}
		
		assertEquals("java.lang.String cannot be cast to java.lang.Integer", thrownThrowable.getMessage());
	}
	
	@Test
	public void testWhenFallbackInstanceDoesntSupportInvokedMethod_exceptionMustBeThrown() {
		Holder2 testInstance = new MethodDispatcher()
				.fallbackOn("toto")
				.build(Holder2.class);
		
		Throwable thrownThrowable = null;
		try {
			// Object is not an instance of declaring class: expected java.util.function.Supplier but was java.lang.String
			// because "toto" doesn't implement get()
			testInstance.get();
		} catch (IllegalArgumentException e) {
			thrownThrowable = e;
		}
		
		assertTrue(thrownThrowable.getMessage().contains("object is not an instance of declaring class"));
	}
	
	@Test
	public void testBuild_targetClassDoesntImplementRedirectingClasses_exceptionIsThrown() {
		MethodDispatcher methodDispatcher = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !");
		Throwable thrownThrowable = null;
		try {
			// Exception is thrown here, else we could do successfully
			// Method get = Reflections.findMethod(Supplier.class, "get");
			// assertEquals("Hello world !", get.invoke(testInstance));
			methodDispatcher.build(CharSequence.class);
		} catch (IllegalArgumentException e) {
			thrownThrowable = e;
		}
		
		assertTrue(thrownThrowable.getMessage().contains(" doesn't implement "));
	}
	
	@Test
	public void testRedirect_returnTypesAreNotThoseOfInterface_exceptionIsThrown() {
		DummyFluentInterface testInstance = new MethodDispatcher()
				.redirect(SubclassAwareFluentInterface.class, new FluentInterfaceSupport())
				.build(DummyFluentInterface.class);
		
		Throwable thrownThrowable = null;
		try {
			// This won't work because FluentInterfaceSupport returns itself which doesn't match DummyFluentInterface : they are dissociated subclasses.
			testInstance.doSomething().doSomethingElse();
		} catch (ClassCastException e) {
			thrownThrowable = e;
		}
		assertTrue(thrownThrowable.getMessage().equals("org.gama.lang.reflect.MethodDispatcherTest$FluentInterfaceSupport cannot be cast" +
				" to org.gama.lang.reflect.MethodDispatcherTest$DummyFluentInterface"));
	}
	
	@Test
	public void testRedirect_returnTypesAreNotThoseOfInterfaceButReturnProxyIsAsked() {
		DummyFluentInterface testInstance = new MethodDispatcher()
				.redirect(SubclassAwareFluentInterface.class, new FluentInterfaceSupport(), true)
				.build(DummyFluentInterface.class);
		
		// This will work because we ask to return the proxy after FluentInterfaceSupport invocations
		testInstance.doSomething().doSomethingElse();
	}
	
	@Test
	public void test_returnTypesAreNotThoseOfInterfaceButReturnProxyIsAsked() {
		MethodDispatcher testInstance = new MethodDispatcher()
				.redirect(String.class, "abc");
		
		Throwable thrownThrowable = null;
		try {
			// This won't work because FluentInterfaceSupport returns itself which doesn't match DummyFluentInterface : they are dissociated subclasses.
			testInstance.build(CharSequence.class);
		} catch (IllegalArgumentException e) {
			thrownThrowable = e;
		}
		assertTrue(thrownThrowable.getMessage().contains("Cannot intercept concrete method"));
	}
	
	private interface Holder1 extends Supplier<String>, Hanger<Integer> {
		
	}
	
	private interface Holder2 extends Supplier<String>, Hanger<String> {
		
	}
	
	private interface Holder3 extends AutoCloseable {
		
	}
	
	/**
	 * We define an interface that takes into account subclasses by returning them on default methods, hence their methods can be chained on them too.
	 * @param <Z> a subclass type
	 */
	private interface SubclassAwareFluentInterface<Z extends SubclassAwareFluentInterface<Z>> {
		Z doSomething();
		
		Z doSomethingElse();
	}
	
	private interface DummyFluentInterface extends SubclassAwareFluentInterface<DummyFluentInterface> {
		// For our test case we should not declare again SubclassAwareFluentInterface methods with DummyFluentInterface return type,
		// because they will be invoked in priority whereas we want SubclassAwareFluentInterface methods to be invoked
		/** A non necessary method just to demonstrate the test case */
		DummyFluentInterface doSpecialThing();
	}
	
	/**
	 * This is a default implementation of an interface, it holds default behavior
	 */
	private static class FluentInterfaceSupport implements SubclassAwareFluentInterface {
		@Override
		public SubclassAwareFluentInterface doSomething() {
			return this;
		}
		
		@Override
		public SubclassAwareFluentInterface doSomethingElse() {
			return this;
		}
	}
}
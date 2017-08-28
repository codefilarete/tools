package org.gama.lang.reflect;

import java.sql.SQLException;
import java.util.function.Supplier;

import org.gama.lang.function.Hanger;
import org.gama.lang.trace.IncrementableInt;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	public void testBuild_noFallbackInstance_throwsException() {
		MethodDispatcher methodDispatcher = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !");
		
		Throwable thrownThrowable = null;
		try {
			methodDispatcher.build(Holder1.class);
		} catch (IllegalArgumentException e) {
			thrownThrowable = e;
		}
		
		assertTrue(thrownThrowable.getMessage().contains("Fallback instance must not be null"));
	}
	
	@Test
	public void testBuild_noFallbackInstance_throwsException2() throws Exception {
		Holder3 testInstance = new MethodDispatcher()
				.redirect(AutoCloseable.class, () -> { throw new SQLException(); })
				.fallbackOn(666)
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
				.fallbackOn("fd")
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
				.redirect(Supplier.class, () -> "Hello world !")
				.fallbackOn(new Object());
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
	
	private interface Holder1 extends Supplier<String>, Hanger<Integer> {
		
	}
	
	private interface Holder2 extends Supplier<String>, Hanger<String> {
		
	}
	
	private interface Holder3 extends AutoCloseable {
		
	}
	
}
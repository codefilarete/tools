package org.codefilarete.tool.reflect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.codefilarete.tool.function.Hanger;
import org.codefilarete.tool.function.Hanger.Holder;
import org.codefilarete.tool.reflect.MethodDispatcher.WrongTypeReturnedException;
import org.codefilarete.tool.trace.ModifiableInt;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Guillaume Mary
 */
class MethodDispatcherTest {
	
	@Test
	void redirect_interface() {
		IntegerHanger surrogate = new IntegerHanger();
		Holder1 testInstance = new MethodDispatcher()
				.redirect(Hanger.class, surrogate)
				.fallbackOn(666)
				.build(Holder1.class);
		
		testInstance.set(42);
		assertThat(surrogate.getValue()).isEqualTo(42);
		assertThat(testInstance).hasToString("Dispatcher to 666");
	}
	
	@Test
	void redirect_interfaceToObject_allMethodResultAreRedirectedToObject() {
		List<String> resultHolder = new ArrayList<>();
		
		EntryPoint surrogate1 = new EntryPoint() {
			@Override
			public NextStep doSomething() {
				resultHolder.add("something done");
				return null;
			}
			
			@Override
			public NextStep doSomethingElse() {
				resultHolder.add("something else done");
				return null;
			}
		};
		NextStep surrogate2 = new NextStep() {
			@Override
			public void doStepThing() {
				resultHolder.add("step thing done");
			}
		};
		EntryPoint testInstance = new MethodDispatcher()
				.redirect(EntryPoint.class, surrogate1, surrogate2)
				.build(EntryPoint.class);
		
		testInstance.doSomething().doStepThing();
		assertThat(resultHolder).containsExactly("something done", "step thing done");
	}
	
	@Test
	void build_noFallbackInstance_callingUnredirectedMethodThrowsException() {
		Holder2 testInstance = new MethodDispatcher()
				.redirect(Hanger.class, new Holder<>())
				.build(Holder2.class);

		assertThatThrownBy(testInstance::get)
				.isInstanceOf(NullPointerException.class)
				.hasMessage("No fallback instance was declared, therefore calling j.u.f.Supplier.get() would throw NullPointerException:" 
						+ " try to set one or redirect given method to a compatible instance");
	}
	
	@Test
	void redirect_whenInvokedMethodThrowsAnException_exceptionMustBeThrownWithoutWrapping() {
		ModifiableInt hanger = new ModifiableInt();
		Holder2 testInstance = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !")
				// this will produce ClassCastException because Holder2 implements Hanger<String> whereas we used Hanger accepts Integer
				.redirect(Hanger.class, (Hanger<Integer>) hanger::increment)
				.build(Holder2.class);
		
		assertThat(testInstance.get()).isEqualTo("Hello world !");
		assertThatThrownBy(() ->
				// This can't be successful because hanger instance expects an Integer so it will throw a ClassCastException
				// This test checks that the exception is not wrapped in a UndeclaredThrowableException or InvocationTargetException
				testInstance.set("sqds"))
				.isInstanceOf(WrongTypeReturnedException.class)
				.hasMessage("Code handling o.c.t.f.Hanger.set(j.l.Object) is expected to return a j.l.Integer but returned a j.l.String");
	}
	
	@Test
	void redirect_whenFallbackInstanceDoesntSupportInvokedMethod_exceptionMustBeThrown() {
		Holder2 testInstance = new MethodDispatcher()
				.fallbackOn("toto")
				.build(Holder2.class);
		
		// Object is not an instance of declaring class: expected java.util.function.Supplier but was java.lang.String
		// because "toto" doesn't implement get()
		assertThatThrownBy(testInstance::get)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Wrong given instance");
	}
	
	@Test
	void redirect_targetClassDoesntImplementRedirectingClasses_exceptionIsThrown() {
		MethodDispatcher methodDispatcher = new MethodDispatcher()
				.redirect(Supplier.class, () -> "Hello world !");
		
		// Exception is thrown here, else we could do successfully
		// Method get = Reflections.findMethod(Supplier.class, "get");
		// assertEquals("Hello world !", get.invoke(testInstance));
		assertThatThrownBy(() -> methodDispatcher.build(CharSequence.class))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining(" doesn't implement ");
	}
	
	@Test
	void redirect_returnTypesAreNotThoseOfInterface_returnProxyIsAsked_works() {
		DummyFluentInterface testInstance = new MethodDispatcher()
				.redirect(SubclassAwareFluentInterface.class, new FluentInterfaceSupport(), true)
				.build(DummyFluentInterface.class);
		
		// This will work because we ask to return the proxy after FluentInterfaceSupport invocations
		assertThatCode(() -> testInstance.doSomething().doSomethingElse()).doesNotThrowAnyException();
	}
	
	@Test
	void redirect_returnTypesAreNotThoseOfInterface_returnProxyIsNotAsked_throwsException() {
		DummyFluentInterface testInstance = new MethodDispatcher()
				.redirect(SubclassAwareFluentInterface.class, new FluentInterfaceSupport())
				.build(DummyFluentInterface.class);
		
		// This won't work because FluentInterfaceSupport returns itself which doesn't match DummyFluentInterface : they are dissociated subclasses.
		assertThatThrownBy(() -> testInstance.doSomething().doSomethingElse())
				.isInstanceOf(java.lang.ClassCastException.class)
				.hasMessage("org.codefilarete.tool.reflect.MethodDispatcherTest$FluentInterfaceSupport cannot be cast"
						+ " to org.codefilarete.tool.reflect.MethodDispatcherTest$DummyFluentInterface");
	}
	
	@Test
	void redirect_proxyHasMultilpleInheritanceWithRefinedType() {
		SubclassNotAwareFluentInterfaceSupport testInstance = new MethodDispatcher()
				.redirect(SubSubclassNotAwareFluentInterface.class, new SubSubclassNotAwareFluentInterface() {
					@Override
					public SubSubclassNotAwareFluentInterface doSomething() {
						return null;
					}
					
					@Override
					public SubSubclassNotAwareFluentInterface doSomethingElse() {
						return null;
					}
					
					@Override
					public SubSubclassNotAwareFluentInterface doEvenMore() {
						return null;
					}
				}, true)
				.build(SubclassNotAwareFluentInterfaceSupport.class);
		
		// This will work because we ask to return the proxy after FluentInterfaceSupport invocations
		assertThatCode(() -> testInstance.doSomething().doSomethingElse().doEvenMore()).doesNotThrowAnyException();
	}
	
	@Test
	void redirect_proxyHasMultilpleInheritanceWithRefinedType2() {
		MultipleInheritanceTestSupport testInstance = new MethodDispatcher()
				.redirect(SubclassNotAwareFluentInterface.class, new SubclassNotAwareFluentInterface() {
					@Override
					public SubclassNotAwareFluentInterface doSomething() {
						return null;
					}
					
					@Override
					public SubclassNotAwareFluentInterface doSomethingElse() {
						return null;
					}
				}, true)
				.redirect(ASimpleContract.class, () -> {
					
				}, true)
				.build(MultipleInheritanceTestSupport.class);
		
		// This will work because we ask to return the proxy after FluentInterfaceSupport invocations
		assertThatCode(() -> testInstance.doSomething().simpleContractMethod()).doesNotThrowAnyException();
	}
	
	@Test
	void redirect_givenClassHasSomeMethodsOutOfInterfaceScope_throwsException() {
		MethodDispatcher testInstance = new MethodDispatcher()
				.redirect(String.class, "abc");
		
		// Concerete methods can't be intercepted by Java proxy, so an exception is raised to say "this is not implementable"
		// This could be enhance by throwing it right after redirect(..) call
		assertThatThrownBy(() -> testInstance.build(CharSequence.class))
				.isInstanceOf(UnsupportedOperationException.class)
				.hasMessageContaining("Cannot intercept concrete method");
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
	
	/**
	 * An interface which methods strongly types their return, at the opposit of {@link SubclassAwareFluentInterface} 
	 */
	private interface SubclassNotAwareFluentInterface {
		
		SubclassNotAwareFluentInterface doSomething();
		
		SubclassNotAwareFluentInterface doSomethingElse();
	}
	
	/**
	 * This is the same as {@link SubclassNotAwareFluentInterface} but with refined return types
	 */
	private interface SubSubclassNotAwareFluentInterface extends SubclassNotAwareFluentInterface {
		
		@Override
		SubSubclassNotAwareFluentInterface doSomething();
		
		@Override
		SubSubclassNotAwareFluentInterface doSomethingElse();
		
		SubSubclassNotAwareFluentInterface doEvenMore();
	}
	
	/**
	 * An interface which inherits from 2 close interfaces
	 */
	private interface SubclassNotAwareFluentInterfaceSupport extends SubclassNotAwareFluentInterface, SubSubclassNotAwareFluentInterface, ASimpleContract {
		
	}
	
	private interface ASimpleContract {
		
		void simpleContractMethod();
	}
	
	private interface MultipleInheritanceTestSupport extends SubclassNotAwareFluentInterface, ASimpleContract {
		@Override
		MultipleInheritanceTestSupport doSomething();
		
		@Override
		MultipleInheritanceTestSupport doSomethingElse();
		
	}
	
	private static class IntegerHanger implements Hanger<Integer> {
		
		private final ModifiableInt hanger = new ModifiableInt();
		
		
		@Override
		public void set(Integer value) {
			hanger.increment(value);
		}
		
		public int getValue() {
			return hanger.getValue();
		}
	}
	
	private interface EntryPoint {
		
		NextStep doSomething();
		
		NextStep doSomethingElse();
	}
	
	private interface NextStep {
		
		void doStepThing();
	}
}
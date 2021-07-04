package org.gama.lang;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import org.gama.lang.InvocationHandlerSupport.DefaultPrimitiveValueInvocationProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
public class InvocationHandlerSupportTest {
	
	public static Object[][] methodReturningPrimitiveTypesProvider() {
		return new Object[][] {
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getBoolean"), false },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getChar"), '\u0000' },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getByte"), (byte) 0 },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getShort"), (short) 0 },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getInt"), 0 },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getLong"), (long) 0 },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getFloat"), (float) 0.0 },
				{ Reflections.findMethod(AllPrimitiveTypesMethods.class, "getDouble"), 0.0 },
				// non primitive case to check primitive type detection
				{ Reflections.findMethod(Object.class, "toString"), null },
		};
	}
	
	@ParameterizedTest
	@MethodSource("methodReturningPrimitiveTypesProvider")
	public void testInvokeMethod_methodReturnsPrimitiveType_defaultPrimitiveValueIsReturned(Method method, Object expectedValue) throws Throwable {
		DefaultPrimitiveValueInvocationProvider testInstance = InvocationHandlerSupport.PRIMITIVE_INVOCATION_HANDLER;
		assertThat(testInstance.invoke(InvocationHandlerSupport.mock(AllPrimitiveTypesMethods.class), method, new Object[0])).isEqualTo(expectedValue);
	}
	
	@Test
	public void testInvoke() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport((proxy, method, args) -> "Hello");
		// 42.get() => "Hello" !
		assertThat(testInstance.invoke(42, Reflections.findMethod(Supplier.class, "get"), new Object[0])).isEqualTo("Hello");
	}
	
	@Test
	public void testMock() throws Throwable {
		CharSequence mock = InvocationHandlerSupport.mock(CharSequence.class, Appendable.class);
		assertThat(mock.length()).isEqualTo(0);
		assertThat(((Appendable) mock).append("coucou")).isNull();
	}
	
	@Test
	public void testInvokeEquals() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		// 42 == 42 ?
		assertThat((boolean) testInstance.invoke(42, Reflections.findMethod(Object.class, "equals", Object.class), new Object[] { 42 })).isTrue();
		// null == 42 ?
		assertThat((boolean) testInstance.invoke(null, Reflections.findMethod(Object.class, "equals", Object.class), new Object[] { 42 })).isFalse();
		// 42 == null ?
		assertThat((boolean) testInstance.invoke(42, Reflections.findMethod(Object.class, "equals", Object.class), new Object[] { null })).isFalse();
	}
	
	/** Mainly tested for non StackOverflowError */
	@Test
	public void testInvokeEquals_onProxiedInstance() {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { CharSequence.class }, testInstance);
		assertThat(proxy).isEqualTo(proxy);
		assertThat(new Object()).isNotEqualTo(proxy);
		assertThat(proxy).isNotEqualTo(new Object());
		assertThat(proxy).isNotEqualTo(testInstance);
		assertThat(testInstance).isNotEqualTo(proxy);
		assertThat(testInstance).isEqualTo(testInstance);
		assertThat(new Object()).isNotEqualTo(testInstance);
		assertThat(testInstance).isNotEqualTo(new Object());
	}
	
	@Test
	public void testInvokeHashCode_returnsTargetHashCode() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		// 42.hasCode()
		assertThat(testInstance.invoke(42, Reflections.findMethod(Object.class, "hashCode"), new Object[0])).isEqualTo(42);
	}
	
	@Test
	public void testInvokeHashCode_onNullReference_throwsNPE() {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		// null.hasCode()
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> testInstance.invoke(null, Reflections.findMethod(Integer.class, 
				"hashCode"), new Object[0]));
	}
	
	/** Mainly tested for non StackOverflowError */
	@Test
	public void testInvokeHashCode_onProxiedInstance() {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { CharSequence.class }, testInstance);
		assertThat(proxy.hashCode()).isEqualTo(testInstance.hashCode());
	}
	
	@Test
	public void testInvokeToString() {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		assertThat(testInstance.toString().contains(InvocationHandlerSupport.class.getSimpleName())).isTrue();
	}
	
	/** Mainly tested for non StackOverflowError */
	@Test
	public void testInvokeToString_onProxiedInstance() {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { CharSequence.class }, testInstance);
		assertThat(proxy.toString()).isEqualTo(testInstance.toString());
	}
	
	private interface AllPrimitiveTypesMethods {
		
		boolean getBoolean();
		
		char getChar();
		
		byte getByte();
		
		short getShort();
		
		int getInt();
		
		long getLong();
		
		float getFloat();
		
		double getDouble();
		
	}
	
}
package org.gama.lang;

import java.lang.reflect.Method;

import org.gama.lang.InvocationHandlerSupport.DefaultPrimitiveValueInvocationProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertEquals(expectedValue, testInstance.invoke(InvocationHandlerSupport.mock(AllPrimitiveTypesMethods.class), method, new Object[0]));
	}
	
	@Test
	public void testInvoke() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport((proxy, method, args) -> "Hello");
		// 42.toString() => "Hello" !
		assertEquals("Hello", testInstance.invoke(42, Reflections.findMethod(Object.class, "toString"), new Object[0]));
	}
	
	@Test
	public void testInvokeEquals() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		// 42 == 42 ?
		assertTrue((boolean) testInstance.invoke(42, Reflections.findMethod(Object.class, "equals", Object.class), new Object[] { 42 }));
		// null == 42 ?
		assertFalse((boolean) testInstance.invoke(null, Reflections.findMethod(Object.class, "equals", Object.class), new Object[] { 42 }));
	}
	
	@Test
	public void testInvokeHashCode_returnsTargetHashCode() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		// 42.hasCode()
		assertEquals(42, testInstance.invoke(42, Reflections.findMethod(Object.class, "hashCode"), new Object[0]));
	}
	
	@Test
	public void testInvokeHashCode_onNullReference_throwsNPE() throws Throwable {
		InvocationHandlerSupport testInstance = new InvocationHandlerSupport();
		// null.hasCode()
		assertThrows(NullPointerException.class, () -> testInstance.invoke(null, Reflections.findMethod(Integer.class, "hashCode"), new Object[0]));
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
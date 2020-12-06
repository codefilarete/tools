package org.gama.lang;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Optional;

import org.gama.lang.Reflections.InvokationRuntimeException;
import org.gama.lang.Reflections.MemberNotFoundException;
import org.gama.lang.exception.Exceptions;
import org.gama.lang.test.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.gama.lang.Reflections.PACKAGES_PRINT_MODE_CONTEXT;
import static org.gama.lang.Reflections.findField;
import static org.gama.lang.Reflections.findMethod;
import static org.gama.lang.Reflections.onJavaBeanPropertyWrapper;
import static org.gama.lang.Reflections.onJavaBeanPropertyWrapperName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Guillaume Mary
 */
public class ReflectionsTest {
	
	@Test
	void toString_flatPackagePrintOtionSwitching() {
		assertEquals("j.l.String", Reflections.toString(String.class));
		
		// testing option change with "off"
		PACKAGES_PRINT_MODE_CONTEXT.set(Optional.of("off"));
		assertEquals("java.lang.String", Reflections.toString(String.class));
		// checking that default behavior is back to normal
		PACKAGES_PRINT_MODE_CONTEXT.remove();
		assertEquals("j.l.String", Reflections.toString(String.class));
		
		// testing option change with "disable"
		PACKAGES_PRINT_MODE_CONTEXT.set(Optional.of("off"));
		// checking that default behavior is back to normal
		PACKAGES_PRINT_MODE_CONTEXT.remove();
		assertEquals("j.l.String", Reflections.toString(String.class));
		
		// testing option change with "false"
		PACKAGES_PRINT_MODE_CONTEXT.set(Optional.of("false"));
		// checking that default behavior is back to normal
		PACKAGES_PRINT_MODE_CONTEXT.remove();
		assertEquals("j.l.String", Reflections.toString(String.class));
	}
	
	
	@Test
	void getDefaultConstructor() {
		Constructor<Toto> defaultConstructor = Reflections.getDefaultConstructor(Toto.class);
		assertNotNull(defaultConstructor);
	}
	
	@Test
	void getDefaultConstructor_closedClass() {
		Constructor<ClosedClass> defaultConstructor = Reflections.getDefaultConstructor(ClosedClass.class);
		assertNotNull(defaultConstructor);
	}
	
	@Test
	void getDefaultConstructor_innerStaticClass() {
		Constructor<InnerStaticClass> defaultConstructor = Reflections.getDefaultConstructor(InnerStaticClass.class);
		assertNotNull(defaultConstructor);
	}
	
	@Test
	void getDefaultConstructor_abstractClass() {
		Constructor<AbstractMap> defaultConstructor = Reflections.getDefaultConstructor(AbstractMap.class);
		assertNotNull(defaultConstructor);
	}
	
	
	public static Object[][] testGetDefaultConstructor_throwingCases_data() {
		return new Object[][] {
				{ InnerClass.class, "Class o.g.l.ReflectionsTest$InnerClass has no default constructor because it is an inner non static class" +
						" (needs an instance of the encosing class to be constructed)" },
				{ int.class, "Class int has no default constructor because it is a primitive type" },
				{ int[].class, "Class int[] has no default constructor because it is an array" },
				{ CharSequence.class, "Class j.l.CharSequence has no default constructor because it is an interface" },
				{ URL.class, "Class j.n.URL has no default constructor" }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testGetDefaultConstructor_throwingCases_data")
	void getDefaultConstructor_throwingCases(Class clazz, String expectedMessage) {
		assertEquals(expectedMessage,
				assertThrows(UnsupportedOperationException.class, () -> Reflections.getDefaultConstructor(clazz)).getMessage());
	}
	
	@Test
	void getConstructor_private() {
		Constructor<InnerStaticClassWithPrivateConstructor> constructor1 = Reflections.getConstructor(InnerStaticClassWithPrivateConstructor.class);
		assertNotNull(constructor1);
		Constructor<InnerClassWithPrivateConstructor> constructor2 = Reflections.getConstructor(InnerClassWithPrivateConstructor.class, ReflectionsTest.class);
		assertNotNull(constructor2);
	}
	
	@Test
	void getConstructor_innerNonStaticClass_missingEnclosingClassAsParameter_throwsException() {
		assertEquals("Non static inner classes require an enclosing class parameter as first argument",
				assertThrows(MemberNotFoundException.class, () -> Reflections.getConstructor(InnerClassWithPrivateConstructor.class)).getMessage());
	}
	
	public static Object[][] testGetFieldData() {
		return new Object[][] {
				{ Toto.class, "a", Toto.class },
				{ Toto.class, "b", Toto.class },
				// inheritance test
				{ Tutu.class, "a", Toto.class },
				{ Tutu.class, "b", Tata.class },
		};
	}
	
	@ParameterizedTest
	@MethodSource("testGetFieldData")
	void getField(Class<Toto> fieldClass, String fieldName, Class expectedDeclaringClass) {
		Field field = findField(fieldClass, fieldName);
		assertNotNull(field);
		assertEquals(fieldName, field.getName());
		assertEquals(expectedDeclaringClass, field.getDeclaringClass());
	}
	
	public static Object[][] testGetMethodData() {
		return new Object[][] {
				{ Toto.class, "toto", null, Toto.class, 0 },
				{ Toto.class, "toto2", null, Toto.class, 0 },
				// with parameter
				{ Toto.class, "toto", Integer.TYPE, Toto.class, 1 },
				{ Toto.class, "toto2", Integer.TYPE, Toto.class, 1 },
				// inheritance test
				{ Tutu.class, "toto", null, Toto.class, 0 },
				{ Tutu.class, "toto2", null, Toto.class, 0 },
				{ Tutu.class, "toto", Integer.TYPE, Toto.class, 1 },
				{ Tutu.class, "toto2", Integer.TYPE, Toto.class, 1 },
		};
	}
	
	@ParameterizedTest
	@MethodSource("testGetMethodData")
	void getMethod(Class<Toto> methodClass, String methodName, Class parameterType, Class expectedDeclaringClass, int exectedParameterCount) {
		Method method;
		if (parameterType == null) {
			method = findMethod(methodClass, methodName);
		} else {
			method = findMethod(methodClass, methodName, parameterType);
		}
		assertNotNull(method);
		assertEquals(methodName, method.getName());
		assertEquals(expectedDeclaringClass, method.getDeclaringClass());
		assertEquals(exectedParameterCount, method.getParameterTypes().length);
	}
	
	@Test
	void getConstructor() throws NoSuchMethodException {
		assertEquals(String.class.getConstructor(String.class), Reflections.getConstructor(String.class, String.class));
		assertThrows(MemberNotFoundException.class, () -> Reflections.getConstructor(String.class, Reflections.class));
	}
	
	@Test
	void findConstructor() throws NoSuchMethodException {
		assertEquals(String.class.getConstructor(String.class), Reflections.findConstructor(String.class, String.class));
		assertNull(Reflections.findConstructor(String.class, Reflections.class));
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_getterIsRecognized() throws NoSuchMethodException {
		class X {
			void getA() {
			}
		}
		boolean found = onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("getA"), m -> true, m -> false, m -> false);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_getterIsRecognized_boolean() throws NoSuchMethodException {
		class X {
			void isA() {
			}
		}
		boolean found = onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("isA"), m -> false, m -> false, m -> true);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_setterIsRecognized() throws NoSuchMethodException {
		class X {
			void setA() {
			}
		}
		boolean found = onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("setA"), m -> false, m -> true, m -> false);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_doesntMatchJavaBeanStandard_throwsException() {
		MemberNotFoundException thrownException = assertThrows(MemberNotFoundException.class, () -> onJavaBeanPropertyWrapperName(String.class.getMethod("toString"),
				m -> false, m -> true, m -> false));
		assertEquals("Field wrapper j.l.String.toString() doesn't fit encapsulation naming convention", thrownException.getMessage());
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_getterIsRecognized() {
		boolean found = onJavaBeanPropertyWrapperName("getA", m -> true, m -> false, m -> false);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_getterIsRecognized_boolean() {
		boolean found = onJavaBeanPropertyWrapperName("isA", m -> false, m -> false, m -> true);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_setterIsRecognized() {
		boolean found = onJavaBeanPropertyWrapperName("setA", m -> false, m -> true, m -> false);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_doesntMatchJavaBeanStandard_throwsException() {
		MemberNotFoundException thrownException = assertThrows(MemberNotFoundException.class, () -> onJavaBeanPropertyWrapperName("doSomething",
				m -> false, m -> true, m -> false));
		assertEquals("Field wrapper doSomething doesn't fit encapsulation naming convention", thrownException.getMessage());
	}
	
	@Test
	void onJavaBeanPropertyWrapper_getterIsRecognized() throws NoSuchMethodException {
		class X {
			String getA() {
				return null;
			}
		}
		boolean found = onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("getA"), m -> true, m -> false, m -> false);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapper_getterIsRecognized_boolean() throws NoSuchMethodException {
		class X {
			boolean isA() {
				return true;
			}
		}
		boolean found = onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("isA"), m -> false, m -> false, m -> true);
		assertTrue(found);
	}
	
	@Test
	void onJavaBeanPropertyWrapper_setterIsRecognized() throws NoSuchMethodException {
		class X {
			void setA(String a) {
			}
		}
		boolean found = onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("setA", String.class), m -> false, m -> true, m -> false);
		assertTrue(found);
	}
	
	@Test
	void newInstance_class_privateConstructor() {
		// This shouldn't cause problem
		ClosedClass closedClass = Reflections.newInstance(ClosedClass.class);
		// minimal test
		assertNotNull(closedClass);
	}
	
	@Test
	void newInstance_interface_throwsException() {
		InvokationRuntimeException thrownException = assertThrows(InvokationRuntimeException.class,
				() -> Reflections.newInstance(CharSequence.class));
		assertNotNull(Exceptions.findExceptionInCauses(thrownException, UnsupportedOperationException.class,
				"Class j.l.CharSequence has no default constructor because it is an interface"));
	}
	
	@Test
	void newInstance_abstractClass_throwsException() {
		InvokationRuntimeException thrownException = assertThrows(InvokationRuntimeException.class,
				() -> Reflections.newInstance(AbstractClass.class));
		assertNotNull(Exceptions.findExceptionInCauses(thrownException, InvokationRuntimeException.class,
				"Class o.g.l.ReflectionsTest$AbstractClass can't be instanciated because it is abstract"));
	}
	
	@Test
	void newInstance_classWhichConstructorThrowsException_throwsException() {
		InvokationRuntimeException thrownException = assertThrows(InvokationRuntimeException.class,
				() -> Reflections.newInstance(ThrowingConstructorClass.class));
		
		assertNotNull(Exceptions.findExceptionInCauses(thrownException, InvokationRuntimeException.class,
				"Class o.g.l.ReflectionsTest$ThrowingConstructorClass can't be instanciated"),
				() -> {
					StringWriter exceptionAsString = new StringWriter();
					thrownException.printStackTrace(new PrintWriter(exceptionAsString));
					return "Can't find exception in " + exceptionAsString;
				});
		// looking for exception thrown by constructor
		assertNotNull(Exceptions.findExceptionInCauses(thrownException, NullPointerException.class));
	}
	
	@Test
	void newInstance_constructor() throws NoSuchMethodException {
		assertEquals("", Reflections.newInstance(String.class.getConstructor()));
		assertEquals("AB", Reflections.newInstance(String.class.getConstructor(byte[].class), new byte[] { 'A', 'B' }));
	}
	
	@Test
	void newInstance_constructor_throwsException_throwsException() {
		InvokationRuntimeException thrownException = assertThrows(InvokationRuntimeException.class,
				() -> Reflections.newInstance(ThrowingConstructorClass.class.getConstructor()));
		
		assertNotNull(Exceptions.findExceptionInCauses(thrownException, InvokationRuntimeException.class,
				"Class o.g.l.ReflectionsTest$ThrowingConstructorClass can't be instanciated"),
				() -> {
					StringWriter exceptionAsString = new StringWriter();
					thrownException.printStackTrace(new PrintWriter(exceptionAsString));
					return "Can't find exception in " + exceptionAsString;
				});
		// looking for exception thrown by constructor
		assertNotNull(Exceptions.findExceptionInCauses(thrownException, NullPointerException.class));
	}
	
	@Test
	void invoke() throws NoSuchMethodException {
		Toto toto = new Toto();
		Method setter = Toto.class.getMethod("setA", int.class);
		
		// minimal test
		Reflections.invoke(setter, toto, 42);
		assertEquals(42, toto.getA());
		
		// wrong input type (throws IllegalArgumentException)
		assertThrows(InvokationRuntimeException.class, () -> Reflections.invoke(setter, toto, "dummyString"));
		
		// Non accessible method
		Method privateMethod = Toto.class.getDeclaredMethod("toto");
		assertThrows(InvokationRuntimeException.class, () -> Reflections.invoke(privateMethod, toto));
	}
	
	@Test
	void wrappedField() throws NoSuchMethodException, NoSuchFieldException {
		// simple case
		Field field = Reflections.wrappedField(Toto.class.getDeclaredMethod("setB", String.class));
		assertEquals(Toto.class.getDeclaredField("b"), field);
		
		// Tata.b hides Toto.b
		field = Reflections.wrappedField(Tata.class.getDeclaredMethod("setB", String.class));
		assertEquals(Tata.class.getDeclaredField("b"), field);
		
		// true override : Tata.setA overrides Toto.setA, field is in Toto
		field = Reflections.wrappedField(Tata.class.getDeclaredMethod("setA", Integer.TYPE));
		assertEquals(Toto.class.getDeclaredField("a"), field);
	}
	
	@Test
	void propertyName() throws NoSuchMethodException {
		// simple case
		String propertyName = Reflections.propertyName(Toto.class.getDeclaredMethod("setB", String.class));
		assertEquals("b", propertyName);
	}
	
	@Test
	void propertyName_methodDoesntFitJavaBeanConvention_exceptionIsThrown() throws NoSuchMethodException {
		Method fixBMethod = Toto.class.getDeclaredMethod("fixB", String.class);
		assertEquals("Field wrapper o.g.l.ReflectionsTest$Toto.fixB(j.l.String) doesn't fit encapsulation naming convention",
				assertThrows(MemberNotFoundException.class, () -> Reflections.propertyName(fixBMethod)).getMessage());
	}
	
	@Test
	void forName() {
		assertEquals(boolean.class, Reflections.forName("Z"));
		assertEquals(int.class, Reflections.forName("I"));
		assertEquals(long.class, Reflections.forName("J"));
		assertEquals(short.class, Reflections.forName("S"));
		assertEquals(byte.class, Reflections.forName("B"));
		assertEquals(double.class, Reflections.forName("D"));
		assertEquals(float.class, Reflections.forName("F"));
		assertEquals(char.class, Reflections.forName("C"));
		assertEquals(void.class, Reflections.forName("V"));
		assertEquals(String.class, Reflections.forName(String.class.getName()));
		assertEquals(Object[].class, Reflections.forName("[Ljava.lang.Object;"));
		assertEquals(Object.class, Reflections.forName("java.lang.Object"));
		assertEquals(boolean[].class, Reflections.forName("[Z"));
		assertEquals(boolean[][].class, Reflections.forName("[[Z"));
	}
	
	@Test
	void wrapperClass() {
		assertEquals(Boolean.class, Reflections.giveWrapperClass(boolean.class));
		assertEquals(Integer.class, Reflections.giveWrapperClass(int.class));
		assertEquals(Long.class, Reflections.giveWrapperClass(long.class));
		assertEquals(Short.class, Reflections.giveWrapperClass(short.class));
		assertEquals(Byte.class, Reflections.giveWrapperClass(byte.class));
		assertEquals(Double.class, Reflections.giveWrapperClass(double.class));
		assertEquals(Float.class, Reflections.giveWrapperClass(float.class));
		assertEquals(Character.class, Reflections.giveWrapperClass(char.class));
		assertEquals(Void.class, Reflections.giveWrapperClass(void.class));
		Assertions.assertThrows(() -> Reflections.giveWrapperClass(String.class), Assertions.hasExceptionInCauses(IllegalArgumentException.class)
				.andProjection(Assertions.hasMessage("Given type is not a primitive one : j.l.String")));
	}
	
	@Test
	void isAssignableFrom() {
		Assertions.assertEquals(true, Reflections.isAssignableFrom(boolean.class, Boolean.class));
		Assertions.assertEquals(true, Reflections.isAssignableFrom(Boolean.class, boolean.class));
		Assertions.assertEquals(true, Reflections.isAssignableFrom(CharSequence.class, String.class));
		Assertions.assertEquals(false, Reflections.isAssignableFrom(String.class, CharSequence.class));
		Assertions.assertEquals(true, Reflections.isAssignableFrom(String.class, String.class));
		Assertions.assertEquals(true, Reflections.isAssignableFrom(CharSequence.class, CharSequence.class));
		Assertions.assertEquals(false, Reflections.isAssignableFrom(CharSequence.class, Object.class));
		Assertions.assertEquals(true, Reflections.isAssignableFrom(Object.class, CharSequence.class));
	}
	
	@Test
	void newProxy() throws NoSuchMethodException, IOException {
		Method[] capturedMethod = new Method[1];
		CharSequence proxy = Reflections.newProxy(CharSequence.class, (p, m, args) -> {
			capturedMethod[0] = m;
			return null;
		}, Closeable.class);
		
		// capturing main method interface
		proxy.subSequence(0, 1);
		assertEquals(CharSequence.class.getMethod("subSequence", int.class, int.class), capturedMethod[0]);
		((Closeable) proxy).close();
		assertEquals(Closeable.class.getMethod("close"), capturedMethod[0]);
	}
	
	private static class ClosedClass {
		private ClosedClass() {
		}
	}
	
	private static class Toto {
		private int a;
		private String b;
		
		private void toto() {
		}
		
		private void toto(int a) {
		}
		
		// Method toto2() is declared in reverse order thant toto() ones to test Reflections on JDK robustness
		// (open jdk doesn't return then in same order)
		private void toto2(int a) {
		}
		
		private void toto2() {
		}
		
		public int getA() {
			return a;
		}
		
		public void setA(int a) {
			this.a = a;
		}
		
		public void setB(String b) {
			this.b = b;
		}
		
		/** Non conventional setter */
		public void fixB(String b) {
			setB(b);
		}
	}
	
	private static class Tata extends Toto {
		private String b;
		
		@Override
		public void setA(int a) {
			super.setA(a);
		}
		
		@Override
		public void setB(String b) {
			this.b = b;
		}
	}
	
	private static class Titi extends Tata {
		// no field, no method, for no member traversal check
	}
	
	private static class Tutu extends Titi {
		// no field, no method, for no member traversal check
	}
	
	private class InnerClass {
		
	}
	
	private class InnerClassWithPrivateConstructor {
		
		private InnerClassWithPrivateConstructor() {
		}
	}
	
	private static class InnerStaticClass {
		
	}
	
	private static class InnerStaticClassWithPrivateConstructor {
		
		private InnerStaticClassWithPrivateConstructor() {
			
		}
	}
	
	private static class ThrowingConstructorClass {
		
		public ThrowingConstructorClass() {
			throw new NullPointerException();
		}
	}
	
	private abstract static class AbstractClass {
		private AbstractClass() {
		}
	}
}
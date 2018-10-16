package org.gama.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.AbstractMap;

import org.gama.lang.Reflections.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
	public void testGetDefaultConstructor() {
		Constructor<Toto> defaultConstructor = Reflections.getDefaultConstructor(Toto.class);
		assertNotNull(defaultConstructor);
	}
	
	@Test
	public void testGetDefaultConstructor_closedClass() {
		Constructor<ClosedClass> defaultConstructor = Reflections.getDefaultConstructor(ClosedClass.class);
		assertNotNull(defaultConstructor);
	}
	@Test
	public void testGetDefaultConstructor_innerStaticClass() {
		Constructor<InnerStaticClass> defaultConstructor = Reflections.getDefaultConstructor(InnerStaticClass.class);
		assertNotNull(defaultConstructor);
	}
	
	@Test
	public void testGetDefaultConstructor_abstractClass() {
		Constructor<AbstractMap> defaultConstructor = Reflections.getDefaultConstructor(AbstractMap.class);
		assertNotNull(defaultConstructor);
	}
	
	
	public static Object[][] testGetDefaultConstructor_throwingCases_data() {
		return new Object[][] {
				{ InnerClass.class, "Class o.g.l.InnerClass has no default constructor because it is an inner non static class" +
						" (needs an instance of the encosing class to be constructed)" },
				{ Integer.TYPE, "Class int has no default constructor because it is a primitive type" },
				{ int[].class, "Class int[] has no default constructor because it is an array" },
				{ CharSequence.class, "Class j.l.CharSequence has no default constructor because it is an interface" },
				{ URL.class, "Class j.n.URL has no default constructor" }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testGetDefaultConstructor_throwingCases_data")
	public void testGetDefaultConstructor_throwingCases(Class clazz, String expectedMessage) {
		assertEquals(expectedMessage,
				assertThrows(UnsupportedOperationException.class, () -> Reflections.getDefaultConstructor(clazz)).getMessage());
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
	public void testGetField(Class<Toto> fieldClass, String fieldName, Class expectedDeclaringClass) {
		Field field = Reflections.findField(fieldClass, fieldName);
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
	public void testGetMethod(Class<Toto> methodClass, String methodName, Class parameterType, Class expectedDeclaringClass, int exectedParameterCount) {
		Method method;
		if (parameterType == null) {
			method = Reflections.findMethod(methodClass, methodName);
		} else {
			method = Reflections.findMethod(methodClass, methodName, parameterType);
		}
		assertNotNull(method);
		assertEquals(methodName, method.getName());
		assertEquals(expectedDeclaringClass, method.getDeclaringClass());
		assertEquals(exectedParameterCount, method.getParameterTypes().length);
	}
	
	@Test
	public void testGetConstructor() throws NoSuchMethodException {
		assertEquals(String.class.getConstructor(String.class), Reflections.getConstructor(String.class, String.class));
		assertThrows(MemberNotFoundException.class, () -> Reflections.getConstructor(String.class, Reflections.class));
	}
	
	@Test
	public void testFindConstructor() throws NoSuchMethodException {
		assertEquals(String.class.getConstructor(String.class), Reflections.findConstructor(String.class, String.class));
		assertNull(Reflections.findConstructor(String.class, Reflections.class));
	}
	
	@Test
	public void testOnJavaBeanPropertyWrapperName_getterIsRecognized() throws NoSuchMethodException {
		class X {
			void getA() {
			}
		}
		boolean found = Reflections.onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("getA"), m -> true, m -> false, m -> false);
		assertTrue(found);
	}
	
	@Test
	public void testOnJavaBeanPropertyWrapperName_getterIsRecognized_boolean() throws NoSuchMethodException {
		class X {
			void isA() {
			}
		}
		boolean found = Reflections.onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("isA"), m -> false, m -> false, m -> true);
		assertTrue(found);
	}
	
	@Test
	public void testOnJavaBeanPropertyWrapperName_setterIsRecognized() throws NoSuchMethodException {
		class X {
			void setA() {
			}
		}
		boolean found = Reflections.onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("setA"), m -> false, m -> true, m -> false);
		assertTrue(found);
	}
	
	@Test
	public void testOnJavaBeanPropertyWrapper_getterIsRecognized() throws NoSuchMethodException {
		class X {
			String getA() {
				return null;
			}
		}
		boolean found = Reflections.onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("getA"), m -> true, m -> false, m -> false);
		assertTrue(found);
	}
	
	@Test
	public void testOnJavaBeanPropertyWrapper_getterIsRecognized_boolean() throws NoSuchMethodException {
		class X {
			boolean isA() {
				return true;
			}
		}
		boolean found = Reflections.onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("isA"), m -> false, m -> false, m -> true);
		assertTrue(found);
	}
	
	@Test
	public void testOnJavaBeanPropertyWrapper_setterIsRecognized() throws NoSuchMethodException {
		class X {
			void setA(String a) {
			}
		}
		boolean found = Reflections.onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("setA", String.class), m -> false, m -> true, m -> false);
		assertTrue(found);
	}
	
	@Test
	public void testNewInstance_privateConstructor() {
		// This shouldn't cause problem
		ClosedClass closedClass = Reflections.newInstance(ClosedClass.class);
		// minimal test
		assertNotNull(closedClass);
	}
	
	@Test
	public void testWrappedField() throws NoSuchMethodException, NoSuchFieldException {
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
	public void testForName() throws ClassNotFoundException {
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
		
		public void setA(int a) {
			this.a = a;
		}
		
		public void setB(String b) {
			this.b = b;
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
	
	private static class InnerStaticClass {
		
	}
}
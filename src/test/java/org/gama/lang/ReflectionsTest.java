package org.gama.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Guillaume Mary
 */
@RunWith(DataProviderRunner.class)
public class ReflectionsTest {
	
	@Test
	public void testGetDefaultConstructor() {
		Constructor<Toto> defaultConstructor = Reflections.getDefaultConstructor(Toto.class);
		assertNotNull(defaultConstructor);
	}
	
	@DataProvider
	public static Object[][] testGetFieldData() {
		return new Object[][] {
				{ Toto.class, "a", Toto.class },
				{ Toto.class, "b", Toto.class },
				// inheritance test
				{ Tutu.class, "a", Toto.class },
				{ Tutu.class, "b", Tata.class },
		};
	}
	
	@Test
	@UseDataProvider("testGetFieldData")
	public void testGetField(Class<Toto> fieldClass, String fieldName, Class expectedDeclaringClass) {
		Field field = Reflections.findField(fieldClass, fieldName);
		assertNotNull(field);
		assertEquals(fieldName, field.getName());
		assertEquals(expectedDeclaringClass, field.getDeclaringClass());
	}
	
	@DataProvider
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
	
	@Test
	@UseDataProvider("testGetMethodData")
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
		
		// méthodes toto2() déclarées en ordre inverse des toto() pour tester la robustesse au jdk
		// (open jdk ne renvoie pas dans le même ordre)  
		private void toto2(int a) {
		}
		
		private void toto2() {
		}
	}
	
	private static class Tata extends Toto {
		private String b;
	}
	
	private static class Titi extends Tata {
		// no field, no method, for no member traversal check
	}
	
	private static class Tutu extends Titi {
		// no field, no method, for no member traversal check
	}
}
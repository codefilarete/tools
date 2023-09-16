package org.codefilarete.tool;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Objects;
import java.util.Optional;

import org.assertj.core.api.AbstractThrowableAssert;
import org.codefilarete.tool.exception.Exceptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.THROWABLE;
import static org.codefilarete.tool.Reflections.*;

/**
 * @author Guillaume Mary
 */
public class ReflectionsTest {
	
	@Test
	void toString_flatPackagePrintOptionSwitching() {
		assertThat(Reflections.toString(String.class)).isEqualTo("j.l.String");
		
		// testing option change with "off"
		PACKAGES_PRINT_MODE_CONTEXT.set(Optional.of("off"));
		assertThat(Reflections.toString(String.class)).isEqualTo("java.lang.String");
		// checking that default behavior is back to normal
		PACKAGES_PRINT_MODE_CONTEXT.remove();
		assertThat(Reflections.toString(String.class)).isEqualTo("j.l.String");
		
		// testing option change with "disable"
		PACKAGES_PRINT_MODE_CONTEXT.set(Optional.of("off"));
		// checking that default behavior is back to normal
		PACKAGES_PRINT_MODE_CONTEXT.remove();
		assertThat(Reflections.toString(String.class)).isEqualTo("j.l.String");
		
		// testing option change with "false"
		PACKAGES_PRINT_MODE_CONTEXT.set(Optional.of("false"));
		// checking that default behavior is back to normal
		PACKAGES_PRINT_MODE_CONTEXT.remove();
		assertThat(Reflections.toString(String.class)).isEqualTo("j.l.String");
	}
	
	
	@Test
	void getDefaultConstructor() {
		Constructor<Toto> defaultConstructor = Reflections.getDefaultConstructor(Toto.class);
		assertThat(defaultConstructor).isNotNull();
	}
	
	@Test
	void getDefaultConstructor_closedClass() {
		Constructor<ClosedClass> defaultConstructor = Reflections.getDefaultConstructor(ClosedClass.class);
		assertThat(defaultConstructor).isNotNull();
	}
	
	@Test
	void getDefaultConstructor_innerStaticClass() {
		Constructor<InnerStaticClass> defaultConstructor = Reflections.getDefaultConstructor(InnerStaticClass.class);
		assertThat(defaultConstructor).isNotNull();
	}
	
	@Test
	void getDefaultConstructor_abstractClass() {
		Constructor<AbstractMap> defaultConstructor = Reflections.getDefaultConstructor(AbstractMap.class);
		assertThat(defaultConstructor).isNotNull();
	}
	
	
	public static Object[][] testGetDefaultConstructor_throwingCases_data() {
		return new Object[][] {
				{ InnerClass.class, "Class o.c.t.ReflectionsTest$InnerClass has no default constructor because it is an inner non static class" +
						" (needs an instance of the enclosing class to be constructed)" },
				{ int.class, "Class int has no default constructor because it is a primitive type" },
				{ int[].class, "Class int[] has no default constructor because it is an array" },
				{ CharSequence.class, "Class j.l.CharSequence has no default constructor because it is an interface" },
				{ URL.class, "Class j.n.URL has no default constructor" }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testGetDefaultConstructor_throwingCases_data")
	void getDefaultConstructor_throwingCases(Class<?> clazz, String expectedMessage) {
		assertThatThrownBy(() -> Reflections.getDefaultConstructor(clazz))
				.isInstanceOf(UnsupportedOperationException.class)
				.hasMessage(expectedMessage);
	}
	
	@Test
	void getConstructor_private() {
		Constructor<InnerStaticClassWithPrivateConstructor> constructor1 = Reflections.getConstructor(InnerStaticClassWithPrivateConstructor.class);
		assertThat(constructor1).isNotNull();
		Constructor<InnerClassWithPrivateConstructor> constructor2 = Reflections.getConstructor(InnerClassWithPrivateConstructor.class, ReflectionsTest.class);
		assertThat(constructor2).isNotNull();
	}
	
	@Test
	void getConstructor_innerNonStaticClass_missingEnclosingClassAsParameter_throwsException() {
		assertThatThrownBy(() -> Reflections.getConstructor(InnerClassWithPrivateConstructor.class))
				.isInstanceOf(MemberNotFoundException.class)
				.hasMessage("Non static inner classes require an enclosing class parameter as first argument");
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
		assertThat(field).isNotNull();
		assertThat(field.getName()).isEqualTo(fieldName);
		assertThat(field.getDeclaringClass()).isEqualTo(expectedDeclaringClass);
	}
	
	public static Object[][] findGetMethodData() {
		return new Object[][] {
				{ Toto.class, "toto", null, Toto.class, 0 },
				{ Toto.class, "toto2", null, Toto.class, 0 },
				// with parameter
				{ Toto.class, "toto", Integer.TYPE, Toto.class, 1 },
				{ Toto.class, "toto2", Integer.TYPE, Toto.class, 1 },
				// with primitive and boxing type
				{ Toto.class, "setA", Integer.class, Toto.class, 1 },
				// with super type in declared method
				{ Toto.class, "setC", String.class, Toto.class, 1 },
				{ Toto.class, "setC", StringBuffer.class, Toto.class, 1 },
				// inheritance test
				{ Tutu.class, "toto", null, Toto.class, 0 },
				{ Tutu.class, "toto2", null, Toto.class, 0 },
				{ Tutu.class, "toto", Integer.TYPE, Toto.class, 1 },
				{ Tutu.class, "toto2", Integer.TYPE, Toto.class, 1 },
		};
	}
	
	@ParameterizedTest
	@MethodSource("findGetMethodData")
	void findMethod(Class<Toto> methodClass, String methodName, Class parameterType, Class expectedDeclaringClass, int expectedParameterCount) {
		Method method;
		if (parameterType == null) {
			method = Reflections.findMethod(methodClass, methodName);
		} else {
			method = Reflections.findMethod(methodClass, methodName, parameterType);
		}
		assertThat(method).isNotNull();
		assertThat(method.getName()).isEqualTo(methodName);
		assertThat(method.getDeclaringClass()).isEqualTo(expectedDeclaringClass);
		assertThat(method.getParameterTypes().length).isEqualTo(expectedParameterCount);
	}
	
	@Test
	void getConstructor() throws NoSuchMethodException {
		assertThat(Reflections.getConstructor(String.class, String.class)).isEqualTo(String.class.getConstructor(String.class));
		assertThatThrownBy(() -> Reflections.getConstructor(String.class, Reflections.class)).isInstanceOf(MemberNotFoundException.class);
	}
	
	@Test
	void findConstructor() throws NoSuchMethodException {
		assertThat(Reflections.findConstructor(String.class, String.class)).isEqualTo(String.class.getConstructor(String.class));
		assertThat(Reflections.findConstructor(String.class, Reflections.class)).isNull();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_getterIsRecognized() throws NoSuchMethodException {
		class X {
			void getA() {
			}
		}
		boolean found = onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("getA"), m -> true, m -> false, m -> false);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_getterIsRecognized_boolean() throws NoSuchMethodException {
		class X {
			void isA() {
			}
		}
		boolean found = onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("isA"), m -> false, m -> false, m -> true);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_setterIsRecognized() throws NoSuchMethodException {
		class X {
			void setA() {
			}
		}
		boolean found = onJavaBeanPropertyWrapperName(X.class.getDeclaredMethod("setA"), m -> false, m -> true, m -> false);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_doesntMatchJavaBeanStandard_throwsException() {
		assertThatThrownBy(() -> onJavaBeanPropertyWrapperName(String.class.getMethod("toString"), m -> false, m -> true, m -> false))
				.isInstanceOf(MemberNotFoundException.class)
				.hasMessage("Field wrapper j.l.String.toString() doesn't fit encapsulation naming convention");
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_getterIsRecognized() {
		boolean found = onJavaBeanPropertyWrapperName("getA", m -> true, m -> false, m -> false);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_getterIsRecognized_boolean() {
		boolean found = onJavaBeanPropertyWrapperName("isA", m -> false, m -> false, m -> true);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_setterIsRecognized() {
		boolean found = onJavaBeanPropertyWrapperName("setA", m -> false, m -> true, m -> false);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapperName_inputMethodName_doesntMatchJavaBeanStandard_throwsException() {
		assertThatThrownBy(() -> onJavaBeanPropertyWrapperName("doSomething", m -> false, m -> true, m -> false))
				.isInstanceOf(MemberNotFoundException.class)
				.hasMessage("Field wrapper doSomething doesn't fit encapsulation naming convention");
	}
	
	@Test
	void onJavaBeanPropertyWrapper_getterIsRecognized() throws NoSuchMethodException {
		class X {
			String getA() {
				return null;
			}
		}
		boolean found = onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("getA"), m -> true, m -> false, m -> false);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapper_getterIsRecognized_boolean() throws NoSuchMethodException {
		class X {
			boolean isA() {
				return true;
			}
		}
		boolean found = onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("isA"), m -> false, m -> false, m -> true);
		assertThat(found).isTrue();
	}
	
	@Test
	void onJavaBeanPropertyWrapper_setterIsRecognized() throws NoSuchMethodException {
		class X {
			void setA(String a) {
			}
		}
		boolean found = onJavaBeanPropertyWrapper(X.class.getDeclaredMethod("setA", String.class), m -> false, m -> true, m -> false);
		assertThat(found).isTrue();
	}
	
	@Test
	void newInstance_class_privateConstructor() {
		// This shouldn't cause problem
		ClosedClass closedClass = Reflections.newInstance(ClosedClass.class);
		// minimal test
		assertThat(closedClass).isNotNull();
	}
	
	@Test
	void newInstance_interface_throwsException() {
		assertThatThrownBy(() -> Reflections.newInstance(CharSequence.class))
				.isInstanceOf(InvokationRuntimeException.class)
				.extracting(thrownException -> Exceptions.findExceptionInCauses(thrownException, UnsupportedOperationException.class), THROWABLE)
				.hasMessage("Class j.l.CharSequence has no default constructor because it is an interface");
	}
	
	@Test
	void newInstance_abstractClass_throwsException() {
		assertThatThrownBy(() -> Reflections.newInstance(AbstractClass.class))
				.isInstanceOf(InvokationRuntimeException.class)
				.extracting(thrownException -> Exceptions.findExceptionInCauses(thrownException, InvokationRuntimeException.class), THROWABLE)
				.hasMessage("Class o.c.t.ReflectionsTest$AbstractClass can't be instantiated because it is abstract");
	}
	
	@Test
	void newInstance_classWhichConstructorThrowsException_throwsException() {
		AbstractThrowableAssert<?, ? extends Throwable> exceptionAsserter =
				assertThatThrownBy(() -> Reflections.newInstance(ThrowingConstructorClass.class))
				.isInstanceOf(InvokationRuntimeException.class);
		
		exceptionAsserter
				.extracting(thrownException -> Exceptions.findExceptionInCauses(thrownException, InvokationRuntimeException.class), THROWABLE)
				.hasMessage("Class o.c.t.ReflectionsTest$ThrowingConstructorClass can't be instantiated");
		
		exceptionAsserter
				.extracting(thrownException -> Exceptions.findExceptionInCauses(thrownException, NullPointerException.class), THROWABLE)
				.isNotNull();
	}
	
	@Test
	void newInstance_constructor() throws NoSuchMethodException {
		assertThat(Reflections.newInstance(String.class.getConstructor())).isEqualTo("");
		assertThat(Reflections.newInstance(String.class.getConstructor(byte[].class), new byte[] { 'A', 'B' })).isEqualTo("AB");
	}
	
	@Test
	void newInstance_constructor_throwsException_throwsException() {
		AbstractThrowableAssert<?, ? extends Throwable> exceptionAsserter =
				assertThatThrownBy(() -> Reflections.newInstance(ThrowingConstructorClass.class.getConstructor()))
						.isInstanceOf(InvokationRuntimeException.class);
		
		exceptionAsserter
				.extracting(thrownException -> Exceptions.findExceptionInCauses(thrownException, InvokationRuntimeException.class), THROWABLE)
				.hasMessage("Class o.c.t.ReflectionsTest$ThrowingConstructorClass can't be instantiated");
		
		exceptionAsserter
				.extracting(thrownException -> Exceptions.findExceptionInCauses(thrownException, NullPointerException.class), THROWABLE)
				.isNotNull();
	}
	
	@Test
	void invoke() throws NoSuchMethodException {
		Toto toto = new Toto();
		Method setter = Toto.class.getMethod("setA", int.class);
		
		// minimal test
		Reflections.invoke(setter, toto, 42);
		assertThat(toto.getA()).isEqualTo(42);
		
		// wrong input type (throws IllegalArgumentException)
		assertThatThrownBy(() -> Reflections.invoke(setter, toto, "dummyString")).isInstanceOf(InvokationRuntimeException.class);

		// Non accessible method
		Method privateMethod = Toto.class.getDeclaredMethod("toto");
		assertThatThrownBy(() -> Reflections.invoke(privateMethod, toto)).isInstanceOf(InvokationRuntimeException.class);
	}
	
	@Test
	void wrappedField() throws NoSuchMethodException, NoSuchFieldException {
		// simple case
		Field field = Reflections.wrappedField(Toto.class.getDeclaredMethod("setB", String.class));
		assertThat(field).isEqualTo(Toto.class.getDeclaredField("b"));
		
		// Tata.b hides Toto.b
		field = Reflections.wrappedField(Tata.class.getDeclaredMethod("setB", String.class));
		assertThat(field).isEqualTo(Tata.class.getDeclaredField("b"));
		
		// true override : Tata.setA overrides Toto.setA, field is in Toto
		field = Reflections.wrappedField(Tata.class.getDeclaredMethod("setA", Integer.TYPE));
		assertThat(field).isEqualTo(Toto.class.getDeclaredField("a"));
	}
	
	@Test
	void propertyName() throws NoSuchMethodException {
		// simple case
		String propertyName = Reflections.propertyName(Toto.class.getDeclaredMethod("setB", String.class));
		assertThat(propertyName).isEqualTo("b");
	}
	
	@Test
	void propertyName_methodDoesntFitJavaBeanConvention_exceptionIsThrown() throws NoSuchMethodException {
		Method fixBMethod = Toto.class.getDeclaredMethod("fixB", String.class);
		assertThatThrownBy(() -> Reflections.propertyName(fixBMethod))
				.isInstanceOf(MemberNotFoundException.class)
				.hasMessage("Field wrapper o.c.t.ReflectionsTest$Toto.fixB(j.l.String) doesn't fit encapsulation naming convention");
	}
	
	@Test
	void forName() {
		assertThat(Reflections.forName("Z")).isEqualTo(boolean.class);
		assertThat(Reflections.forName("I")).isEqualTo(int.class);
		assertThat(Reflections.forName("J")).isEqualTo(long.class);
		assertThat(Reflections.forName("S")).isEqualTo(short.class);
		assertThat(Reflections.forName("B")).isEqualTo(byte.class);
		assertThat(Reflections.forName("D")).isEqualTo(double.class);
		assertThat(Reflections.forName("F")).isEqualTo(float.class);
		assertThat(Reflections.forName("C")).isEqualTo(char.class);
		assertThat(Reflections.forName("V")).isEqualTo(void.class);
		assertThat(Reflections.forName(String.class.getName())).isEqualTo(String.class);
		assertThat(Reflections.forName("[Ljava.lang.Object;")).isEqualTo(Object[].class);
		assertThat(Reflections.forName("Ljava.lang.Object;")).isEqualTo(Object.class);
		assertThat(Reflections.forName("java.lang.Object")).isEqualTo(Object.class);
		assertThat(Reflections.forName("[Z")).isEqualTo(boolean[].class);
		assertThat(Reflections.forName("[[Z")).isEqualTo(boolean[][].class);
	}
	
	@Test
	void wrapperClass() {
		assertThat(Reflections.giveWrapperClass(boolean.class)).isEqualTo(Boolean.class);
		assertThat(Reflections.giveWrapperClass(int.class)).isEqualTo(Integer.class);
		assertThat(Reflections.giveWrapperClass(long.class)).isEqualTo(Long.class);
		assertThat(Reflections.giveWrapperClass(short.class)).isEqualTo(Short.class);
		assertThat(Reflections.giveWrapperClass(byte.class)).isEqualTo(Byte.class);
		assertThat(Reflections.giveWrapperClass(double.class)).isEqualTo(Double.class);
		assertThat(Reflections.giveWrapperClass(float.class)).isEqualTo(Float.class);
		assertThat(Reflections.giveWrapperClass(char.class)).isEqualTo(Character.class);
		assertThat(Reflections.giveWrapperClass(void.class)).isEqualTo(Void.class);
		assertThatThrownBy(() -> Reflections.giveWrapperClass(String.class))
				.extracting(throwException -> Exceptions.findExceptionInCauses(throwException, IllegalArgumentException.class), THROWABLE)
				.hasMessage("Given type is not a primitive one : j.l.String");
	}
	
	@Test
	void isAssignableFrom() {
		assertThat(Reflections.isAssignableFrom(boolean.class, Boolean.class)).isTrue();
		assertThat(Reflections.isAssignableFrom(Boolean.class, boolean.class)).isTrue();
		assertThat(Reflections.isAssignableFrom(CharSequence.class, String.class)).isTrue();
		assertThat(Reflections.isAssignableFrom(String.class, CharSequence.class)).isFalse();
		assertThat(Reflections.isAssignableFrom(String.class, String.class)).isTrue();
		assertThat(Reflections.isAssignableFrom(CharSequence.class, CharSequence.class)).isTrue();
		assertThat(Reflections.isAssignableFrom(CharSequence.class, Object.class)).isFalse();
		assertThat(Reflections.isAssignableFrom(Object.class, CharSequence.class)).isTrue();
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
		assertThat(capturedMethod[0]).isEqualTo(CharSequence.class.getMethod("subSequence", int.class, int.class));
		((Closeable) proxy).close();
		assertThat(capturedMethod[0]).isEqualTo(Closeable.class.getMethod("close"));
	}
	
	@Test
	void isStatic_class() {
		assertThat(Reflections.isStatic(InnerClass.class)).isFalse();
		assertThat(Reflections.isStatic(InnerStaticClass.class)).isTrue();
	}
	
	@Test
	void isStatic_method() {
		assertThat(Reflections.isStatic(Reflections.getMethod(Objects.class, "equals", Object.class, Object.class))).isTrue();
		assertThat(Reflections.isStatic(Reflections.getMethod(Object.class, "equals", Object.class))).isFalse();
	}
	
	@Test
	void isStatic_field() {
		assertThat(Reflections.isStatic(Reflections.getField(String.class, "CASE_INSENSITIVE_ORDER"))).isTrue();
		assertThat(Reflections.isStatic(Reflections.getField(String.class, "value"))).isFalse();
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
		
		public void setC(CharSequence s) {
		
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
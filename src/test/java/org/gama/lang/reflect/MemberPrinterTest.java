package org.gama.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.gama.lang.Reflections;
import org.gama.lang.ReflectionsTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.gama.lang.reflect.MemberPrinter.FLATTEN_PACKAGE_PRINTER;
import static org.gama.lang.reflect.MemberPrinter.FULL_PACKAGE_PRINTER;
import static org.gama.lang.reflect.MemberPrinter.WELL_KNOWN_FLATTEN_PACKAGE_PRINTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
public class MemberPrinterTest {
	
	@Test
	public void toStringClass() throws ClassNotFoundException {
		MemberPrinter testInstance = FLATTEN_PACKAGE_PRINTER;
		assertEquals("j.l.String", testInstance.toString(String.class));
		assertEquals("j.u.Collection", testInstance.toString(Collection.class));
		assertEquals("o.g.l.ReflectionsTest", testInstance.toString(ReflectionsTest.class));
		assertEquals("TopPackageLevelClass", testInstance.toString(Class.forName("TopPackageLevelClass")));
		assertEquals("boolean", testInstance.toString(boolean.class));
		assertEquals("boolean", testInstance.toString(Boolean.TYPE));
		assertEquals("void", testInstance.toString(Void.TYPE));
		assertEquals("j.l.Comparable[]", testInstance.toString(Comparable[].class));
		assertEquals("j.u.Map$Entry", testInstance.toString(Map.Entry.class));
	}
	
	private static Object[][] toStringConstructor() {
		Constructor stringWithStringArgConstructor = Reflections.getConstructor(String.class, String.class);
		Constructor stringWithCharIntIntArgsConstructor = Reflections.getConstructor(String.class, char[].class, int.class, int.class);
		return new Object[][] {
				new Object[] { stringWithStringArgConstructor, "j.l.String(j.l.String)", "java.lang.String(java.lang.String)" },
				new Object[] { stringWithCharIntIntArgsConstructor, "j.l.String(char[], int, int)", "java.lang.String(char[], int, int)" },
		};
	}
	
	@ParameterizedTest
	@MethodSource
	public void toStringConstructor(Constructor constructor, String expectedFlattenPkgePrint, String expectedFullPkgePrint) {
		assertEquals(expectedFlattenPkgePrint, FLATTEN_PACKAGE_PRINTER.toString(constructor));
		assertEquals(expectedFullPkgePrint, FULL_PACKAGE_PRINTER.toString(constructor));
	}
	
	private static Object[][] toStringMethod() {
		Method equalsIgnoreCaseMethod = Reflections.getMethod(String.class, "equalsIgnoreCase", String.class);
		Method substringMethod = Reflections.getMethod(String.class, "substring", int.class, int.class);
		return new Object[][] {
				new Object[] { equalsIgnoreCaseMethod, "j.l.String.equalsIgnoreCase(j.l.String)", "java.lang.String.equalsIgnoreCase(java.lang.String)" },
				new Object[] { substringMethod, "j.l.String.substring(int, int)", "java.lang.String.substring(int, int)" },
		};
	}
	
	@ParameterizedTest
	@MethodSource
	public void toStringMethod(Method method, String expectedFlattenPkgePrint, String expectedFullPkgePrint) {
		assertEquals(expectedFlattenPkgePrint, FLATTEN_PACKAGE_PRINTER.toString(method));
		assertEquals(expectedFullPkgePrint, FULL_PACKAGE_PRINTER.toString(method));
	}
	
	@ParameterizedTest
	@MethodSource({ "toStringConstructor", "toStringMethod" })
	public void toStringExecutable(Executable executable, String expectedFlattenPkgePrint, String expectedFullPkgePrint) {
		assertEquals(expectedFlattenPkgePrint, FLATTEN_PACKAGE_PRINTER.toString(executable));
		assertEquals(expectedFullPkgePrint, FULL_PACKAGE_PRINTER.toString(executable));
	}
	
	@Test
	public void toStringField() {
		Field valueField = Reflections.getField(String.class, "value");
		assertEquals("j.l.String.value", MemberPrinter.FLATTEN_PACKAGE_PRINTER.toString(valueField));
		assertEquals("java.lang.String.value", FULL_PACKAGE_PRINTER.toString(valueField));
		Field defaultsPropertiesField = Reflections.getField(Properties.class, "defaults");
		assertEquals("j.u.Properties.defaults", FLATTEN_PACKAGE_PRINTER.toString(defaultsPropertiesField));
		assertEquals("java.util.Properties.defaults", FULL_PACKAGE_PRINTER.toString(defaultsPropertiesField));
	}
	
	@Test
	public void toStringClass_wellKnowPackage() throws ClassNotFoundException {
		MemberPrinter testInstance = WELL_KNOWN_FLATTEN_PACKAGE_PRINTER;
		assertEquals("String", testInstance.toString(String.class));
		assertEquals("Collection", testInstance.toString(Collection.class));
		assertEquals("o.g.l.ReflectionsTest", testInstance.toString(ReflectionsTest.class));
		assertEquals("TopPackageLevelClass", testInstance.toString(Class.forName("TopPackageLevelClass")));
		assertEquals("boolean", testInstance.toString(boolean.class));
		assertEquals("boolean", testInstance.toString(Boolean.TYPE));
		assertEquals("void", testInstance.toString(Void.TYPE));
	}
}
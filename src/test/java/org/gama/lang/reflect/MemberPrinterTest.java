package org.gama.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.gama.lang.Reflections;
import org.gama.lang.ReflectionsTest;
import org.junit.jupiter.api.Test;

import static org.gama.lang.reflect.MemberPrinter.FLATTEN_PACKAGE_PRINTER;
import static org.gama.lang.reflect.MemberPrinter.FULL_PACKAGE_PRINTER;
import static org.gama.lang.reflect.MemberPrinter.WELL_KNOWN_FLATTEN_PACKAGE_PRINTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
public class MemberPrinterTest {
	
	@Test
	public void testToStringClass() throws ClassNotFoundException {
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
	
	@Test
	public void testToStringConstructor() {
		Constructor stringWithStringArgConstructor = Reflections.getConstructor(String.class, String.class);
		assertEquals("j.l.String(j.l.String)", FLATTEN_PACKAGE_PRINTER.toString(stringWithStringArgConstructor));
		assertEquals("java.lang.String(java.lang.String)", FULL_PACKAGE_PRINTER.toString(stringWithStringArgConstructor));
		Constructor stringWithCharIntIntArgsConstructor = Reflections.getConstructor(String.class, char[].class, int.class, int.class);
		assertEquals("j.l.String(char[], int, int)", FLATTEN_PACKAGE_PRINTER.toString(stringWithCharIntIntArgsConstructor));
		assertEquals("java.lang.String(char[], int, int)", FULL_PACKAGE_PRINTER.toString(stringWithCharIntIntArgsConstructor));
	}
	
	@Test
	public void testToStringMethod() {
		Method equalsIgnoreCaseMethod = Reflections.getMethod(String.class, "equalsIgnoreCase", String.class);
		assertEquals("j.l.String.equalsIgnoreCase(j.l.String)", FLATTEN_PACKAGE_PRINTER.toString(equalsIgnoreCaseMethod));
		assertEquals("java.lang.String.equalsIgnoreCase(java.lang.String)", FULL_PACKAGE_PRINTER.toString(equalsIgnoreCaseMethod));
		Method substringMethod = Reflections.getMethod(String.class, "substring", int.class, int.class);
		assertEquals("j.l.String.substring(int, int)", FLATTEN_PACKAGE_PRINTER.toString(substringMethod));
		assertEquals("java.lang.String.substring(int, int)", FULL_PACKAGE_PRINTER.toString(substringMethod));
	}
	
	@Test
	public void testToStringField() {
		Field valueField = Reflections.getField(String.class, "value");
		assertEquals("j.l.String.value", MemberPrinter.FLATTEN_PACKAGE_PRINTER.toString(valueField));
		assertEquals("java.lang.String.value", FULL_PACKAGE_PRINTER.toString(valueField));
		Field defaultsPropertiesField = Reflections.getField(Properties.class, "defaults");
		assertEquals("j.u.Properties.defaults", FLATTEN_PACKAGE_PRINTER.toString(defaultsPropertiesField));
		assertEquals("java.util.Properties.defaults", FULL_PACKAGE_PRINTER.toString(defaultsPropertiesField));
	}
	
	@Test
	public void testToStringClass_wellKnowPackage() throws ClassNotFoundException {
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
package org.gama.lang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Properties;

import org.gama.lang.Reflections;
import org.gama.lang.ReflectionsTest;
import org.junit.Test;

import static org.gama.lang.reflect.MemberPrinter.FLATTEN_PACKAGE_PRINTER;
import static org.gama.lang.reflect.MemberPrinter.FULL_PACKAGE_PRINTER;
import static org.gama.lang.reflect.MemberPrinter.WELL_KNOWN_FLATTEN_PACKAGE_PRINTER;
import static org.junit.Assert.assertEquals;

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
	}
	
	@Test
	public void testToStringMethod() {
		Method equalsIgnoreCaseMethod = Reflections.getMethod(String.class, "equalsIgnoreCase", String.class);
		assertEquals("boolean j.l.String.equalsIgnoreCase(j.l.String)", FLATTEN_PACKAGE_PRINTER.toString(equalsIgnoreCaseMethod));
		assertEquals("boolean java.lang.String.equalsIgnoreCase(java.lang.String)", FULL_PACKAGE_PRINTER.toString(equalsIgnoreCaseMethod));
	}
	
	@Test
	public void testToStringField() {
		Field valueField = Reflections.getField(String.class, "value");
		assertEquals("char[] j.l.String.value", MemberPrinter.FLATTEN_PACKAGE_PRINTER.toString(valueField));
		assertEquals("char[] java.lang.String.value", FULL_PACKAGE_PRINTER.toString(valueField));
		Field defaultsPropertiesField = Reflections.getField(Properties.class, "defaults");
		assertEquals("j.u.Properties j.u.Properties.defaults", FLATTEN_PACKAGE_PRINTER.toString(defaultsPropertiesField));
		assertEquals("java.util.Properties java.util.Properties.defaults", FULL_PACKAGE_PRINTER.toString(defaultsPropertiesField));
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
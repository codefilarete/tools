package org.codefilarete.tool.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.codefilarete.tool.Reflections;
import org.codefilarete.tool.ReflectionsTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codefilarete.tool.reflect.MemberPrinter.FLATTEN_PACKAGE_PRINTER;
import static org.codefilarete.tool.reflect.MemberPrinter.FULL_PACKAGE_PRINTER;
import static org.codefilarete.tool.reflect.MemberPrinter.WELL_KNOWN_FLATTEN_PACKAGE_PRINTER;

/**
 * @author Guillaume Mary
 */
public class MemberPrinterTest {
	
	@Test
	public void toStringClass() throws ClassNotFoundException {
		MemberPrinter testInstance = FLATTEN_PACKAGE_PRINTER;
		assertThat(testInstance.toString(String.class)).isEqualTo("j.l.String");
		assertThat(testInstance.toString(Collection.class)).isEqualTo("j.u.Collection");
		assertThat(testInstance.toString(ReflectionsTest.class)).isEqualTo("o.g.l.ReflectionsTest");
		assertThat(testInstance.toString(Class.forName("TopPackageLevelClass"))).isEqualTo("TopPackageLevelClass");
		assertThat(testInstance.toString(boolean.class)).isEqualTo("boolean");
		assertThat(testInstance.toString(Boolean.TYPE)).isEqualTo("boolean");
		assertThat(testInstance.toString(Void.TYPE)).isEqualTo("void");
		assertThat(testInstance.toString(Comparable[].class)).isEqualTo("j.l.Comparable[]");
		assertThat(testInstance.toString(Map.Entry.class)).isEqualTo("j.u.Map$Entry");
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
		assertThat(FLATTEN_PACKAGE_PRINTER.toString(constructor)).isEqualTo(expectedFlattenPkgePrint);
		assertThat(FULL_PACKAGE_PRINTER.toString(constructor)).isEqualTo(expectedFullPkgePrint);
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
		assertThat(FLATTEN_PACKAGE_PRINTER.toString(method)).isEqualTo(expectedFlattenPkgePrint);
		assertThat(FULL_PACKAGE_PRINTER.toString(method)).isEqualTo(expectedFullPkgePrint);
	}
	
	@ParameterizedTest
	@MethodSource({ "toStringConstructor", "toStringMethod" })
	public void toStringExecutable(Executable executable, String expectedFlattenPkgePrint, String expectedFullPkgePrint) {
		assertThat(FLATTEN_PACKAGE_PRINTER.toString(executable)).isEqualTo(expectedFlattenPkgePrint);
		assertThat(FULL_PACKAGE_PRINTER.toString(executable)).isEqualTo(expectedFullPkgePrint);
	}
	
	@Test
	public void toStringField() {
		Field valueField = Reflections.getField(String.class, "value");
		assertThat(MemberPrinter.FLATTEN_PACKAGE_PRINTER.toString(valueField)).isEqualTo("j.l.String.value");
		assertThat(FULL_PACKAGE_PRINTER.toString(valueField)).isEqualTo("java.lang.String.value");
		Field defaultsPropertiesField = Reflections.getField(Properties.class, "defaults");
		assertThat(FLATTEN_PACKAGE_PRINTER.toString(defaultsPropertiesField)).isEqualTo("j.u.Properties.defaults");
		assertThat(FULL_PACKAGE_PRINTER.toString(defaultsPropertiesField)).isEqualTo("java.util.Properties.defaults");
	}
	
	@Test
	public void toStringClass_wellKnowPackage() throws ClassNotFoundException {
		MemberPrinter testInstance = WELL_KNOWN_FLATTEN_PACKAGE_PRINTER;
		assertThat(testInstance.toString(String.class)).isEqualTo("String");
		assertThat(testInstance.toString(Collection.class)).isEqualTo("Collection");
		assertThat(testInstance.toString(ReflectionsTest.class)).isEqualTo("o.g.l.ReflectionsTest");
		assertThat(testInstance.toString(Class.forName("TopPackageLevelClass"))).isEqualTo("TopPackageLevelClass");
		assertThat(testInstance.toString(boolean.class)).isEqualTo("boolean");
		assertThat(testInstance.toString(Boolean.TYPE)).isEqualTo("boolean");
		assertThat(testInstance.toString(Void.TYPE)).isEqualTo("void");
	}
}
package org.gama.lang.bean;

import java.util.List;

import org.gama.lang.collection.Arrays;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Guillaume Mary
 */
public class ClassIteratorTest {
	
	public static Object[][] testNextMethodsData() {
		return new Object[][] {
				{ X.class, Arrays.asList((Class) X.class, Object.class) },
				{ Y.class, Arrays.asList((Class) Y.class, X.class, Object.class) }
		};
	}
	
	@ParameterizedTest
	@MethodSource("testNextMethodsData")
	public void testNextMethods(Class clazz, List<Class> expectedClasses) {
		ClassIterator testInstance = new ClassIterator(clazz);
		assertEquals(expectedClasses, Iterables.copy(testInstance));
	}
	
	@Test
	public void testNextMethods_stopClass() throws Exception {
		ClassIterator testInstance = new ClassIterator(Z.class, X.class);
		assertEquals(Arrays.asList((Class) Z.class, Y.class), Iterables.copy(testInstance));
	}
	
	@Test
	public void testHasNext_false() throws Exception {
		ClassIterator testInstance = new ClassIterator(X.class, X.class);
		assertFalse(testInstance.hasNext());
	}
	
	static class X { }
	
	static class Y extends X { }
	
	static class Z extends Y { }
}
package org.codefilarete.tool.bean;

import java.util.List;
import java.util.NoSuchElementException;

import org.codefilarete.tool.collection.Arrays;
import org.codefilarete.tool.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TypeIteratorTest {
	
	static Object[][] nextMethodsData() {
		return new Object[][] {
				{ X.class, Arrays.asList((Class) X.class, B.class, C.class, A.class) },
				{ Y.class, Arrays.asList((Class) Y.class, D.class, X.class, B.class, C.class, A.class) }
		};
	}
	
	@ParameterizedTest
	@MethodSource("nextMethodsData")
	void next(Class clazz, List<Class> expectedClasses) {
		TypeIterator testInstance = new TypeIterator(clazz);
		assertThat(Iterables.copy(testInstance)).isEqualTo(expectedClasses);
	}
	
	@Test
	void next_stopClass() {
		TypeIterator testInstance = new TypeIterator(Z.class, X.class);
		assertThat(Iterables.copy(testInstance)).isEqualTo(Arrays.asList((Class) Z.class, Y.class, D.class));
	}

	@Test
	void nasNext_false() {
		TypeIterator testInstance = new TypeIterator(X.class, X.class);
		assertThat(testInstance.hasNext()).isFalse();
	}

	@Test
	void next_throwsNoSuchElementException() {
		// with intermediary hasNext() invocation
		TypeIterator testInstance = new TypeIterator(Object.class);
		assertThat(testInstance.hasNext()).isFalse();
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);

		// without hasNext() invocation
		testInstance = new TypeIterator(Object.class);
		assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(testInstance::next);
	}
	
	static class X implements B, C { }
	
	static class Y extends X implements D { }
	
	static class Z extends Y { }
	
	interface A { }
	
	interface B extends A { }
	
	interface C { }
	
	interface D { }
	
}
package org.gama.lang.function;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.gama.lang.collection.Arrays;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Guillaume Mary
 */
class PredicatesTest {
	
	@Test
	void not() {
		Predicate<Object> testInstance = Predicates.not(Objects::isNull);
		assertThat(testInstance.test("")).isTrue();
		assertThat(testInstance.test(null)).isFalse();
	}
	
	@Test
	void predicate() {
		Set<String> data = Arrays.asHashSet("a", "b", "c");
		Predicate<Object> testInstance = Predicates.predicate(data::contains);
		assertThat(testInstance.test("a")).isTrue();
		assertThat(testInstance.test("d")).isFalse();
	}
	
	@Test
	void predicate_withMapper() {
		Set<String> data = Arrays.asHashSet("1", "2", "3");
		Predicate<Integer> testInstance = Predicates.predicate(Object::toString, data::contains);
		assertThat(testInstance.test(1)).isTrue();
		assertThat(testInstance.test(4)).isFalse();
	}
	
	@Test
	void toCompartor() {
		Comparator<Object> comparator = Predicates.toComparator(Objects::equals);
		assertThat(-1).isEqualTo(comparator.compare("a", "b"));
		assertThat(-1).isEqualTo(comparator.compare("b", "a"));
		assertThat(0).isEqualTo(comparator.compare("a", "a"));
	}
	
	@Test
	void acceptAll() {
		assertThat(Predicates.acceptAll().test("")).isTrue();
		assertThat(Predicates.acceptAll().test(null)).isTrue();
		assertThat(Predicates.acceptAll().test("a")).isTrue();
		assertThat(Predicates.acceptAll().test(42)).isTrue();
	}
	
	@Test
	void rejectAll() {
		assertThat(Predicates.rejectAll().test("")).isFalse();
		assertThat(Predicates.rejectAll().test(null)).isFalse();
		assertThat(Predicates.rejectAll().test("a")).isFalse();
		assertThat(Predicates.rejectAll().test(42)).isFalse();
	}
	
	@Test
	void equalsWithNull() {
		assertThat(Predicates.equalOrNull(null, null)).isTrue();
		assertThat(Predicates.equalOrNull("a", "a")).isTrue();
		assertThat(Predicates.equalOrNull("a", "b")).isFalse();
		assertThat(Predicates.equalOrNull("b", "a")).isFalse();
		assertThat(Predicates.equalOrNull(null, "b")).isFalse();
		assertThat(Predicates.equalOrNull("a", null)).isFalse();
	}
	
	@Test
	void equalsWithNull_predicate() {
		BiPredicate predicateMock = mock(BiPredicate.class);
		when(predicateMock.test(any(), any())).thenReturn(true);
		
		assertThat(Predicates.equalOrNull(null, null, predicateMock)).isTrue();
		verifyZeroInteractions(predicateMock);

		Predicates.equalOrNull("a", "a", predicateMock);
		verify(predicateMock, times(1)).test(eq("a"), eq("a"));

		Predicates.equalOrNull("a", "b", predicateMock);
		verify(predicateMock, times(1)).test(eq("a"), eq("b"));

		Predicates.equalOrNull("b", "a", predicateMock);
		verify(predicateMock, times(1)).test(eq("a"), eq("b"));
		
		clearInvocations(predicateMock);
		
		Predicates.equalOrNull(null, "b", predicateMock);
		verifyZeroInteractions(predicateMock);
		
		Predicates.equalOrNull("a", null, predicateMock);
		verifyZeroInteractions(predicateMock);
	}
	
	@Test
	void and_functions() {
		Function<Toto, String> function1 = Toto::getProp1;
		Function<Toto, Integer> function2 = Toto::getProp2;
		
		BiPredicate<Toto, Toto> testInstance = Predicates.and(function1, function2);
		assertThat(testInstance.test(new Toto("a", 1), new Toto("a", 1))).isTrue();
		assertThat(testInstance.test(new Toto("a", 1), new Toto("a", 2))).isFalse();
		assertThat(testInstance.test(new Toto("a", 1), new Toto("b", 1))).isFalse();
		// test with null
		assertThat(testInstance.test(new Toto(null, 1), new Toto(null, 1))).isTrue();
		assertThat(testInstance.test(new Toto("a", null), new Toto("a", null))).isTrue();
	}
	
	private class Toto {
		
		private final String prop1;
		private final Integer prop2;
		
		private Toto(String prop1, Integer prop2) {
			this.prop1 = prop1;
			this.prop2 = prop2;
		}
		
		public String getProp1() {
			return prop1;
		}
		
		public Integer getProp2() {
			return prop2;
		}
	}
}
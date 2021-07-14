package org.gama.lang.function;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Guillaume Mary
 */
class FunctionsTest {
	
	@Test
	void toFunction_predicateArg() {
		Function<Object, Boolean> predicateAsFunction = Functions.toFunction("hello"::equals);
		assertThat(predicateAsFunction.apply("hello")).isTrue();
		assertThat(predicateAsFunction.apply("coucou")).isFalse();
	}
	
	@Test
	void toFunction_methodArg() throws NoSuchMethodException {
		Function<Object, Object> methodAsFunction = Functions.toFunction(Integer.class.getDeclaredMethod("toString"));
		assertThat(methodAsFunction.apply(1)).isEqualTo("1");
	}
	
	@Test
	void toBiConsumer_methodArg() throws NoSuchMethodException {
		BiConsumer<Object, Object> methodAsBiConsumer = Functions.toBiConsumer(StringBuilder.class.getDeclaredMethod("append", int.class));
		StringBuilder target = new StringBuilder();
		methodAsBiConsumer.accept(target, 1);
		assertThat(target.toString()).isEqualTo("1");
	}
	
	@Test
	void asPredicate() {
		Predicate<StringBuilder> methodAsBiConsumer = Functions.asPredicate(StringBuilder::toString, "1"::equals);
		StringBuilder target = new StringBuilder();
		assertThat(methodAsBiConsumer.test(target)).isFalse();
		target.append(1);
		assertThat(methodAsBiConsumer.test(target)).isTrue();
	}
	
	@Test
	void asPredicate_givenMethod() {
		Predicate<StringBuilder> methodAsBiConsumer = Functions.asPredicate(StringBuilder::toString, "1"::equals);
		StringBuilder target = new StringBuilder();
		assertThat(methodAsBiConsumer.test(target)).isFalse();
		target.append(1);
		assertThat(methodAsBiConsumer.test(target)).isTrue();
	}
	
	@Test
	void link() {
		// given StringBuffer is transformed to a "2" String, then parsed to a 2 int
		assertThat((int) Functions.link(Object::toString, Integer::parseInt).apply(new StringBuffer("2"))).isEqualTo(2);
		// test againt null
		assertThat(Functions.link(Object::toString, Integer::parseInt).apply(null)).isNull();
		// the first function may return null, the second doesn't support null, the whole chain will return a null value
		assertThat(Functions.link(Object::toString, Integer::parseInt).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		})).isNull();
	}
	
	@Test
	void link_andThen() {
		assertThat(Functions.link(Object::toString, Integer::parseInt).andThen(i -> String.valueOf(i + 1)).apply(new Object() {
			@Override
			public String toString() {
				return "1";
			}
		})).isEqualTo("2");
		// Testing the andThen(..) method that must be null-pointer-proof too
		assertThat(Functions.link(Object::toString, Integer::parseInt).andThen(String::valueOf).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		})).isNull();
	}
	
	@Test
	void chain() {
		// StringBuffer is transformed to a "2" String, then parsed to a 2 int
		assertThat((int) Functions.chain(Object::toString, Integer::parseInt).apply(new StringBuffer("2"))).isEqualTo(2);
	}
	
	@Test
	void chain_throwNPE1() {
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Functions.chain(Object::toString, Object::toString).apply(null));
	}
	
	@Test
	void chain_throwNPE2() {
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Functions.chain(Object::toString, Object::toString).apply(new Object() {
			@Override
			public String toString() {
				return null;
			}
		}));
	}
	
	@Test
	void chain_andThen_throwNPE() {
		assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Functions.chain(Object::toString, Object::toString).andThen(String::valueOf).apply(null));
	}
	
}
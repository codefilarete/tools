package org.codefilarete.tool.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.codefilarete.tool.Reflections;
import org.codefilarete.tool.bean.ClassIterator;
import org.codefilarete.tool.bean.InterfaceIterator;
import org.codefilarete.tool.bean.MethodIterator;
import org.codefilarete.tool.exception.Exceptions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Guillaume Mary
 */
class ReadOnlySetTest {
	
	private static final Set<Method> MUTATING_METHODS = new HashSet<>();
	private static final Set<Method> ITERATOR_MUTATING_METHODS = new HashSet<>();
	private static final Set<Method> EQUALS_HASHCODE_METHODS = new HashSet<>();
	
	static {
		try {
			EQUALS_HASHCODE_METHODS.add(Set.class.getMethod("equals", Object.class));
			EQUALS_HASHCODE_METHODS.add(Set.class.getMethod("hashCode"));
			
			MUTATING_METHODS.add(Set.class.getMethod("add", Object.class));
			MUTATING_METHODS.add(Set.class.getMethod("addAll", Collection.class));
			MUTATING_METHODS.add(Set.class.getMethod("remove", Object.class));
			MUTATING_METHODS.add(Set.class.getMethod("removeIf", Predicate.class));
			MUTATING_METHODS.add(Set.class.getMethod("removeAll", Collection.class));
			MUTATING_METHODS.add(Set.class.getMethod("retainAll", Collection.class));
			MUTATING_METHODS.add(Set.class.getMethod("clear"));
			
			ITERATOR_MUTATING_METHODS.add(Iterator.class.getMethod("remove"));
		} catch (NoSuchMethodException noSuchMethodException) {
			throw new RuntimeException(noSuchMethodException);
		}
	}
	
	@Test
	void readOnlyMethodsThrowException() {
		Set delegate = Mockito.mock(Set.class);
		
		Assertions.setMaxStackTraceElementsDisplayed(100);
		
		Condition<Throwable> hasUnsupportedOperationExceptionInTrace = new Condition<>(
				thrownException -> Exceptions.findExceptionInCauses(thrownException, UnsupportedOperationException.class) != null,
				"exception stack contains " + UnsupportedOperationException.class.getSimpleName());
		
		ReadOnlySet testInstance = new ReadOnlySet(delegate);
		for (Method method : MUTATING_METHODS) {
			// we create default arguments otherwise we get IllegalArgumentException from the JVM at invoke() time
			Object[] args = new Object[method.getParameterCount()];
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++) {
				Class arg = parameterTypes[i];
				if (arg.isArray()) {
					args[i] = Array.newInstance(arg.getComponentType(), 0);
				} else {
					args[i] = Reflections.PRIMITIVE_DEFAULT_VALUES.getOrDefault(arg, null /* default value for any non-primitive Object */);
				}
			}
			assertThatThrownBy(() -> {
				try {
					method.invoke(testInstance, args);
				} catch (ReflectiveOperationException | IllegalArgumentException e) {
					throw new RuntimeException("Error executing " + Reflections.toString(method), e);
				}
			}).satisfies(hasUnsupportedOperationExceptionInTrace);
		}
	}
	
	@Test
	void listIteratorMethodsInvokeDelegateMethods() {
		Set set = Mockito.mock(Set.class);
		Iterator delegate = Mockito.mock(Iterator.class);
		Mockito.when(set.iterator()).thenReturn(delegate);
		
		Iterator testInstance = new ReadOnlySet<>(set).iterator();
		List<Class> iteratorClassInheritance = Iterables.copy(new InterfaceIterator(new ClassIterator(Iterator.class, null)));
		// ListIterator.class must be added because it is not included by inheritance iterator
		iteratorClassInheritance.add(0, Iterator.class);
		MethodIterator methodInHierarchyIterator = new MethodIterator(iteratorClassInheritance.iterator());
		FilteringIterator<Method> nonMutatingMethodsIterator = new FilteringIterator<>(methodInHierarchyIterator,
																					   m -> !ITERATOR_MUTATING_METHODS.contains(m) && !EQUALS_HASHCODE_METHODS.contains(m));
		Iterable<Method> methods = () -> nonMutatingMethodsIterator;
		int methodCount = 0;
		for (Method method : methods) {
			Object invokationResult;
			try {
				// we create default arguments otherwise we get IllegalArgumentException from the JVM at invoke() time
				Object[] args = new Object[method.getParameterCount()];
				Class<?>[] parameterTypes = method.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Class arg = parameterTypes[i];
					if (arg.isArray()) {
						args[i] = Array.newInstance(arg.getComponentType(), 0);
					} else {
						args[i] = Reflections.PRIMITIVE_DEFAULT_VALUES.getOrDefault(arg, null /* default value for any non-primitive Object */);
					}
				}
				invokationResult = method.invoke(testInstance, args);
				
				// hacking some checks because some methods don't redirect to themselves
				Object delegateResult = method.invoke(Mockito.verify(delegate), args);
				assertThat(invokationResult).isEqualTo(delegateResult);
				Mockito.clearInvocations(delegate);
				methodCount++;
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw new RuntimeException("Error executing " + Reflections.toString(method), e);
			}
		}
		// checking that iteration over methods really worked
		assertThat(methodCount).isEqualTo(3);
	}
	
	@Test
	void readOnlyIteratorMethodsThrowException() {
		Set delegate = Mockito.mock(Set.class);
		
		Condition<Throwable> hasUnsupportedOperationExceptionInTrace = new Condition<>(
				thrownException -> Exceptions.findExceptionInCauses(thrownException, UnsupportedOperationException.class) != null,
				"exception stack contains " + UnsupportedOperationException.class.getSimpleName());
		
		Iterator testInstance = new ReadOnlySet(delegate).iterator();
		for (Method method : ITERATOR_MUTATING_METHODS) {
			// we create default arguments otherwise we get IllegalArgumentException from the JVM at invoke() time
			Object[] args = new Object[method.getParameterCount()];
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (int i = 0; i < parameterTypes.length; i++) {
				Class arg = parameterTypes[i];
				if (arg.isArray()) {
					args[i] = Array.newInstance(arg.getComponentType(), 0);
				} else {
					args[i] = Reflections.PRIMITIVE_DEFAULT_VALUES.getOrDefault(arg, null /* default value for any non-primitive Object */);
				}
			}
			assertThatThrownBy(() -> {
				try {
					method.invoke(testInstance, args);
				} catch (ReflectiveOperationException | IllegalArgumentException e) {
					throw new RuntimeException("Error executing " + Reflections.toString(method), e);
				}
			}).satisfies(hasUnsupportedOperationExceptionInTrace);
		}
	}
	
}
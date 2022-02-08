package org.codefilarete.tool.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codefilarete.tool.Reflections;
import org.codefilarete.tool.bean.ClassIterator;
import org.codefilarete.tool.bean.InterfaceIterator;
import org.codefilarete.tool.bean.MethodIterator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class CollectionWrapperTest {
	
	private static final Set<Method> EQUALS_HASHCODE_METHODS = new HashSet<>();
	
	static {
		try {
			EQUALS_HASHCODE_METHODS.add(Collection.class.getMethod("equals", Object.class));
			EQUALS_HASHCODE_METHODS.add(Collection.class.getMethod("hashCode"));
		} catch (NoSuchMethodException noSuchMethodException) {
			throw new RuntimeException(noSuchMethodException);
		}
	}
	
	@Test
	void methodsInvokeDelegateMethods() {
		Collection delegate = Mockito.mock(Collection.class);
		
		CollectionWrapper testInstance = new CollectionWrapper(delegate);
		List<Class> collectionClassInheritance = Iterables.copy(new InterfaceIterator(new ClassIterator(Collection.class, null)));
		// Collection.class must be added because it is not included by inheritance iterator
		collectionClassInheritance.add(0, Collection.class);
		MethodIterator methodInHierarchyIterator = new MethodIterator(collectionClassInheritance.iterator());
		
		// We have to filter some methods because Mockito doesn't support stubbing them and throws an error
		FilteringIterator<Method> nonMutatingMethodsIterator = new FilteringIterator<>(methodInHierarchyIterator,
																					 m -> !EQUALS_HASHCODE_METHODS.contains(m));
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
				
				Object delegateResult = method.invoke(Mockito.verify(delegate), args);
				// small hack because Mockito is not consistent with itself : mock() returns an empty instances whereas verify() returns null
				if (method.equals(Collection.class.getMethod("stream"))
						|| method.equals(Collection.class.getMethod("parallelStream"))) {
					invokationResult = null;
				}
				assertThat(invokationResult).isEqualTo(delegateResult);
				
				Mockito.clearInvocations(delegate);
				methodCount++;
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw new RuntimeException("Error executing " + Reflections.toString(method), e);
			}
		}
		// checking that iteration over methods really worked
		assertThat(methodCount).isEqualTo(20);
	}
	
}
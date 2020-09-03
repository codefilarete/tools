package org.gama.lang.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Set;

import org.gama.lang.Reflections;
import org.gama.lang.bean.MethodIterator;
import org.gama.lang.test.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;

/**
 * @author Guillaume Mary
 */
class SetWrapperTest {
	
	@Test
	public void methodsInvokeDelegateMethods() {
		Set delegate = Mockito.mock(Set.class);
		SetWrapper testInstance = new SetWrapper(delegate);
		MethodIterator methodIterator = new MethodIterator(Set.class, null);
		Iterable<Method> methods = () -> methodIterator;
		int methodCount = 0;
		for (Method method : methods) {
			if (Arrays.asHashSet("equals", "hashCode").contains(method.getName())) {
				continue;
			}
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
				Assertions.assertEquals(delegateResult, invokationResult);
				Mockito.clearInvocations(delegate);
				methodCount++;
			} catch (AssertionFailedError assertionFailedError) {
				throw new AssertionFailedError("method " + method.getName(), assertionFailedError.getExpected(), assertionFailedError.getActual());
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw new RuntimeException("Error executing " + Reflections.toString(method), e);
			}
		}
		// checking that iteration over methods really worked
		org.junit.jupiter.api.Assertions.assertEquals(14, methodCount);
	}
}
package org.gama.lang.collection;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.gama.lang.Reflections;
import org.gama.lang.bean.MethodIterator;
import org.gama.lang.test.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;

/**
 * @author Guillaume Mary
 */
class MapWrapperTest {
	
	@Test
	public void methodsInvokeDelegateMethods() {
		Map delegate = Mockito.mock(Map.class);
		MapWrapper testInstance = new MapWrapper(delegate);
		MethodIterator methodIterator = new MethodIterator(Map.class, null);
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
				// small hack because Mockito is not consistent with itself : mock() returns an empty instances whereas verify() returns null
				if (method.getName().equals("values")) {
					delegateResult = new ArrayList<>();
				}
				if (method.getName().equals("entrySet")) {
					delegateResult = new HashSet<>();
				}
				if (method.getName().equals("keySet")) {
					delegateResult = new HashSet<>();
				}
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
		org.junit.jupiter.api.Assertions.assertEquals(23, methodCount);
	}
	
}
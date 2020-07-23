package org.gama.lang.sql;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.List;

import org.gama.lang.Reflections;
import org.gama.lang.bean.ClassIterator;
import org.gama.lang.bean.InterfaceIterator;
import org.gama.lang.bean.MethodIterator;
import org.gama.lang.collection.Iterables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.gama.lang.test.Assertions.assertEquals;

/**
 * @author Guillaume Mary
 */
class ResultSetWrapperTest {
	
	@Test
	public void methodsInvokeDelegateMethods() {
		ResultSet delegate = Mockito.mock(ResultSet.class);
		ResultSetWrapper testInstance = new ResultSetWrapper(delegate);
		List<Class> dataSourceClassInheritance = Iterables.copy(new InterfaceIterator(new ClassIterator(ResultSet.class, null)));
		// ResultSet.class must be added because it is not included by inheritance iterator
		dataSourceClassInheritance.add(0, ResultSet.class);
		MethodIterator methodIterator = new MethodIterator(dataSourceClassInheritance.iterator());
		Iterable<Method> methods = () -> methodIterator;
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
				assertEquals(delegateResult, invokationResult);
				Mockito.clearInvocations(delegate);
				methodCount++;
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw new RuntimeException("Error executing " + Reflections.toString(method), e);
			}
		}
		// checking that iteration over methods really worked
		Assertions.assertEquals(196, methodCount);
	}
}
package org.codefilarete.tool.sql;

import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

import org.codefilarete.tool.Reflections;
import org.codefilarete.tool.bean.ClassIterator;
import org.codefilarete.tool.bean.InterfaceIterator;
import org.codefilarete.tool.bean.MethodIterator;
import org.codefilarete.tool.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Guillaume Mary
 */
class DataSourceWrapperTest {
	
	@Test
	public void methodsInvokeDelegateMethods() {
		DataSource delegate = Mockito.mock(DataSource.class);
		DataSourceWrapper testInstance = new DataSourceWrapper(delegate);
		List<Class> dataSourceClassInheritance = Iterables.copy(new InterfaceIterator(new ClassIterator(DataSource.class, null)));
		// DataSource.class must be added because it is not included by inheritance iterator
		dataSourceClassInheritance.add(0, DataSource.class);
		MethodIterator methodIterator = new MethodIterator(dataSourceClassInheritance.iterator());
		Iterable<Method> methods = () -> methodIterator;
		int methodCount = 0;
		for (Method method : methods) {
			Object invocationResult;
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
				invocationResult = method.invoke(testInstance, args);
				Object delegateResult = method.invoke(Mockito.verify(delegate), args);
				assertThat(invocationResult).isEqualTo(delegateResult);
				Mockito.clearInvocations(delegate);
				methodCount++;
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw new RuntimeException("Error executing " + Reflections.toString(method), e);
			}
		}
		// checking that iteration over methods really worked
		assertThat(methodCount).isEqualTo(9);
	}
	
}
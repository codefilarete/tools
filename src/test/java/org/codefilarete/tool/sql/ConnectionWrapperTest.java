package org.codefilarete.tool.sql;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.HashMap;
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
class ConnectionWrapperTest {
	
	@Test
	void unwrap_parameterIsConnectionClass_returnsDelegate() throws SQLException {
		Connection delegate = Mockito.mock(Connection.class);
		ConnectionWrapper testInstance = new ConnectionWrapper(delegate);
		assertThat(testInstance.unwrap(Connection.class)).isEqualTo(delegate);
	}
	
	@Test
	void methodsInvokeDelegateMethods() {
		Connection delegate = Mockito.mock(Connection.class);
		ConnectionWrapper testInstance = new ConnectionWrapper(delegate);
		List<Class<?>> dataSourceClassInheritance = Iterables.copy(new InterfaceIterator(new ClassIterator(Connection.class, null)));
		// Connection.class must be added because it is not included by inheritance iterator
		dataSourceClassInheritance.add(0, Connection.class);
		dataSourceClassInheritance.remove(Wrapper.class);
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
				// small hack because Mockito is not consistent with itself : mock() returns an empty instances whereas verify() returns null
				if (method.getName().equals("getTypeMap")) {
					delegateResult = new HashMap<>();
				}
				assertThat(invocationResult).isEqualTo(delegateResult);
				Mockito.clearInvocations(delegate);
				methodCount++;
			} catch (ReflectiveOperationException | IllegalArgumentException e) {
				throw new RuntimeException("Error executing " + Reflections.toString(method), e);
			}
		}
		// checking that iteration over methods really worked
		assertThat(methodCount).isEqualTo(53);
	}
}
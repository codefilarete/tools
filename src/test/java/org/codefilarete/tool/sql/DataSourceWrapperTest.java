package org.codefilarete.tool.sql;

import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.List;

import org.codefilarete.tool.InvocationHandlerSupport;
import org.codefilarete.tool.Reflections;
import org.codefilarete.tool.bean.ClassIterator;
import org.codefilarete.tool.bean.InterfaceIterator;
import org.codefilarete.tool.bean.MethodIterator;
import org.codefilarete.tool.collection.Iterables;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author Guillaume Mary
 */
class DataSourceWrapperTest {
	
	@Test
	void methodsInvocation_forwardToDelegateMethods() {
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
				// we make an exception for wrapper methods since they are implemented with some necessary behavior
				// given by their specifications
				if (method.equals(Wrapper.class.getMethod("unwrap", Class.class))
						|| method.equals(Wrapper.class.getMethod("isWrapperFor", Class.class))) {
					// we can set whatever class type, it's only to avoid an NullPointerException
					args[0] = CharSequence.class;
				} else {
					Class<?>[] parameterTypes = method.getParameterTypes();
					for (int i = 0; i < parameterTypes.length; i++) {
						Class arg = parameterTypes[i];
						if (arg.isArray()) {
							args[i] = Array.newInstance(arg.getComponentType(), 0);
						} else {
							args[i] = Reflections.PRIMITIVE_DEFAULT_VALUES.getOrDefault(arg, null /* default value for any non-primitive Object */);
						}
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
	
	
	@Test
	void isWrapperFor() throws SQLException {
		DataSource mock = InvocationHandlerSupport.mock(DataSource.class);
		DataSourceWrapper testInstance = new DataSourceWrapper(mock);
		assertThat(testInstance.isWrapperFor(mock.getClass())).isTrue();
		assertThat(testInstance.isWrapperFor(CharSequence.class)).isFalse();
	}
	
	@Test
	void unwrap() throws SQLException {
		DataSource mock = InvocationHandlerSupport.mock(DataSource.class);
		DataSourceWrapper testInstance = new DataSourceWrapper(mock);
		assertThat(testInstance.unwrap(mock.getClass())).isSameAs(mock);
		
		// testing in-depth unwrapping
		testInstance = new DataSourceWrapper(testInstance);
		assertThat(testInstance.unwrap(mock.getClass())).isSameAs(mock);
		
		assertThatCode(() -> new DataSourceWrapper(null).unwrap(CharSequence.class))
				.isInstanceOf(SQLException.class)
				.hasMessage("o.c.t.s.DataSourceWrapper cannot be unwrapped as j.l.CharSequence");
	}
}
package org.codefilarete.tool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import org.codefilarete.tool.collection.Arrays;
import org.codefilarete.tool.collection.Iterables;

/**
 * InvocationHandler that does nothing. Useful to create no-operation proxy (for mocking services) or intercept a
 * particular method.
 *
 * @author Guillaume Mary
 * @see #mock(Class, Class[]) 
 */
public class InvocationHandlerSupport implements InvocationHandler {
	
	/**
	 * Method handler that ALWAYS RETURNS NULL, even for primitive type, which can leads to weird {@link NullPointerException}.
	 * Prefer usage of {@link #PRIMITIVE_INVOCATION_HANDLER}
	 */
	public static final InvocationHandler NULL_INVOCATION_PROVIDER = (proxy, method, args) -> null;
	
	/**
	 * Method handler that returns default primitive values when method returns primitive types, else will return null.
	 */
	public static final DefaultPrimitiveValueInvocationProvider PRIMITIVE_INVOCATION_HANDLER = new DefaultPrimitiveValueInvocationProvider(NULL_INVOCATION_PROVIDER);
	
	private final InvocationHandler defaultHandler;
	
	/**
	 * Will create a kind of mock since all method will do nothing and return null
	 */
	public InvocationHandlerSupport() {
		this(PRIMITIVE_INVOCATION_HANDLER);
	}
	
	/**
	 * Creates an instance that will call the given default {@link InvocationHandler} for all methods
	 * but {@link #equals(Object)} and {@link #hashCode()}.
	 *
	 * @param defaultHandler the handler for all methods except {@link #equals(Object)} and {@link #hashCode()}
	 */
	public InvocationHandlerSupport(InvocationHandler defaultHandler) {
		this.defaultHandler = defaultHandler;
	}
	
	/**
	 * Implemented to fallback on default handler, except for equals() and hashCode() methods which are called on the given proxy
	 *
	 * @param proxy the target on which method must be called
	 * @param method the method to invoke
	 * @param args the method arguments
	 * @return result of method invocation on handler given at construction time, except for equals / hashCode / toString which are handled specifically
	 * @throws Throwable in case of invocation error
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object toReturn;
		if (isEqualsMethod(method)) {
			// Consider equality between objects.
			Object other = args[0];
			if (other != null && proxy != null && Proxy.isProxyClass(proxy.getClass()) && Proxy.getInvocationHandler(proxy) == this) {
				return Proxy.isProxyClass(other.getClass()) && Proxy.getInvocationHandler(other) == this;
			} else {
				return Objects.equals(proxy, other);
			}
		} else if (isHashCodeMethod(method)) {
			if (proxy == null) {
				throw new NullPointerException("hashCode() invoked on a null reference");
			} else {
				if (Proxy.isProxyClass(proxy.getClass()) && Proxy.getInvocationHandler(proxy) == this) {	// prevent infinite loop
					return this.hashCode();
				} else {
					return proxy.hashCode();
				}
			}
		} else if (isToStringMethod(method)) {
			if (proxy == null) {
				throw new NullPointerException("toString() invoked on a null reference");
			} else {
				return this.toString();
			}
		} else {
			toReturn = defaultHandler.invoke(proxy, method, args);
		}
		return toReturn;
	}
	
	/**
	 * Eases the creation of an interface stub
	 *
	 * @param interfazz an interface
	 * @param <T> type interface
	 * @return a no-operation proxy, of type T
	 */
	public static <T> T mock(Class<T> interfazz, Class ... interfaces) {
		return (T) Proxy.newProxyInstance(InvocationHandlerSupport.class.getClassLoader(), Arrays.cat(new Class[] { interfazz }, interfaces), new InvocationHandlerSupport());
	}
	
	/**
	 * Determines whether the given method is the "equals" method.
	 */
	public static boolean isEqualsMethod(Method method) {
		return method.getName().equals("equals") && Iterables.first(method.getParameterTypes()) == Object.class && method.getReturnType() == boolean.class;
	}
	
	/**
	 * Determines whether the given method is the "hashCode" method.
	 */
	public static boolean isHashCodeMethod(Method method) {
		return method.getName().equals("hashCode") && method.getParameterTypes().length == 0 && method.getReturnType() == int.class;
	}
	
	/**
	 * Determines whether the given method is the "toString" method.
	 */
	public static boolean isToStringMethod(Method method) {
		return method.getName().equals("toString") && method.getParameterTypes().length == 0 && method.getReturnType() == String.class;
	}
	
	/**
	 * {@link InvocationHandler} that returns default primitve values for method returning primitive types, else fallbacks to its delegate
	 */
	public static class DefaultPrimitiveValueInvocationProvider implements InvocationHandler {
		
		private final InvocationHandler delegate;
		
		public DefaultPrimitiveValueInvocationProvider(InvocationHandler delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object defaultReturnTypeValue = Reflections.PRIMITIVE_DEFAULT_VALUES.get(method.getReturnType());
			return defaultReturnTypeValue != null ? defaultReturnTypeValue : delegate.invoke(proxy, method, args);
		}
	}
	
}

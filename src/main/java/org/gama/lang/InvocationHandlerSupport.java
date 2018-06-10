package org.gama.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.gama.lang.collection.Maps;

/**
 * InvocationHandler that does nothing. Usefull to create no-operation proxy (for mocking services) or intercept a
 * particular method.
 *
 * @author Guillaume Mary
 * @see #mock(Class)
 */
public class InvocationHandlerSupport implements InvocationHandler {
	
	/**
	 * Method handler that invoke method. Acts as an API bridge between {@link Method} and {@link InvocationHandler}
	 */
	public static final InvocationHandler METHOD_INVOKER = (proxy, method, args) -> method.invoke(proxy, args);
	
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
	 * @return null
	 * @throws Throwable in case of invokation error
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object toReturn;
		if (isEqualsMethod(method)) {
			// Consider equality between objects.
			toReturn = Objects.equals(proxy, args[0]);
		} else if (isHashCodeMethod(method)) {
			// Use hashCode of reference proxy.
			if (proxy == null) {
				throw new NullPointerException("hashCode() invoked on a null reference");
			} else {
				return proxy.hashCode();
			}
		} else {
			toReturn = defaultHandler.invoke(proxy, method, args);
		}
		return toReturn;
	}
	
	/**
	 * Ease the creation of an interface stub
	 *
	 * @param interfazz an interface
	 * @param <T> type interface
	 * @return a no-operation proxy, of type T
	 */
	public static <T> T mock(Class<T> interfazz) {
		return (T) Proxy.newProxyInstance(InvocationHandlerSupport.class.getClassLoader(), new Class[] { interfazz }, new InvocationHandlerSupport());
	}
	
	/**
	 * Determines whether the given method is the "equals" method.
	 */
	public static boolean isEqualsMethod(Method method) {
		return method.getName().equals("equals")
				&& method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == Object.class
				&& method.getReturnType() == boolean.class;
	}
	
	/**
	 * Determines whether the given method is the "hashCode" method.
	 */
	public static boolean isHashCodeMethod(Method method) {
		return method.getName().equals("hashCode") && method.getParameterTypes().length == 0 && method.getReturnType() == int.class;
	}
	
	/**
	 * {@link InvocationHandler} that returns default primitve values for method returning primitive types, else fallbacks to its surrogate
	 */
	public static class DefaultPrimitiveValueInvocationProvider implements InvocationHandler {
		
		private static final Map<Class, Object> PRIMITIVE_DEFAULT_VALUES = Collections.unmodifiableMap(Maps
				.asHashMap((Class) boolean.class, (Object) false)
				.add(char.class, '\u0000')
				.add(byte.class, (byte) 0)
				.add(short.class, (short) 0)
				.add(int.class, 0)
				.add(long.class, 0L)
				.add(float.class, 0F)
				.add(double.class, 0D)
		);
		
		private final InvocationHandler surrogate;
		
		public DefaultPrimitiveValueInvocationProvider(InvocationHandler surrogate) {
			this.surrogate = surrogate;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (PRIMITIVE_DEFAULT_VALUES.containsKey(method.getReturnType())) {
				return PRIMITIVE_DEFAULT_VALUES.get(method.getReturnType());
			} else {
				return surrogate.invoke(proxy, method, args);
			}
		}
	}
	
}

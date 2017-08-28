package org.gama.lang;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import org.gama.lang.collection.Iterables;

/**
 * InvocationHandler that does nothing. Usefull to create no-operation proxy (for mocking services) or intercept a
 * particular method.
 *
 * @author Guillaume Mary
 * @see #mock(Class)
 */
public class InvocationHandlerSupport implements InvocationHandler {
	
	public static final InvocationHandler METHOD_INVOKER = (proxy, method, args) -> method.invoke(proxy, args);
	
	private final InvocationHandler defaultHandler;
	
	/**
	 * Will create a kind of mock since all method will do nothing and return null
	 */
	public InvocationHandlerSupport() {
		this((proxy, method, args) -> null);
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
			// Consider equal between objects.
			toReturn = Objects.equals(proxy, args[0]);
		} else if (isHashCodeMethod(method)) {
			// Use hashCode of reference proxy.
			toReturn = System.identityHashCode(proxy);
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
		return method.getName().equals("equals") && Iterables.first(method.getParameterTypes()) == Object.class && method.getReturnType() == boolean.class;
	}
	
	/**
	 * Determines whether the given method is the "hashCode" method.
	 */
	public static boolean isHashCodeMethod(Method method) {
		return method.getName().equals("hashCode") && method.getParameterTypes().length == 0 && method.getReturnType() == int.class;
	}
}

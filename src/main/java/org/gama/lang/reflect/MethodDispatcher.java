package org.gama.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gama.lang.InvocationHandlerSupport;
import org.gama.lang.Reflections;

/**
 * A class aimed at creating proxy that will redirect some interface methods to some concrete implementation, fallbacking non-redirecting method
 * to a concrete instance.
 * This allows to "extend" or decorate an instance (the fallbacking one) with some additional feature.
 * 
 * @author Guillaume Mary
 */
public class MethodDispatcher {
	
	private final Map<Method, Object> methodsToBeIntercepted = new HashMap<>();
	
	private Object fallback;
	
	public <X> MethodDispatcher redirect(Class<X> interfazz, X extensionSurrogate) {
		for (Method method : interfazz.getMethods()) {
			methodsToBeIntercepted.put(method, extensionSurrogate);
		}
		return this;
	}
	
	public <X> X build(Class<X> interfazz) {
		assertClassImplementsInterceptingInterface(interfazz);
		assertFallbackInstanceIsNotNull();
		
		// Which ClassLoader ? Thread, fallback, this ?
		// we don't use the Thread one because it can live forever so it might lead to memory leak
		// we don't use fallback because it can be null
		ClassLoader classLoader = getClass().getClassLoader();
		Set<Class<?>> targetInterfaces = methodsToBeIntercepted.keySet().stream().map(Method::getDeclaringClass).collect(Collectors.toSet());
		// we must add the X interface to the list that will be proxied, else we'll get a "com.sun.proxy.$Proxy4 cannot be cast to X"
		targetInterfaces.add(interfazz);
		Class[] interfaces = targetInterfaces.toArray(new Class[0]);
		InvocationHandler dispatcher = new InvocationHandlerSupport((input, method, args) -> {
			Object targetInstance = methodsToBeIntercepted.getOrDefault(method, fallback);
			return invoke(targetInstance, method, args);
		});
		return (X) Proxy.newProxyInstance(classLoader, interfaces, dispatcher);
	}
	
	private <X> void assertClassImplementsInterceptingInterface(Class<X> interfazz) {
		methodsToBeIntercepted.keySet().forEach(m -> {
			if (!m.getDeclaringClass().isAssignableFrom(interfazz)) {
				throw new IllegalArgumentException(
						Reflections.toString(interfazz) + " doesn't implement " + Reflections.toString(m.getDeclaringClass()));
			}
		});
	}
	
	private void assertFallbackInstanceIsNotNull() {
		if (this.fallback == null) {
			throw new IllegalArgumentException("Fallback instance must not be null, else you'll get NullPointerException hard to debug");
		}
	}
	
	/**
	 * Indicates on which instance unregistered methods will be invoked
	 * @param fallback the instance on which unregistered methods will be invoked
	 * @return this
	 */
	public MethodDispatcher fallbackOn(Object fallback) {
		this.fallback = fallback;
		return this;
	}
	
	/**
	 * Invokes a method on a target
	 * @param target
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(Object target, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			// we rethrow the main exception so caller will not be polluted by InvocationTargetException
			throw e.getCause();
		} catch (IllegalArgumentException e) {
			// See ExceptionConverter for some code correlation
			String message = "object is not an instance of declaring class";
			IllegalArgumentException result = e;
			if (message.equals(e.getMessage())) {
				Class<?> declaringClass = method.getDeclaringClass();
				message += ": expected " + declaringClass.getName() + " but " + target.getClass().getName() + " was given";
				result = new IllegalArgumentException(message);
			}
			throw result;
		}
	}
}

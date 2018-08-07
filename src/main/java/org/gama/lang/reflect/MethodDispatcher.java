package org.gama.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gama.lang.InvocationHandlerSupport;
import org.gama.lang.Reflections;
import org.gama.lang.StringAppender;
import org.gama.lang.bean.InterfaceIterator;
import org.gama.lang.bean.MethodIterator;

/**
 * A class aimed at creating proxy that will redirect some interface methods to some concrete implementation, fallbacking non-redirecting method
 * to a concrete instance.
 * This allows to "extend" or decorate an instance (the fallbacking one) with some additional feature.
 * 
 * @author Guillaume Mary
 */
public class MethodDispatcher {
	
	private final Map<Method, Object /* target */> methodsToBeIntercepted = new HashMap<>();
	private final Map<Method, Boolean> methodsReturningProxy = new HashMap<>();
	
	private Object fallback;
	
	/**
	 * Redirects all interfazz methods to extensionSurrogate
	 * @param interfazz an interface, must be extended by the one that will be given by {@link #build(Class)} (should be a super type of it) 
	 * @param extensionSurrogate an instance implementing X so methods of X can be redirect to it
	 * @param <X> a interface type
	 * @return this
	 */
	public <X> MethodDispatcher redirect(Class<X> interfazz, X extensionSurrogate) {
		return redirect(interfazz, extensionSurrogate, false);
	}
	
	/**
	 * Same as {@link #redirect(Class, Object)} but proxy will be return by all methods invocation : result of them will be ignored.
	 * Usefull when return types of extensionSurrogate methods don't match those of {@link #build(Class)} (kind of rare case).
	 * 
	 * @param interfazz an interface, must be extended by the one that will be given by {@link #build(Class)} (should be a super type of it) 
	 * @param extensionSurrogate an instance implementing X so methods of X can be redirect to it
	 * @param <X> a interface type
	 * @return this
	 */
	public <X> MethodDispatcher redirect(Class<X> interfazz, X extensionSurrogate, boolean returnProxy) {
		Set<String> signatures = new HashSet<>();
		for (Method method : interfazz.getMethods()) {
			methodsToBeIntercepted.put(method, extensionSurrogate);
			methodsReturningProxy.put(method, returnProxy);
			signatures.add(giveSignature(method));
		}
		// we add super methods with same name (and arguments) to take "polymorphism" into account, more precisely in multiple inheritance case
		// with same method signature, only differing in return type
		// all of this is done by comparing "signatures" (lightweight one)
		MethodIterator x = new MethodIterator(new InterfaceIterator(interfazz));
		while (x.hasNext()) {
			Method next = x.next();
			if (signatures.contains(giveSignature(next))) {
				// the method is cancidate to polymorphism, we should trap it
				methodsToBeIntercepted.put(next, extensionSurrogate);
				methodsReturningProxy.put(next, returnProxy);
			}
		}
		return this;
	}
	
	/**
	 * Gives a very ligth signature of a method (no return type, no modifiers) : only name and arguments
	 * 
	 * @param method a method to get a signature
	 * @return a lightweight version of the signature of the given method
	 */
	private String giveSignature(Method method) {
		StringAppender signature = new StringAppender();
		signature.cat(method.getName());
		signature.ccat(method.getParameterTypes(), ",");
		return signature.toString();
	}
	
	public <X> X build(Class<X> interfazz) {
		assertInterceptingMethodsAreFromInterfaces();
		assertClassImplementsInterceptingInterface(interfazz);
		// Fallback instance must not be null, else you'll get NullPointerException hard to debug
		ensureFallbackInstanceIsNotNull(interfazz);
		
		// Which ClassLoader ? Thread, fallback, this ?
		// we don't use the Thread one because it can live forever so it might lead to memory leak
		// we don't use fallback because it can be null
		ClassLoader classLoader = getClass().getClassLoader();
		Set<Class<?>> targetInterfaces = methodsToBeIntercepted.keySet().stream().map(Method::getDeclaringClass).collect(Collectors.toSet());
		// we must add the X interface to the list that will be proxied, else we'll get a "com.sun.proxy.$Proxy4 cannot be cast to X"
		targetInterfaces.add(interfazz);
		// building invocationHandler : we create a holder for the proxy because it must be referenced in some cases
		Object[] proxyHolder = new Object[1];
		InvocationHandler dispatcher = new InvocationHandlerSupport((input, method, args) -> {
			Object targetInstance = methodsToBeIntercepted.getOrDefault(method, fallback);
			Object result = invoke(targetInstance, method, args);
			if (methodsReturningProxy.getOrDefault(method, false)) {
				result = proxyHolder[0];
			}
			return result;
		});
		proxyHolder[0] = Proxy.newProxyInstance(classLoader, targetInterfaces.toArray(new Class[0]), dispatcher);
		return (X) proxyHolder[0];
	}
	
	private <X> void assertClassImplementsInterceptingInterface(Class<X> interfazz) {
		methodsToBeIntercepted.keySet().forEach(m -> {
			if (!m.getDeclaringClass().isAssignableFrom(interfazz)) {
				throw new IllegalArgumentException(
						Reflections.toString(interfazz) + " doesn't implement " + Reflections.toString(m.getDeclaringClass()));
			}
		});
	}
	
	private void assertInterceptingMethodsAreFromInterfaces() {
		methodsToBeIntercepted.keySet().forEach(m -> {
			if (!m.getDeclaringClass().isInterface()) {
				throw new IllegalArgumentException("Cannot intercept concrete method : " + Reflections.toString(m));
			}
		});
	}
	
	private <X> void ensureFallbackInstanceIsNotNull(Class<X> interfazz) {
		if (this.fallback == null) {
			// we fallback equals(), hashCode(), toString(), etc on a more printable instance
			this.fallback = new Fallback(interfazz);
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
	private Object invoke(Object target, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			// we rethrow the main exception so caller will not be polluted by InvocationTargetException
			throw e.getCause();
		} catch (IllegalArgumentException e) {
			// See ExceptionConverter for some code correlation
			IllegalArgumentException result = e;
			if ("object is not an instance of declaring class".equals(e.getMessage())) {
				Class<?> declaringClass = method.getDeclaringClass();
				String message = "Wrong given instance while invoking " + Reflections.toString(method);
				message += ": expected " + declaringClass.getName() + " but " + target + " was given";
				result = new IllegalArgumentException(message, e);
			}
			throw result;
		}
	}
	
	/**
	 * Dedicated class to fallback objects so printed stack trace are more understandable
	 * Only override the {@link #toString()}
	 */
	private static class Fallback {
		
		private final Class interfazz;
		
		public Fallback(Class interfazz) {
			this.interfazz = interfazz;
		}
		
		@Override
		public String toString() {
			return interfazz.getName() + "@" + Integer.toHexString(hashCode());
		}
	}
}

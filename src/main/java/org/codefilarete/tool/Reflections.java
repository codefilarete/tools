package org.codefilarete.tool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.codefilarete.tool.bean.FieldIterator;
import org.codefilarete.tool.bean.MethodIterator;
import org.codefilarete.tool.collection.ArrayIterator;
import org.codefilarete.tool.collection.Arrays;
import org.codefilarete.tool.collection.Iterables;
import org.codefilarete.tool.collection.Maps;
import org.codefilarete.tool.collection.PairIterator;
import org.codefilarete.tool.function.ThrowingFunction;
import org.codefilarete.tool.reflect.MemberPrinter;

import static org.codefilarete.tool.Nullable.nullable;

/**
 * @author Guillaume Mary
 */
public final class Reflections {
	
	/** The system property name to manage how classes are printed when using {@link #toString(Class)} */
	public static final String FLAT_PACKAGES_OPTION_KEY = "reflections.flatPackages";
	
	/** Possible values of {@link #FLAT_PACKAGES_OPTION_KEY} : disable, false, off */
	public static final Set<String> DISABLE_FLAT_PACKAGES_OPTIONS = Collections.unmodifiableSet(Arrays.asHashSet("disable", "false", "off"));
	
	public static final Function<String, String> GET_SET_PREFIX_REMOVER = methodName -> methodName.substring(3);
	
	public static final Function<String, String> IS_PREFIX_REMOVER = methodName -> methodName.substring(2);
	
	public static final Function<Method, String> JAVA_BEAN_ACCESSOR_PREFIX_REMOVER = method -> GET_SET_PREFIX_REMOVER.apply(method.getName());
	
	public static final Function<Method, String> JAVA_BEAN_BOOLEAN_ACCESSOR_PREFIX_REMOVER = method -> IS_PREFIX_REMOVER.apply(method.getName());
	
	public static final Predicate<String> JAVA_BEAN_METHOD_NAME_CONVENTION_MATCHER = methodName ->
			onJavaBeanPropertyWrapperNameGeneric(methodName, methodName, s -> true, s -> true, s -> true, s -> false);
	
	public static final Predicate<Method> JAVA_BEAN_METHOD_CONVENTION_MATCHER = method ->
			onJavaBeanPropertyWrapperNameGeneric(method.getName(), method, s -> true, s -> true, s -> true, s -> false);
	
	/**
	 * Default values of primitive types
	 * Note that it contains Object-equivalent types because in some circumstances auto-boxing happens and you won't find your type in the Map.
	 */
	public static final Map<Class, Object> PRIMITIVE_DEFAULT_VALUES = Collections.unmodifiableMap(Maps
			.forHashMap(Class.class, Object.class)
			.add(boolean.class, false)
			.add(Boolean.class, false)
			.add(char.class, '\u0000')
			.add(Character.class, '\u0000')
			.add(byte.class, (byte) 0)
			.add(Byte.class, (byte) 0)
			.add(short.class, (short) 0)
			.add(Short.class, (short) 0)
			.add(int.class, 0)
			.add(Integer.class, 0)
			.add(long.class, 0L)
			.add(Long.class, 0L)
			.add(float.class, 0F)
			.add(Float.class, 0F)
			.add(double.class, 0D)
			.add(Double.class, 0D)
	);
	
	public static final ThreadLocal<Optional<String>> PACKAGES_PRINT_MODE_CONTEXT = ThreadLocal.withInitial(
			//AccessControlException
			() -> Optional.ofNullable(System.getProperty(FLAT_PACKAGES_OPTION_KEY)));
	
	/**
	 * Printer for {@link #toString(Class)} and {@link #toString(Method)}.
	 * Depends on {@link #FLAT_PACKAGES_OPTION_KEY} system property
	 */
	private static final Supplier<MemberPrinter> MEMBER_PRINTER = () -> {
		Optional<String> flattenPackageOption = PACKAGES_PRINT_MODE_CONTEXT.get();
		return flattenPackageOption.filter(DISABLE_FLAT_PACKAGES_OPTIONS::contains).isPresent()
				? MemberPrinter.FULL_PACKAGE_PRINTER
				: MemberPrinter.FLATTEN_PACKAGE_PRINTER;
	};
	
	/**
	 * Shortcut for {@link AccessibleObject#setAccessible(boolean)}
	 * @param accessibleObject the object to be set accessible
	 */
	@SuppressWarnings("squid:S3011")	// "Changing accessibility is security sensitive" : the goal of this method is the exact opposit of this rule 
	public static void ensureAccessible(AccessibleObject accessibleObject) {
		// we check accessibility first because setting it is a little costy
		if (!accessibleObject.isAccessible()) {
			accessibleObject.setAccessible(true);
		}
	}
	
	/**
	 * Looks for the "default constructor" (no argument) of a class
	 * @param clazz a class, not null
	 * @param <T> type of the class instances
	 * @return the default constructor of the given class (never null)
	 * @throws UnsupportedOperationException if the class doesn't have a default constructor
	 */
	public static <T> Constructor<T> getDefaultConstructor(@Nonnull Class<T> clazz) {
		try {
			return clazz.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			String reason;
			Optional<MissingDefaultConstructorReason> missingDefaultConstructorReason = giveMissingDefaultConstructorReason(clazz);
			if (missingDefaultConstructorReason.isPresent()) {
				switch (missingDefaultConstructorReason.get()) {
					case INNER_CLASS:
						reason = " because it is an inner non static class (needs an instance of the enclosing class to be constructed)";
						break;
					case INTERFACE:
						reason = " because it is an interface";
						break;
					case PRIMITIVE:
						reason = " because it is a primitive type";
						break;
					case ARRAY:
						reason = " because it is an array";
						break;
					default:
						// to enhance in case of new reason
						reason = " for undetermined reason";
				} 
			} else {
				// no reason: default sentence is right
				reason = "";
			}
			throw new UnsupportedOperationException("Class " + toString(clazz) + " has no default constructor" + reason);
		}
	}
	
	private static Optional<MissingDefaultConstructorReason> giveMissingDefaultConstructorReason(Class<?> clazz) {
		Optional<MissingDefaultConstructorReason> result = Optional.empty();
		if (isInnerClass(clazz)) {
			result = Optional.of(MissingDefaultConstructorReason.INNER_CLASS);
		} else if (Modifier.isInterface(clazz.getModifiers())) {
			result = Optional.of(MissingDefaultConstructorReason.INTERFACE);
		} else if (clazz.isPrimitive()) {
			result = Optional.of(MissingDefaultConstructorReason.PRIMITIVE);
		} else if (clazz.isArray()) {
			result = Optional.of(MissingDefaultConstructorReason.ARRAY);
		}
		return result;
	}
	
	/**
	 * @param clazz a class, not null
	 * @return true is the given class is a non-static inner class
	 */
	public static boolean isInnerClass(@Nonnull Class<?> clazz) {
		return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
	}
	
	/**
	 * Indicates if given class is static or not
	 * 
	 * @param clazz any non null {@link Class}
	 * @return true if given {@link Class} is a static one, else false
	 */
	public static boolean isStatic(@Nonnull Class<?> clazz) {
		return Modifier.isStatic(clazz.getModifiers());
	}
	
	/**
	 * Indicates if given method is static or not
	 * 
	 * @param method any non null {@link Method}
	 * @return true if given {@link Method} is a static one, else false
	 */
	public static boolean isStatic(@Nonnull Method method) {
		return Modifier.isStatic(method.getModifiers());
	}
	
	/**
	 * Indicates if given field is static or not
	 *
	 * @param field any non null {@link Field}
	 * @return true if given {@link Field} is a static one, else false
	 */
	public static boolean isStatic(@Nonnull Field field) {
		return Modifier.isStatic(field.getModifiers());
	}
	
	public static Map<String, Field> mapFieldsOnName(Class<?> clazz) {
		return Iterables.map(() -> new FieldIterator(clazz), Field::getName);
	}
	
	/**
	 * Returns the field with the given signature elements. Class hierarchy is checked also until Object class. 
	 *
	 * @param clazz the class of the field
	 * @param name the name of the field
	 * @return the found field, null possible
	 * @throws MemberNotFoundException in case of non-existing field
	 */
	@Nullable
	public static Field findField(Class<?> clazz, String name) {
		return Iterables.stream(new FieldIterator(clazz)).filter(field -> field.getName().equals(name)).findAny().orElse(null);
	}
	
	/**
	 * Same as {@link #findField(Class, String)} but throws a {@link Reflections.MemberNotFoundException}
	 * if the field is not found.
	 *
	 * @param clazz the class of the method
	 * @param name the name of the method
	 * @return the found method, never null
	 */
	public static Field getField(Class<?> clazz, String name) {
		Field field = findField(clazz, name);
		if (field == null) {
			throw new MemberNotFoundException("Field " + name + " on " + toString(clazz) + " was not found");
		}
		return field;
	}
	
	/**
	 * Returns the method with the given signature elements
	 * - Class hierarchy is checked also until Object class
	 * - Method argument compatibility is also taken into account : primitive / boxed type and super type in declared method
	 *
	 * @param clazz the class of the method
	 * @param name the name of the method
	 * @param argTypes the argument types of the method
	 * @return the found method, null possible
	 */
	@Nullable
	public static Method findMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		return Iterables.stream(new MethodIterator(clazz, null))	// having null here allows to take Object class into account
				.filter(method -> method.getName().equals(name) && method.getParameterTypes().length == argTypes.length)
				.filter(method -> {
					PairIterator<Class<?>, Class<?>> argsIterator = new PairIterator<>(new ArrayIterator<>(method.getParameterTypes()), new ArrayIterator<>(argTypes));
					boolean argsAreCompatible = true;
					while (argsAreCompatible && argsIterator.hasNext()) {
						Duo<Class<?>, Class<?>> next = argsIterator.next();
						argsAreCompatible = isAssignableFrom(next.getLeft(), next.getRight());
					}
					return argsAreCompatible;
				})
				.findAny().orElse(null);
	}
	
	/**
	 * Same as {@link #findMethod(Class, String, Class[])} but throws a {@link Reflections.MemberNotFoundException}
	 * if the method is not found.
	 * 
	 * @param clazz the class of the method
	 * @param name the name of the method
	 * @param argTypes the argument types of the method
	 * @return the found method, never null
	 * @throws MemberNotFoundException in case of non-existing method
	 */
	public static Method getMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		Method method = findMethod(clazz, name, argTypes);
		if (method == null) {
			throw new MemberNotFoundException("Method " + name + "(" + new StringAppender().ccat(argTypes, ", ").toString()
					+ ") on " + toString(clazz) + " was not found");
		}
		return method;
	}
	
	/**
	 * Returns the constructor with the given signature elements. 
	 * 
	 * @param clazz the class of the method
	 * @param argTypes the argument types of the method
	 * @return the found method, null possible
	 */
	@Nullable
	public static <C> Constructor<C> findConstructor(Class<C> clazz, Class<?>... argTypes) {
		try {
			return getConstructor(clazz, argTypes);
		} catch (MemberNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * Same as {@link #findConstructor(Class, Class[])} but throws a {@link Reflections.MemberNotFoundException}
	 * if the constructor is not found.
	 * 
	 * @param clazz the class of the method
	 * @param argTypes the argument types of the method
	 * @return the found method, never null
	 * @throws MemberNotFoundException in case of non-existing constructor
	 */
	public static <C> Constructor<C> getConstructor(Class<C> clazz, Class<?>... argTypes) {
		try {
			return clazz.getDeclaredConstructor(argTypes);
		} catch (NoSuchMethodException e) {
			MemberNotFoundException detailedException = new MemberNotFoundException("Constructor of " + toString(clazz) 
					+ " with arguments (" + new StringAppender().ccat(argTypes, ", ") + ") was not found");
			if (isInnerClass(clazz)
					&& (argTypes.length == 0 || argTypes[0] != clazz.getEnclosingClass())) {
				throw new MemberNotFoundException("Non static inner classes require an enclosing class parameter as first argument", detailedException);
			} else {
				throw detailedException;
			}
		}
	}
	
	/**
	 * Instantiates a class from its default constructor
	 * 
	 * @param clazz the target instance class
	 * @param <E> the target instance type
	 * @return a new instance of type E, never null
	 */
	public static <E> E newInstance(Class<E> clazz) {
		try {
			return newInstance(getDefaultConstructor(clazz));
		} catch (UnsupportedOperationException e) {
			throw new InvokationRuntimeException("Class " + toString(clazz) + " can't be instantiated", e);
		}
	}
	
	/**
	 * Instantiates a class from given constructor and parameters
	 *
	 * @param constructor the constructor to use
	 * @param args arguments to use with given constructor
	 * @param <E> the target instance type
	 * @return a new instance of type E, never null
	 */
	public static <E> E newInstance(Constructor<E> constructor, Object ... args) {
		try {
			// safeguard for non-accessible accessor
			Reflections.ensureAccessible(constructor);
			return constructor.newInstance(args);
		} catch (ReflectiveOperationException e) {
			if (e instanceof InstantiationException && Modifier.isAbstract(constructor.getDeclaringClass().getModifiers()) ) {
				throw new InvokationRuntimeException("Class " + toString(constructor.getDeclaringClass()) + " can't be instantiated because it is abstract", e);
			} else {
				throw new InvokationRuntimeException("Class " + toString(constructor.getDeclaringClass()) + " can't be instantiated", e);
			}
		}
	}
	
	/**
	 * Invokes method on target instance with given arguments without throwing checked exception
	 * but wrapping them into an unchecked {@link InvokationRuntimeException}
	 * 
	 * @param method the method to be invoked
	 * @param target the instance on which the method will be invoked
	 * @param args methods arguments
	 * @return method invocation result
	 */
	public static Object invoke(Method method, Object target, Object ... args) {
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
			throw new InvokationRuntimeException(e);
		}
	}
	
	/**
	 * Gives the field managed (get/set) by the given method.
	 * Name is deduced from method's one (expected to be a Java bean compliant method), so there's no absolute guarantee that the method really
	 * set or get this field.
	 *
	 * @param fieldWrapper the method supposed to get access to an attribute
	 * @return the name of the attribute expected to be manage 
	 * @see #findField(Class, String)
	 */
	public static Field wrappedField(Method fieldWrapper) {
		String fieldName = propertyName(fieldWrapper);
		return findField(fieldWrapper.getDeclaringClass(), fieldName);
	}
	
	/**
	 * Gives the name of the attribute expected to be managed (get/set) by the given method.
	 * No existence is checked : name is deduced from method's one (expected to be a Java bean compliant method)
	 *
	 * @param fieldWrapper the method supposed to get access to an attribute
	 * @return the name of the attribute expected to be manage
	 * @throws MemberNotFoundException if property name can't be determined (method is not a conventional getter/setter)
	 */
	public static String propertyName(Method fieldWrapper) {
		String propertyName = Reflections.onJavaBeanPropertyWrapperName(fieldWrapper,
				JAVA_BEAN_ACCESSOR_PREFIX_REMOVER, JAVA_BEAN_ACCESSOR_PREFIX_REMOVER, JAVA_BEAN_BOOLEAN_ACCESSOR_PREFIX_REMOVER);
		propertyName = Strings.uncapitalize(propertyName);
		return propertyName;
	}
	
	public static String propertyName(String methodName) {
		String propertyName = Reflections.onJavaBeanPropertyWrapperName(methodName,
				GET_SET_PREFIX_REMOVER, GET_SET_PREFIX_REMOVER, IS_PREFIX_REMOVER);
		propertyName = Strings.uncapitalize(propertyName);
		return propertyName;
	}
	
	/**
	 * Calls a {@link Supplier} according to the detected kind of getter or setter a method is. The implementation tests method name and parameter count
	 * (or method return type for boolean getter), hence it is a little bit more stricter that {@link #onJavaBeanPropertyWrapperName(Method, Function, Function, Function)}.
	 * This does not ensure that a real field matches the wrapped method.
	 *
	 * @param fieldWrapper the method to test against getter, setter
	 * @param getterAction the action run in case of given method is a getter
	 * @param setterAction the action run in case of given method is a setter
	 * @param booleanGetterAction the action run in case of given method is a getter of a boolean
	 * @param <E> the returned type
	 * @return the result of the called action
	 */
	public static <E> E onJavaBeanPropertyWrapper(Method fieldWrapper, Function<Method, E> getterAction, Function<Method, E> setterAction, Function<Method, E> booleanGetterAction) {
		int parameterCount = fieldWrapper.getParameterCount();
		Class<?> returnType = fieldWrapper.getReturnType();
		MemberNotFoundException exception = newEncapsulationException(() -> toString(fieldWrapper));
		return onJavaBeanPropertyWrapperName(fieldWrapper, new GetOrThrow<>(getterAction, () -> parameterCount == 0 && returnType != Void.class, () -> exception),
				new GetOrThrow<>(setterAction, () -> parameterCount == 1 && returnType == void.class, () -> exception),
				new GetOrThrow<>(booleanGetterAction, () -> parameterCount == 0 && returnType == boolean.class, () -> exception));
	}
	
	/**
	 * Calls a {@link Supplier} according to the detected kind of getter or setter a method is. The implementation only tests method name, no parameter count
	 * nor return type, as a difference to {@link #onJavaBeanPropertyWrapper(Method, Function, Function, Function)}.
	 * This does not ensure that a real field matches the wrapped method.
	 * 
	 * @param fieldWrapper the method to test against getter, setter
	 * @param getterAction the action run in case of given method is a getter
	 * @param setterAction the action run in case of given method is a setter
	 * @param booleanGetterAction the action run in case of given method is a getter of a boolean
	 * @param <E> the returned type
	 * @return the result of the called action
	 */
	public static <E> E onJavaBeanPropertyWrapperName(Method fieldWrapper, Function<Method, E> getterAction, Function<Method, E> setterAction, Function<Method, E> booleanGetterAction) {
		return onJavaBeanPropertyWrapperName(fieldWrapper.getName(), fieldWrapper, getterAction, setterAction, booleanGetterAction, () -> toString(fieldWrapper));
	}
	
	/**
	 * Same as {@link #onJavaBeanPropertyWrapper(Method, Function, Function, Function)} but with only method name as input.
	 *
	 * @param methodName the method name to test against getter, setter
	 * @param getterAction the action run in case of given method is a getter
	 * @param setterAction the action run in case of given method is a setter
	 * @param booleanGetterAction the action run in case of given method is a getter of a boolean
	 * @param <E> the returned type
	 * @return the result of the called action
	 */
	public static <E> E onJavaBeanPropertyWrapperName(String methodName, Function<String, E> getterAction, Function<String, E> setterAction, Function<String, E> booleanGetterAction) {
		return onJavaBeanPropertyWrapperName(methodName, methodName, getterAction, setterAction, booleanGetterAction, () -> methodName);
	}
	
	private static MemberNotFoundException newEncapsulationException(Supplier<String> methodName) {
		return new MemberNotFoundException("Field wrapper " + methodName.get() + " doesn't fit encapsulation naming convention");
	}
	
	/**
	 * Technical method to centralize quite similar code of {@link #onJavaBeanPropertyWrapperName(Method, Function, Function, Function)} and
	 * {@link #onJavaBeanPropertyWrapperName(String, Function, Function, Function)}.
	 * 
	 * @param methodName method name
	 * @param input real object that represents the method
	 * @param getterAction {@link Function} to be applied if method name starts with "get"
	 * @param setterAction {@link Function} to be applied if method name starts with "set"
	 * @param booleanGetterAction {@link Function} to be applied if method name starts with "is"
	 * @param inputToString printing of the input to be used in the exception message if methodName doesn't start with any of "get", "set", or "is"
	 * @param <I> real "method" object type
	 * @param <E> returned type
	 * @return the result of the called action
	 */
	private static <I, E> E onJavaBeanPropertyWrapperName(String methodName, I input,
														  Function<I, E> getterAction, Function<I, E> setterAction,
														  Function<I, E> booleanGetterAction,
														  Supplier<String> inputToString) {
		return onJavaBeanPropertyWrapperNameGeneric(methodName, input, getterAction, setterAction, booleanGetterAction,
				i -> { throw newEncapsulationException(inputToString); });
	}
	
	/**
	 * Very technical method to centralize similar code of Java bean method name convention.
	 *
	 * @param methodName method name
	 * @param input real object that represents the method
	 * @param getterAction {@link Function} to be applied if method name starts with "get"
	 * @param setterAction {@link Function} to be applied if method name starts with "set"
	 * @param booleanGetterAction {@link Function} to be applied if method name starts with "is"
	 * @param onNonCompliantName {@link Function} to be applied when method name doesn't start with any of "get", "set", or "is"
	 * @param <I> real "method" object type
	 * @param <E> returned type
	 * @return the result of the called action
	 */
	public static <I, E> E onJavaBeanPropertyWrapperNameGeneric(String methodName,
																 I input,
																 Function<I, E> getterAction,
																 Function<I, E> setterAction,
																 Function<I, E> booleanGetterAction,
																 ThrowingFunction<I, E, ? extends RuntimeException> onNonCompliantName) {
		if (methodName.startsWith("get")) {
			return getterAction.apply(input);
		} else if (methodName.startsWith("set")) {
			return setterAction.apply(input);
		} else if (methodName.startsWith("is")) {
			return booleanGetterAction.apply(input);
		} else {
			return onNonCompliantName.apply(input);
		}
	}
	
	/**
	 * Gives the "target" type of some method. Target type is the returned type for getter and the first arg type for setter.
	 * The method must follow the Java Bean Convention : starts by "get", "set", or "is", else it will throw an {@link IllegalArgumentException}
	 * 
	 * @param method not null, expected to be a getter or setter
	 * @return the target type of the getter/setter
	 */
	public static <C> Class<C> javaBeanTargetType(Method method) {
		return (Class<C>) Reflections.onJavaBeanPropertyWrapper(method, Method::getReturnType, m -> m.getParameterTypes()[0], m -> boolean.class);
	}
	
	public static String toString(Field field) {
		return MEMBER_PRINTER.get().toString(field);
	}
	
	public static String toString(Constructor<?> constructor) {
		return MEMBER_PRINTER.get().toString(constructor);
	}
	
	public static String toString(Method method) {
		return MEMBER_PRINTER.get().toString(method);
	}
	
	public static String toString(Executable executable) {
		return MEMBER_PRINTER.get().toString(executable);
	}
	
	public static String toString(Class<?> clazz) {
		return MEMBER_PRINTER.get().toString(clazz);
	}
	
	/**
	 * Almost the same as {@link Class#forName(String)} but accepts serialized form of type names.
	 * @param typeName a type name, not null
	 * @return the {@link Class} found behind typeName
	 * @throws MemberNotFoundException wrapping the one thrown by {@link Class#forName(String)}
	 * @see Class#getName()
	 */
	public static Class forName(String typeName) {
		switch (typeName) {
			case "Z":
				return boolean.class;
			case "B":
				return byte.class;
			case "C":
				return char.class;
			case "D":
				return double.class;
			case "F":
				return float.class;
			case "I":
				return int.class;
			case "J":
				return long.class;
			case "S":
				return short.class;
			case "V":
				return void.class;
			default:
				// we try default JVM type representation : see Class.getName() javadoc
				if (typeName.startsWith("L") && typeName.endsWith(";")) {
					typeName = typeName.substring(1, typeName.length() - 1);
				}
				try {
					return Class.forName(typeName);
				} catch (ClassNotFoundException e) {
					throw new Reflections.MemberNotFoundException(e);
				}
		}
	}
	
	/**
	 * Shortcut to create a JDK proxy for a given interface (implementing also some optional other interfaces) which methods are intercepted
	 * by the given {@link InvocationHandler} 
	 * 
	 * @param iface the mandatory interface that the JDK proxy must implement
	 * @param invocationHandler the intercepting code
	 * @param additionalInterfaces optional other interfaces also implemented by the proxy
	 * @param <I> main interface type, which is also the returned-proxy type
	 * @return the created JDK proxy for the given interface and given handler
	 */
	public static <I> I newProxy(Class<I> iface, InvocationHandler invocationHandler, Class<?> ... additionalInterfaces) {
		return (I) Proxy.newProxyInstance(iface.getClassLoader(), Arrays.cat(new Class[] { iface }, additionalInterfaces), invocationHandler);
	}
	
	/**
	 * Gives the wrapping class of a primitive type (Integer for int, Boolean for boolean, etc.)
	 * Caller must check if given argument is a primitive type (with {@link Class#isPrimitive()} else a {@link IllegalArgumentException} will be thrown.
	 * 
	 * @param clazz any primitive type
	 * @return the matching wrapper class of the given type
	 * @throws IllegalArgumentException if given argument is not a primitive type
	 */
	public static Class giveWrapperClass(Class<?> clazz) {
		return nullable(findWrapperClass(clazz)).getOrThrow(() -> new IllegalArgumentException("Given type is not a primitive one : " + toString(clazz)));
	}
	
	/**
	 * Gives the wrapping class of a primitive type (Integer for int, Boolean for boolean, etc.)
	 *
	 * @param clazz any primitive type
	 * @return the matching wrapper class of the given type, or null if given argument is not a primitive type
	 */
	@Nullable
	public static Class findWrapperClass(Class<?> clazz) {
		if (clazz == Integer.TYPE)
			return Integer.class;
		if (clazz == Long.TYPE)
			return Long.class;
		if (clazz == Boolean.TYPE)
			return Boolean.class;
		if (clazz == Byte.TYPE)
			return Byte.class;
		if (clazz == Character.TYPE)
			return Character.class;
		if (clazz == Float.TYPE)
			return Float.class;
		if (clazz == Double.TYPE)
			return Double.class;
		if (clazz == Short.TYPE)
			return Short.class;
		if (clazz == Void.TYPE)
			return Void.class;
		return null;
	}
	
	/**
	 * Equivalent of {@link Class#isAssignableFrom(Class)} extended to primitive type, because {@link Class#isAssignableFrom(Class)} was created at
	 * a time when autoboxing wasn't existing. See <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6456930">Class.cast does not work for primitive types</a>
	 * @param aClass any class
	 * @param otherClass any class
	 * @return true if aClass variable accepts an otherClass variable
	 */
	public static boolean isAssignableFrom(Class<?> aClass, Class<?> otherClass) {
		return aClass.isAssignableFrom(otherClass)
				|| aClass.equals(findWrapperClass(otherClass))
				|| otherClass.equals(findWrapperClass(aClass));
	}
		
	@FunctionalInterface
	private interface Checker {
		boolean check();
	}
	
	private static class GetOrThrow<E> implements Function<Method, E> {
		
		private final Function<Method, E> surrogate;
		private final Checker predicate;
		private final Supplier<RuntimeException> throwableSupplier;
		
		private GetOrThrow(Function<Method, E> surrogate, Checker predicate, Supplier<RuntimeException> s) {
			this.surrogate = surrogate;
			this.predicate = predicate;
			this.throwableSupplier = s;
		}
		
		@Override
		public E apply(Method method) {
			if (predicate.check()) {
				return surrogate.apply(method);
			} else {
				throw throwableSupplier.get();
			}
		}
	}
	
	/**
	 * Simple enumeration for a class to not have a default contructor 
	 */
	private enum MissingDefaultConstructorReason {
		INNER_CLASS,
		INTERFACE,
		PRIMITIVE,
		ARRAY
	}
	
	public static class MemberNotFoundException extends RuntimeException {
		public MemberNotFoundException(String message) {
			super(message);
		}
		
		public MemberNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public MemberNotFoundException(Throwable cause) {
			super(cause);
		}
	}
	
	public static class InvokationRuntimeException extends RuntimeException {
		
		public InvokationRuntimeException(String message) {
			super(message);
		}
		
		public InvokationRuntimeException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public InvokationRuntimeException(Throwable cause) {
			super(cause);
		}
	}
}

package org.codefilarete.tool.bean;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.codefilarete.tool.collection.ArrayIterator;
import org.codefilarete.tool.collection.Iterables;

/**
 * Iterator over (declared) methods of a class hierarchy
 * 
 * @author Guillaume Mary
 * @see InstanceMethodIterator
 */
public class MethodIterator extends InheritedElementIterator<Method> {
	
	/** Jacoco (code coverage tool) synthetic method name so it can be removed of method list because it breaks some tests */
	private static final String JACOCO_METHOD_NAME = "$jacocoInit";
	
	public MethodIterator(Class fromClass) {
		this(fromClass, Object.class);
	}
	
	public MethodIterator(Class fromClass, Class toClass) {
		this(new ClassIterator(fromClass, toClass));
	}
	
	public MethodIterator(Iterator<Class> classIterator) {
		super(classIterator);
	}
	
	/**
	 * Gives methods of a class. Default is {@link Class#getDeclaredMethods()}.
	 * Can be overriden to filter methods for instance.
	 * 
	 * @param clazz the class for which fields must be given
	 * @return an array of Field, not null
	 */
	@Override
	protected Method[] getElements(Class clazz) {
		return Iterables.stream(new ArrayIterator<>(clazz.getDeclaredMethods()))
				// we exclude Jacoco method
				.filter(declaredField -> !JACOCO_METHOD_NAME.equals(declaredField.getName())).toArray(Method[]::new);
	}
	
}

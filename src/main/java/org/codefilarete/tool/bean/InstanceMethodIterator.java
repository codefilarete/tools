package org.codefilarete.tool.bean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.codefilarete.tool.collection.ArrayIterator;
import org.codefilarete.tool.collection.Iterables;

/**
 * Iterator dedicated to non-static fields.
 *
 * @author Guillaume Mary
 */
public class InstanceMethodIterator extends MethodIterator {
	
	public InstanceMethodIterator(Class currentClass) {
		super(currentClass);
	}
	
	public InstanceMethodIterator(Class fromClass, Class toClass) {
		super(fromClass, toClass);
	}
	
	/**
	 * Overriden to keep non-static methods only
	 *
	 * @param clazz the class for which methods must be given
	 * @return non-static methods of the class only
	 */
	@Override
	protected Method[] getElements(Class clazz) {
		return Iterables.stream(new ArrayIterator<>(super.getElements(clazz)))
				// we exclude static method by contract of the method
				.filter(declaredMethod -> !Modifier.isStatic(declaredMethod.getModifiers())).toArray(Method[]::new);
	}
}

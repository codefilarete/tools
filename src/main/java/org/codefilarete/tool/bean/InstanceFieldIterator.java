package org.codefilarete.tool.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.codefilarete.tool.collection.ArrayIterator;
import org.codefilarete.tool.collection.Iterables;

/**
 * Iterator dedicated to non-static fields.
 *
 * @author Guillaume Mary
 */
public class InstanceFieldIterator extends FieldIterator {
	
	public InstanceFieldIterator(Class currentClass) {
		super(currentClass);
	}
	
	/**
	 * Overriden to keep non-static fields only
	 *
	 * @param clazz the class for which fields must be given
	 * @return non-static fields of the class only
	 */
	@Override
	protected Field[] getElements(Class clazz) {
		return Iterables.stream(new ArrayIterator<>(clazz.getDeclaredFields()))
				// we exclude static fields by contract of the method
				.filter(declaredField -> !Modifier.isStatic(declaredField.getModifiers())).toArray(Field[]::new);
	}
}

package org.gama.lang.bean;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.gama.lang.collection.ArrayIterator;
import org.gama.lang.collection.Iterables;

/**
 * Iterator over (declared) fields of a class hierarchy
 * 
 * @author Guillaume Mary
 * @see InstanceFieldIterator
 */
public class FieldIterator extends InheritedElementIterator<Field> {
	
	/** Jacoco (code coverage tool) synthetic field name so it can be removed of field list because it breaks some tests */
	private static final String JACOCO_FIELD_NAME = "$jacocoData";
	
	public FieldIterator(Class aClass) {
		this(new ClassIterator(aClass));
	}
	
	public FieldIterator(Iterator<Class> classIterator) {
		super(classIterator);
	}
	
	/**
	 * Gives fields of a class. Default is {@link Class#getDeclaredFields()}.
	 * Can be overriden to filter fields for instance.
	 *
	 * @param clazz the class for which fields must be given
	 * @return an array of Field, not null
	 */
	@Override
	protected Field[] getElements(Class clazz) {
		return Iterables.stream(new ArrayIterator<>(clazz.getDeclaredFields()))
				// we exclude Jacoco fields
				.filter(declaredField -> !JACOCO_FIELD_NAME.equals(declaredField.getName())).toArray(Field[]::new);
	}
}

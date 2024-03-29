package org.codefilarete.tool.bean;

import java.util.Iterator;
import java.util.List;

import org.codefilarete.tool.collection.ArrayIterator;
import org.codefilarete.tool.collection.Iterables;

/**
 * An {@link java.util.Iterator} that gives interfaces of each class encountered in a hierarchy.
 * 
 * @author Guillaume Mary
 */
public class InterfaceIterator extends InheritedElementIterator<Class> {
	
	public InterfaceIterator(Class aClass) {
		this(new ClassIterator(aClass, null));
	}
	
	public InterfaceIterator(Iterator<Class> classIterator) {
		super(classIterator);
	}
	
	@Override
	protected Class[] getElements(Class clazz) {
		Class[] interfaces = clazz.getInterfaces();
		List<Class> result = Iterables.copy(new ArrayIterator<>(interfaces));
		// getting all (parent) interfaces of previous interfaces: we use our own class since getInterfaces() returns super interfaces of an interface
		InterfaceIterator interfaceIterator = new InterfaceIterator(new ArrayIterator<>(interfaces));
		Iterables.copy(interfaceIterator, result);
		return result.toArray(new Class[0]);
	}
}

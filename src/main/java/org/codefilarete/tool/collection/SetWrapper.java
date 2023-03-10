package org.codefilarete.tool.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@link Set} that wraps another one and delegates all its methods to it without any additionnal feature.
 * Made for overriding only some targeted methods.
 * 
 * @author Guillaume Mary
 */
public class SetWrapper<E> implements Set<E> {
	
	private final Set<E> surrogate;
	
	public SetWrapper(Set<E> surrogate) {
		this.surrogate = surrogate;
	}
	
	public Set<E> getSurrogate() {
		return surrogate;
	}
	
	@Override
	public int size() {
		return surrogate.size();
	}
	
	@Override
	public boolean isEmpty() {
		return surrogate.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return surrogate.contains(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		return surrogate.iterator();
	}
	
	@Override
	public Object[] toArray() {
		return surrogate.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		return surrogate.toArray(a);
	}
	
	@Override
	public boolean add(E e) {
		return surrogate.add(e);
	}
	
	@Override
	public boolean remove(Object o) {
		return surrogate.remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return surrogate.containsAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return surrogate.addAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		return surrogate.retainAll(c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		return surrogate.removeAll(c);
	}
	
	@Override
	public void clear() {
		surrogate.clear();
	}
	
	@Override
	public boolean equals(Object o) {
		return surrogate.equals(o);
	}
	
	@Override
	public int hashCode() {
		return surrogate.hashCode();
	}
	
	@Override
	public Spliterator<E> spliterator() {
		return surrogate.spliterator();
	}
	
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		return surrogate.removeIf(filter);
	}
	
	@Override
	public Stream<E> stream() {
		return surrogate.stream();
	}
	
	@Override
	public Stream<E> parallelStream() {
		return surrogate.parallelStream();
	}
	
	@Override
	public void forEach(Consumer<? super E> action) {
		surrogate.forEach(action);
	}
	
	@Override
	public String toString() {
		return surrogate.toString();
	}
}

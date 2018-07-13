package org.gama.lang.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.gama.lang.Duo;
import org.gama.lang.collection.PairIterator.UntilBothIterator;

/**
 * @author Guillaume Mary
 */
public final class Iterables {
	
	/**
	 * Transform an {@link Iterator} to an {@link Iterable}
	 * @param iterator the surrogate
	 * @param <E> the content typed of the {@link Iterator}
	 * @return an {@link Iterable} whose {@link Iterable#iterator()} returns the given {@link Iterator}
	 */
	public static <E> Iterable<E> asIterable(Iterator<E> iterator) {
		return () -> iterator;
	}
	
	/**
	 * Renvoie la première valeur d'une Iterable
	 * 
	 * @return null s'il n'y a pas de valeur dans l'Iterable
	 */
	public static <E> E first(Iterable<E> iterable) {
		return first(iterable, null);
	}
	
	public static <E> E first(Iterable<E> iterable, E defaultValue) {
		if (iterable == null) {
			return defaultValue;
		} else {
			return first(iterable.iterator());
		}
	}
	
	public static <E> E first(Iterator<E> iterator) {
		return first(iterator, null);
	}
	
	public static <E> E first(Iterator<E> iterator, E defaultValue) {
		return iterator.hasNext() ? iterator.next() : defaultValue;
	}
	
	public static <E> E first(List<E> iterable) {
		return first(iterable, null);
	}
	
	public static <E> E first(List<E> iterable, E defaultValue) {
		if (Collections.isEmpty(iterable)) {
			return defaultValue;
		} else {
			return iterable.get(0);
		}
	}
	
	public static <E> E first(E[] iterable) {
		return first(iterable, null);
	}
	
	public static <E> E first(E[] iterable, E defaultValue) {
		if (Arrays.isEmpty(iterable)) {
			return defaultValue;
		} else {
			return iterable[0];
		}
	}
	
	/**
	 * Renvoie la première entrée d'une Map, intéressant pour une {@link SortedMap} ou une {@link LinkedHashMap}
	 * 
	 * @param iterable une Map, SortedMap ou LinkedHashMap sinon ça n'a pas d'intérêt
	 * @return la première entrée de la Map, null si iterable est null
	 */
	public static <K, V> Map.Entry<K, V> first(Map<K, V> iterable) {
		return first(iterable, null);
	}
	
	public static <K, V> Map.Entry<K, V> first(Map<K, V> iterable, Map.Entry<K, V> defaultValue) {
		if (iterable == null) {
			return defaultValue;
		} else {
			return first(iterable.entrySet());
		}
	}
	
	/**
	 * Renvoie la première valeur d'une Map, intéressant pour une {@link SortedMap} ou une {@link LinkedHashMap}
	 * 
	 * @param iterable une Map, SortedMap ou LinkedHashMap sinon ça n'a pas d'intérêt
	 * @return la première valeur de la Map, null si iterable est null ou s'il n'y a pas d'entrée dans la Map
	 */
	public static <V> V firstValue(Map<?, V> iterable) {
		return firstValue(iterable, null);
	}
	
	public static <V> V firstValue(Map<?, V> iterable, V defaultValue) {
		Entry<?, V> firstEntry = first(iterable);
		return firstEntry == null ? defaultValue : firstEntry.getValue();
	}
	
	public static boolean isEmpty(Iterable iterable) {
		return iterable == null
				|| ((iterable instanceof Collection) ? ((Collection) iterable).isEmpty() : iterable.iterator().hasNext()) ;
	}
	
	public static <E> E last(List<E> iterable) {
		return last(iterable, null);
	}
	
	public static <E> E last(List<E> iterable, E defaultValue) {
		if (iterable == null || iterable.isEmpty()) {
			return defaultValue;
		} else {
			return iterable.get(iterable.size()-1);
		}
	}
	
	/**
	 * Maps an {@link Iterable} on a key took as a {@link Function} of its beans. The value is also a function took on them.
	 * 
	 * @param iterable the iterable to Map, not null
	 * @param keyMapper the key provider
	 * @param valueMapper the value provider
	 * @param <T> the type of objets iterated
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @return a new (Hash)Map, never null
	 */
	public static <T, K, V> Map<K, V> map(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return map(iterable, keyMapper, valueMapper, HashMap::new);
	}
	
	/**
	 * Maps an {@link Iterable} on a key took as a {@link Function} of its beans. The value is also a function took on them.
	 *
	 * @param iterable the iterable to Map, not null
	 * @param keyMapper the key provider
	 * @param valueMapper the value provider
	 * @param target the map to be filled
	 * @param <T> the type of objets iterated
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @return a new (Hash)Map, never null
	 */
	public static <T, K, V, M extends Map<K, V>> M map(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<M> target) {
		M result = target.get();
		for (T t : iterable) {
			result.put(keyMapper.apply(t), valueMapper.apply(t));
		}
		return result;
	}
	
	/**
	 * Maps objects of an {@link Iterable} over a key took as a {@link Function} of them. 
	 * 
	 * @param iterable the iterable to Map, not null
	 * @param keyMapper the key provider
	 * @param <T> the type of objects iterated
	 * @param <K> the type of the keys
	 * @return a new (Hash)Map, never null
	 */
	public static <T, K> Map<K, T> mapIdentity(Iterable<T> iterable, Function<T, K> keyMapper) {
		return map(iterable, keyMapper, Function.identity());
	}
	
	/**
	 * Equivalent to {@link #collect(Iterable, Function, Supplier)} collecting to a {@link List}
	 * @param iterable the source
	 * @param mapper the mapping function
	 * @param <I> the input type
	 * @param <O> the output type
	 * @return the collection given by the supplier
	 */
	public static <I, O> List<O> collectToList(Iterable<I> iterable, Function<I, O> mapper) {
		return collect(iterable, mapper, ArrayList::new);
	}
	
	/**
	 * Applies a mapper over an iterable to collect information and put each result into a collection.
	 * 
	 * @param iterable the source
	 * @param mapper the mapping function
	 * @param target the supplier of resulting collection
	 * @param <I> the input type
	 * @param <O> the output type
	 * @param <C> the collecting type
	 * @return the collection given by the supplier
	 */
	public static <I, O, C extends Collection<O>> C collect(Iterable<I> iterable, Function<I, O> mapper, Supplier<C> target) {
		C result = target.get();
		iterable.forEach(i -> result.add(mapper.apply(i)));
		return result;
	}
	
	/**
	 * Copies an {@link Iterable} to a {@link List}
	 * 
	 * @param iterable an {@link Iterable}, not null
	 * @return a new {@link List}<E> containing all elements of <t>iterable</t>
	 */
	public static <E> List<E> copy(Iterable<E> iterable) {
		return (iterable instanceof Collection) ? new ArrayList<>((Collection<E>) iterable) : copy(iterable.iterator());
	}
	
	/**
	 * Copies an {@link Iterator} to a {@link List}
	 * 
	 * @param iterator an {@link Iterator}, not null
	 * @return a new {@link List}<E> containing all elements of <t>iterator</t>
	 */
	public static <E> List<E> copy(Iterator<E> iterator) {
		return copy(iterator, new ArrayList<>());
	}
	
	/**
	 * Copies an {@link Iterator} to a given {@link List}
	 * 
	 * @param iterator an {@link Iterator}, not null
	 * @return the given {@link List}
	 */
	public static <E> List<E> copy(Iterator<E> iterator, List<E> result) {
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}
	
	public static <E, C extends Collection<E>> C copy(Iterable<E> iterable, C result) {
		return copy(iterable.iterator(), result);
	}
	
	public static <E, C extends Collection<E>> C copy(Iterator<E> iterator, C result) {
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}
	
	/**
	 * Gives the intersection between two {@link Collection}s.
	 * Implementation has no particular optimization, it is based on a {@link HashSet}.
	 * 
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param <E> type of elements
	 * @return the intersection betwenn two {@link Collection}s
	 */
	public static <E> Set<E> intersect(Collection<E> c1, Collection<E> c2) {
		Set<E> copy = new HashSet<>(c1);
		copy.retainAll(c2);
		return copy; 
	}
	
	/**
	 * Gives the complement of c2 in c1 : all elements of c1 that are not member of c2
	 * Implementation has no particular optimization, it is based on a {@link HashSet#removeAll(Collection)}.
	 * 
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param <E> type of elements
	 * @return the complement of c1 in c2
	 */
	public static <E> Set<E> minus(Collection<E> c1, Collection<E> c2) {
		Set<E> copy = new HashSet<>(c1);
		copy.removeAll(c2);
		return copy; 
	}
	
	public static <K, V> Map<K, V> pair(Iterable<K> keys, Iterable<V> values) {
		return pair(keys, values, HashMap::new);
	}
	
	public static <K, V, M extends Map<K, V>> M pair(Iterable<K> keys, Iterable<V> values, Supplier<M> target) {
		UntilBothIterator<K, V> bothIterator = new UntilBothIterator<>(keys, values);
		return map(() -> bothIterator, Duo::getLeft, Duo::getRight, target);
	}
	
	/**
	 * Converts an {@link Iterator} to a {@link Stream}.
	 * If the {@link Iterator} comes from a {@link Collection}, then prefer usage of {@link Collection#stream()}
	 * 
	 * @param iterator an {@link Iterator}, not null
	 * @return a {@link Stream} than will iterate over the {@link Iterator} passed as arguemnt
	 */
	public static <E> Stream<E> stream(Iterator<E> iterator) {
		return stream(() -> iterator);
	}
	
	/**
	 * Converts an {@link Iterable} to a {@link Stream}.
	 * If the {@link Iterable} is a {@link Collection}, then prefer usage of {@link Collection#stream()}
	 * 
	 * @param iterable an {@link Iterable}, not null
	 * @return a {@link Stream} than will iterate over the {@link Iterable} passed as arguemnt
	 */
	public static <E> Stream<E> stream(Iterable<E> iterable) {
		// StreamSupport knows how to convert an Iterable to a stream
		return StreamSupport.stream(iterable.spliterator(), false);
	}
	
	public static <E> Iterable<E> filter(Iterable<E> iterable, Predicate<E> predicate) {
		Iterator<E> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			E e = iterator.next();
			if (!predicate.test(e)) {
				iterator.remove();
			}
			
		}
		return iterable;
	}
	
	/**
	 * Finds the first predicate-matching value (according to mapper) into the {@link Iterator} of an {@link Iterable}
	 *
	 * @param <I> input type
	 * @param <O> output type
	 * @param iterable the {@link Iterable} to scan
	 * @param mapper the mapper to extract the value to test
	 * @return null if no mapped values matches the predicate
	 */
	public static <I, O> Duo<I, O> find(Iterable<I> iterable, Function<I, O> mapper, Predicate<O> predicate) {
		return find(iterable.iterator(), mapper, predicate);
	}
	
	/**
	 * Finds the first predicate-matching value (according to mapper) into an {@link Iterator}
	 * 
	 * @param iterator the {@link Iterator} to scan
	 * @param mapper the mapper to extract the value to test
	 * @param <I> input type
	 * @param <O> output type
	 * @return null if no mapped values matches the predicate
	 */
	public static <I, O> Duo<I, O> find(Iterator<I> iterator, Function<I, O> mapper, Predicate<O> predicate) {
		Duo<I, O> result = null;
		boolean found = false;
		while (iterator.hasNext() && !found) {
			I step = iterator.next();
			O mapperResult = mapper.apply(step);
			if (found = predicate.test(mapperResult)) {
				result = new Duo<>(step, mapperResult);
			}
		}
		return result;
	}
	
	/**
	 * Finds the first predicate-matching value (according to mapper) into an {@link Iterator}
	 *
	 * @param iterator the {@link Iterator} to scan
	 * @param mapper the mapper to extract the value to test
	 * @param <I> input type
	 * @param <O> output type
	 * @return true if a value that matches the {@link Predicate} is found
	 */
	public static <I, O> boolean contains(Iterator<I> iterator, Function<I, O> mapper, Predicate<O> predicate) {
		return find(iterator, mapper, predicate) != null;
	}
	
	public static <E> Iterator<E> reverseIterator(List<E> list) {
		return new ReverseListIterator<>(list);
	}
	
	private Iterables() {}
}

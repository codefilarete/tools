package org.codefilarete.tool.collection;

import javax.annotation.Nullable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.codefilarete.tool.Duo;
import org.codefilarete.tool.collection.PairIterator.UntilBothIterator;
import org.codefilarete.tool.trace.MutableInt;

import static java.util.Spliterator.SIZED;

/**
 * @author Guillaume Mary
 */
public final class Iterables {
	
	/**
	 * Transforms an {@link Iterator} to an {@link Iterable}
	 * 
	 * @param iterator the delegate
	 * @param <E> the content typed of the {@link Iterator}
	 * @return an {@link Iterable} which {@link Iterable#iterator()} returns the given {@link Iterator}
	 */
	public static <E> Iterable<E> asIterable(Iterator<E> iterator) {
		return () -> iterator;
	}
	
	/**
	 * Iterates over the given {@link Iterable} by chunks of <code>chunkSize</code> size.
	 * This method is made to avoid boilerplate code that wraps "batch treatments"; thus, each chunk is given to the <code>consumer</code> argument,
	 * even the very last one even if it is not of <code>chunkSize</code>.
	 * Hence, <code>beforeConsumer</code> is called only once before iterating over all chunks, because it is expected to prepare a
	 * "context" that is passed back to the chunk consumer.
	 * As well, <code>afterConsumer</code> is called only once after all chunks iteration, because it is expected to clean this "context".
	 *
	 * @param iterable the iterable to be iterated over by partition of chunkSize
	 * @param beforeAllConsumer code to be invoked before the treatment of all chunks
	 * @param chunkSize the size of the chunk to be passed to the consumer
	 * @param beforeConsumer code to be invoked once before all chunks that have chunkSize size, and the remaining one
	 * @param consumer the consumer to invoke with each chunk
	 * @param afterConsumer code to be invoked once after all chunks that have chunkSize size, and the remaining one
	 * @param <E> the iterable element type
	 * @param <O> the context type
	 */
	public static <E, O> void forEachChunk(Iterable<E> iterable,
										   int chunkSize,
										   Consumer<List<List<E>>> beforeAllConsumer,
										   Function<Integer, O> beforeConsumer,
										   BiConsumer<O, ? super List<E>> consumer,
										   Consumer<O> afterConsumer) {
		List<List<E>> chunks = chunk(iterable, chunkSize);
		if (!chunks.isEmpty()) {
			beforeAllConsumer.accept(chunks);
			List<E> lastChunk = last(chunks, java.util.Collections.emptyList());
			if (lastChunk.size() != chunkSize) {
				chunks = Iterables.cutTail(chunks);
			} else {
				lastChunk = java.util.Collections.emptyList();
			}
			if (!chunks.isEmpty()) {
				O initObject = beforeConsumer.apply(chunkSize);
				chunks.forEach(chunk -> consumer.accept(initObject, chunk));
				afterConsumer.accept(initObject);
			}
			
			// last packet treatment (chunk size may be different)
			if (!lastChunk.isEmpty()) {
				O initObject = beforeConsumer.apply(lastChunk.size());
				consumer.accept(initObject, lastChunk);
				afterConsumer.accept(initObject);
			}
		}
	}
	
	/**
	 * Gives the size of the given {@link Iterable}.
	 * Throws an exception if its size can't be known (if it is not a {@link Collection} and its {@link Spliterator} doesn't have a
	 * {@link Spliterator#getExactSizeIfKnown()}).
	 *
	 * @param iterable the object of which we need the size
	 * @return the size of the given {@link Iterable}
	 */
	public static int size(Iterable<?> iterable) {
		return size(iterable, () -> {
			throw new UnsupportedOperationException("Can't give size of Iterable, make it override Spliterator.getExactSizeIfKnown() or support Spliterator.SIZED");
		});
	}
	
	/**
	 * Gives the size of the given {@link Iterable} or returns a default size if its size can't be known (if it is not a {@link Collection} and
	 * its {@link Spliterator} doesn't have a {@link Spliterator#getExactSizeIfKnown()}).
	 *
	 * @param iterable the object of which we need the size
	 * @param defaultSizeSupplier supplier of a the value to return if the size can't be computed
	 * @return the size of the given {@link Iterable}
	 */
	public static int size(Iterable<?> iterable, Supplier<Integer> defaultSizeSupplier) {
		if (iterable instanceof Collection) {
			return ((Collection<?>) iterable).size();
		} else {
			Spliterator<?> spliterator = iterable.spliterator();
			int exactSizeIfKnown = (int) spliterator.getExactSizeIfKnown();
			if (exactSizeIfKnown == -1) {
				return defaultSizeSupplier.get();
			} else {
				return exactSizeIfKnown;
			}
		}
	}
	
	/**
	 * @param iterable a nullable {@link Iterable}
	 * @return the first element of the argument or null if argument is empty
	 */
	@Nullable
	public static <E> E first(@Nullable Iterable<E> iterable) {
		return first(iterable, null);
	}
	
	/**
	 * @param iterable a nullable {@link Iterable}
	 * @return the first element of the argument or the given default value if argument is empty
	 */
	public static <E> E first(@Nullable Iterable<E> iterable, E defaultValue) {
		if (iterable == null) {
			return defaultValue;
		} else {
			return first(iterable.iterator());
		}
	}
	
	/**
	 * @param iterator a nullable {@link Iterator}
	 * @return the first element of the argument or null if argument is empty
	 */
	@Nullable
	public static <E> E first(Iterator<E> iterator) {
		return first(iterator, null);
	}
	
	/**
	 * @param iterator a nullable {@link Iterator}
	 * @return the first element of the argument or the given default value if argument is empty
	 */
	public static <E> E first(Iterator<E> iterator, E defaultValue) {
		return iterator != null && iterator.hasNext() ? iterator.next() : defaultValue;
	}
	
	/**
	 * Optimized version of {@link #first(Iterable)} for {@link List} : will use {@code get(0)} on the list to get first element (instead of hasNext())
	 *
	 * @param iterable a nullable {@link List}
	 * @return the first element of the argument or null if argument is empty
	 */
	@Nullable
	public static <E> E first(List<E> iterable) {
		return first(iterable, null);
	}
	
	/**
	 * Optimized version of {@link #first(Iterable)} for {@link List} : will use {@code get(0)} on the list to get first element (instead of hasNext())
	 * 
	 * @param iterable a nullable {@link List}
	 * @return the first element of the argument or the given default value if argument is empty
	 */
	public static <E> E first(List<E> iterable, E defaultValue) {
		if (Collections.isEmpty(iterable)) {
			return defaultValue;
		} else {
			return iterable.get(0);
		}
	}
	
	/**
	 * Optimized version of {@link #first(Iterable)} for arrays : will use {@code [0]} on the array to get first element (instead of hasNext())
	 *
	 * @param array a nullable array
	 * @return the first element of the argument or null if argument is empty
	 */
	@Nullable
	public static <E> E first(E[] array) {
		return first(array, null);
	}
	
	/**
	 * Optimized version of {@link #first(Iterable)} for arrays : will use {@code [0]} on the array to get first element (instead of hasNext())
	 *
	 * @param array a nullable array
	 * @return the first element of the argument or the given default value if argument is empty
	 */
	public static <E> E first(E[] array, E defaultValue) {
		if (Arrays.isEmpty(array)) {
			return defaultValue;
		} else {
			return array[0];
		}
	}
	
	/**
	 * Returns the first entry of a {@link Map}, only makes sense on a {@link SortedMap} or a {@link LinkedHashMap}
	 * 
	 * @param iterable a Map : Sorted or Linked (else it has no purpose)
	 * @return the first {@link Entry} of the Map, or null if argument is empty or null
	 */
	@Nullable
	public static <K, V> Map.Entry<K, V> first(@Nullable Map<K, V> iterable) {
		return first(iterable, null);
	}
	
	/**
	 * Returns the first entry of a {@link Map}, only makes sense on a {@link SortedMap} or a {@link LinkedHashMap}
	 *
	 * @param iterable a Map : Sorted or Linked (else it has no purpose)
	 * @return the first {@link Entry} of the Map, or the given default {@link Entry} if argument is empty or null
	 */
	public static <K, V> Map.Entry<K, V> first(@Nullable Map<K, V> iterable, Map.Entry<K, V> defaultValue) {
		if (iterable == null) {
			return defaultValue;
		} else {
			return first(iterable.entrySet());
		}
	}
	
	/**
	 * Returns the first value of a {@link Map}, only makes sense on a {@link SortedMap} or a {@link LinkedHashMap}
	 *
	 * @param iterable a Map : Sorted or Linked (else it has no purpose)
	 * @return the first value of the Map, null if argument is null
	 */
	@Nullable
	public static <V> V firstValue(@Nullable Map<?, V> iterable) {
		return firstValue(iterable, null);
	}
	
	/**
	 * Returns the first value of a {@link Map}, only makes sense on a {@link SortedMap} or a {@link LinkedHashMap}
	 *
	 * @param iterable a Map : Sorted or Linked (else it has no purpose)
	 * @return the first value of the Map, or the given default value if argument is empty or null
	 */
	@Nullable
	public static <V> V firstValue(@Nullable Map<?, V> iterable, V defaultValue) {
		Entry<?, V> firstEntry = first(iterable);
		return firstEntry == null ? defaultValue : firstEntry.getValue();
	}
	
	/**
	 * Indicates if an {@link Iterable} is empty or not.
	 * Implementation is optimized for {@link Collection}s by using {@link Collection#isEmpty()}, else will use {@link Iterator#hasNext()}.
	 * 
	 * @param iterable a nullable {@link Iterable}
	 * @return true if given argument is null or has no element
	 */
	public static boolean isEmpty(@Nullable Iterable iterable) {
		return iterable == null
				|| ((iterable instanceof Collection) ? ((Collection) iterable).isEmpty() : !iterable.iterator().hasNext()) ;
	}
	
	/**
	 * @param iterable a nullable {@link List}
	 * @return the last element of the argument or null if argument is empty or null
	 */
	@Nullable
	public static <E> E last(@Nullable List<E> iterable) {
		return last(iterable, null);
	}
	
	/**
	 * @param iterable a nullable {@link List}
	 * @return the last element of the argument or the given default value if argument is empty or null
	 */
	public static <E> E last(@Nullable List<E> iterable, E defaultValue) {
		if (iterable == null || iterable.isEmpty()) {
			return defaultValue;
		} else {
			return iterable.get(iterable.size() - 1);
		}
	}
	
	/**
	 * @param iterable a nullable {@link Iterable}
	 * @return the last element of the argument or null
	 */
	public static <E> E last(@Nullable Iterable<E> iterable) {
		return last(iterable, null);
	}
	
	/**
	 * @param iterable a nullable {@link Iterable}
	 * @param defaultValue value to be return if iterable is empty
	 * @return the last element of the argument or the given default value if argument is empty or null
	 */
	public static <E> E last(@Nullable Iterable<E> iterable, @Nullable E defaultValue) {
		if (iterable == null) {
			return defaultValue;
		} else {
			Iterator<E> iterator = iterable.iterator();
			E result = null;
			if (!iterator.hasNext()) {
				result = defaultValue;
			}
			while (iterator.hasNext()) {
				result = iterator.next();
			}
			return result;
		}
	}
	
	/**
	 * Collects some elements from an iterable until a given element
	 * 
	 * @param iterable the iterable to scan
	 * @param untilExcluded stopping element
	 * @param <E> elements type
	 * @return firsts elements of the iterable that differ from {@code untilExcluded}, in the iteration order, without {@code untilExcluded}
	 */
	public static <E> List<E> head(Iterable<E> iterable, E untilExcluded) {
		List<E> result = new ArrayList<>();
		for (E e : iterable) {
			if (untilExcluded.equals(e)) {
				break;
			} else {
				result.add(e);
			}
		}
		return result;
	}
	
	/**
	 * Collects the <code>count</code> first elements of given {@link List}.
	 * Shortcut for {@link List#subList(int, int)}
	 *
	 * @param iterable the iterable to extract elements from
	 * @param count number of elements to be collected
	 * @param <E> elements type
	 * @return firsts elements of given iterable
	 */
	public static <E> List<E> head(List<E> iterable, int count) {
		return new ArrayList<>(iterable.subList(0, count));
	}
	
	/**
	 * Returns the head of given {@link List} without its last element.
	 * Returns a copy of given argument
	 *
	 * @param list the {@link List} to extract elements from
	 * @return a copy of given argument without its very last element
	 * @param <E> element type
	 * @see #cutHead(List)
	 */
	public static <E> List<E> cutTail(List<E> list) {
		return cutTail(list, 1);
	}
	
	/**
	 * Returns the head of given {@link List}, without its last <code>elementCount</code> elements.
	 * Returns a copy of given argument
	 *
	 * @param list the {@link List} to extract elements from
	 * @return a copy of given argument without its last elements
	 * @param <E> element type
	 * @see #cutHead(List, int)
	 */
	public static <E> List<E> cutTail(List<E> list, int elementCount) {
		return new ArrayList<>(list.subList(0, list.size() - elementCount));
	}
	
	/**
	 * Returns the tail of given {@link List} without its first element.
	 * Returns a copy of given argument
	 *
	 * @param list the {@link List} to extract elements from
	 * @return a copy of given argument without its very first element
	 * @param <E> element type
	 * @see #cutTail(List)}
	 */
	public static <E> List<E> cutHead(List<E> list) {
		return cutHead(list, 1);
	}
	
	/**
	 * Returns the tail of given {@link List}, without its first <code>elementCount</code> elements.
	 * Returns a copy of given argument
	 *
	 * @param list the {@link List} to extract elements from
	 * @return a copy of given argument without its first elements
	 * @param <E> element type
	 * @see #cutTail(List, int)}
	 */
	public static <E> List<E> cutHead(List<E> list, int elementCount) {
		return new ArrayList<>(list.subList(elementCount, list.size()));
	}
	
	/**
	 * Maps an {@link Iterable} on a key took as a {@link Function} of its beans. The value is also a function took on them.
	 * 
	 * @param iterable the iterable to map
	 * @param keyMapper the key provider
	 * @param valueMapper the value provider
	 * @param <T> iterated objets type
	 * @param <K> keys type
	 * @param <V> values type
	 * @return a new (Hash)Map
	 */
	public static <T, K, V> Map<K, V> map(Iterable<T> iterable,
										  Function<? super T, ? extends K> keyMapper,
										  Function<? super T, ? extends V> valueMapper) {
		return map(iterable, keyMapper, valueMapper, HashMap::new);
	}
	
	/**
	 * Maps an {@link Iterable} on a key took as a {@link Function} of its beans. The value is also a function took on them.
	 *
	 * @param iterable the iterable to map
	 * @param keyMapper the key provider
	 * @param valueMapper the value provider
	 * @param target the map to be filled
	 * @param <T> iterated objets type
	 * @param <K> keys type
	 * @param <V> values type
	 * @param <M> returned {@link Map} type
	 * @return a new {@link Map} of type M filled with key and values from given parameters
	 */
	public static <T, K, V, M extends Map<K, V>> M map(Iterable<T> iterable,
													   Function<? super T, ? extends K> keyMapper,
													   Function<? super T, ? extends V> valueMapper,
													   Supplier<M> target) {
		M result = target.get();
		for (T t : iterable) {
			result.put(keyMapper.apply(t), valueMapper.apply(t));
		}
		return result;
	}
	
	/**
	 * Maps objects of an {@link Iterable} over a key took as a {@link Function} of them. 
	 * 
	 * @param iterable the iterable to map
	 * @param keyMapper the key provider
	 * @param <T> iterated objets type
	 * @param <K> keys type
	 * @return a new (Hash)Map
	 */
	public static <T, K> Map<K, T> map(Iterable<T> iterable, Function<? super T, ? extends K> keyMapper) {
		return map(iterable, keyMapper, Function.identity());
	}
	
	/**
	 * Maps an {@link Iterable} on a key took as a {@link Function} of its beans. The value is also a function took on them.
	 *
	 * @param iterable the iterable to map
	 * @param keyMapper the key provider
	 * @param target the map to be filled
	 * @param <T> iterated objets type
	 * @param <K> keys type
	 * @param <M> returned {@link Map} type
	 * @return a new {@link Map} of type M filled with key and values from given parameters
	 */
	public static <T, K, M extends Map<K, T>> M map(Iterable<T> iterable, Function<? super T, ? extends K> keyMapper, Supplier<M> target) {
		return map(iterable, keyMapper, Function.identity(), target);
	}
	
	/**
	 * Equivalent to {@link #collect(Iterable, Function, Supplier)} collecting to a {@link List}
	 * 
	 * @param iterable the source
	 * @param mapper the mapping function
	 * @param <I> input type
	 * @param <O> output type
	 * @return the collection given by the supplier
	 */
	public static <I, O> List<O> collectToList(Iterable<? extends I> iterable, Function<I, O> mapper) {
		return collect(iterable, mapper, ArrayList::new);
	}
	
	/**
	 * Returns an {@link Iterable} as a {@link List}: return it if it's one, else creates a new one from it.
	 *
	 * @param c an {@link Iterable}
	 * @param <E> contained element type
	 * @return a new {@link List} or the Collection if it's one
	 */
	public static <E> List<E> asList(Iterable<E> c) {
		return c instanceof List ? (List<E>) c : copy(c);
	}
	
	/**
	 * Applies a mapper over an {@link Iterable} and puts each object into a collection.
	 * 
	 * @param iterable the source
	 * @param mapper the mapping function
	 * @param target the supplier of resulting collection
	 * @param <I> the input type
	 * @param <O> the output type
	 * @param <C> the collecting type
	 * @return the collection given by the supplier
	 */
	public static <I, O, C extends Collection<O>> C collect(Iterable<? extends I> iterable, Function<I, O> mapper, Supplier<C> target) {
		return collect(iterable, i -> true, mapper, target);
	}
	
	/**
	 * Applies a filter and a mapper over an {@link Iterable} to collect objects and puts them into a collection.
	 *
	 * @param iterable the source
	 * @param acceptFilter the accepting condition
	 * @param mapper the mapping function
	 * @param target the supplier of resulting collection
	 * @param <I> the input type
	 * @param <O> the output type
	 * @param <C> the collecting type
	 * @return the collection given by the supplier
	 */
	public static <I, O, C extends Collection<O>> C collect(Iterable<? extends I> iterable, Predicate<I> acceptFilter, Function<I, O> mapper,
															Supplier<C> target) {
		return collect(iterable, acceptFilter, mapper, x -> true, target);
	}
	
	/**
	 * Applies a filter and a mapper over an {@link Iterable} to collect objects and puts them into a collection if it's accepted by a second
	 * predicate.
	 *
	 * @param iterable the source
	 * @param acceptFilter the accepting condition
	 * @param mapper the mapping function
	 * @param mappedValueFilter the accepting condition for the mapped value
	 * @param target the supplier of resulting collection
	 * @param <I> the input type
	 * @param <O> the output type
	 * @param <C> the collecting type
	 * @return the collection given by the supplier
	 */
	public static <I, O, C extends Collection<O>> C collect(Iterable<? extends I> iterable, Predicate<I> acceptFilter, Function<I, O> mapper,
															Predicate<O> mappedValueFilter, Supplier<C> target) {
		C result = target.get();
		for (I pawn : iterable) {
			if (acceptFilter.test(pawn)) {
				O mappedPawn = mapper.apply(pawn);
				if (mappedValueFilter.test(mappedPawn)) {
					result.add(mappedPawn);
				}
			}
		}
		return result;
	}
	
	/**
	 * Copies an {@link Iterable} to a {@link List}
	 * 
	 * @param iterable an {@link Iterable}, not null
	 * @return a new {@link List}<E> containing all elements of <t>iterable</t>
	 */
	public static <E> List<E> copy(Iterable<? extends E> iterable) {
		return copy(iterable, new ArrayList<>());
	}
	
	/**
	 * Copies an {@link Iterator} to a {@link List}
	 * 
	 * @param iterator an {@link Iterator}, not null
	 * @return a new {@link List}<E> containing all elements of <t>iterator</t>
	 */
	public static <E> List<E> copy(Iterator<? extends E> iterator) {
		return copy(iterator, new ArrayList<>());
	}
	
	/**
	 * Copies an {@link Iterable} to a given {@link Collection}
	 *
	 * @param iterable an {@link Iterable}, not null
	 * @return the given {@link List}
	 */
	public static <E, C extends Collection<E>> C copy(Iterable<? extends E> iterable, C result) {
		if (iterable instanceof Collection) {
			result.addAll((Collection<E>) iterable);
		} else {
			copy(iterable.iterator(), result);
		}
		return result;
	}
	
	/**
	 * Copies an {@link Iterator} to a given {@link List}
	 *
	 * @param iterator an {@link Iterator}, not null
	 * @return the given {@link List}
	 */
	public static <E, C extends Collection<E>> C copy(Iterator<? extends E> iterator, C result) {
		iterator.forEachRemaining(result::add);
		return result;
	}
	
	/**
	 * Gives the intersection between two {@link Collection}s.
	 * Implementation has no particular optimization, it is based on a {@link HashSet}, hence comparison is based on
	 * {@link Object#equals(Object)}).
	 * 
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param <E> type of elements
	 * @return the intersection between two {@link Collection}s
	 */
	public static <E> Set<E> intersect(Collection<? extends E> c1, Collection<? extends E> c2) {
		Set<E> copy = new HashSet<>(c1);
		copy.retainAll(c2);
		return copy;
	}
	
	/**
	 * Gives the intersection between two {@link Collection}s by comparing objects with a {@link Comparator}
	 * Implementation has no particular optimization, it is based on a {@link java.util.TreeSet}.
	 *
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param <E> type of elements
	 * @return the intersection between two {@link Collection}s according to the given {@link Comparator}
	 */
	public static <E, T> Set<E> intersect(Collection<E> c1, Collection<T> c2, BiPredicate<E, T> predicate) {
		Set<E> copy = new HashSet<>(c1);
		copy.removeIf(e -> !contains(c2, c -> predicate.test(e, c)));
		return copy;
	}
	
	/**
	 * Gives the complement of c2 in c1 : all elements of c1 that are not member of c2
	 * Implementation has no particular optimization, it is based on a {@link HashSet#removeAll(Collection)} and as such
	 * is based on {@link Object#equals}
	 * 
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param <E> type of elements
	 * @return the complement of c1 in c2
	 */
	public static <E> Set<E> minus(Collection<E> c1, Collection<E> c2) {
		return minus(c1, c2, (Function<Collection<E>, HashSet<E>>) HashSet::new);
	}
	
	/**
	 * Gives the complement of c2 in c1 : all elements of c1 that are not member of c2
	 * Implementation has no particular optimization, it is based on a {@link HashSet#removeAll(Collection)} and as such
	 * is based on {@link Object#equals}.
	 * Result is pushed to given resultHolder.
	 *
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param resultHolder function expected to give resulting {@link Set}. Argument is c1.
	 * @param <E> type of elements
	 * @return the complement of c1 in c2
	 */
	public static <E, S extends Set<E>> S minus(Collection<E> c1, Collection<E> c2, Function<Collection<E>, S> resultHolder) {
		S copy = resultHolder.apply(c1);
		copy.removeAll(c2);
		return copy;
	}
	
	/**
	 * Gives the complement of c2 in c1 : all elements of c1 that are not member of c2 by comparing objects with a {@link Comparator}
	 * Implementation has no particular optimization, it is based on a {@link java.util.TreeSet}.
	 *
	 * @param c1 a {@link Collection}, not null
	 * @param c2 a {@link Collection}, not null
	 * @param <E> type of elements
	 * @return the complement of c1 in c2
	 */
	public static <E, T> Set<E> minus(Collection<E> c1, Collection<T> c2, BiPredicate<E, T> predicate) {
		Set<E> copy = new HashSet<>(c1);
		copy.removeIf(e -> contains(c2, c -> predicate.test(e, c)));
		return copy;
	}
	
	public static <E> boolean equals(Iterable<E> it1, Iterable<E> it2, BiPredicate<E, E> predicate) {
		if (it1 == it2)
			return true;
		
		Iterator<E> e1 = it1.iterator();
		Iterator<E> e2 = it2.iterator();
		while (e1.hasNext() && e2.hasNext()) {
			E o1 = e1.next();
			E o2 = e2.next();
			if ((o1 == null ^ o2 == null) || !predicate.test(o1, o2)) {
				return false;
			}
		}
		return !(e1.hasNext() || e2.hasNext());
	}
	
	
	/**
	 * Puts 2 {@link Iterator}s side by side in a {@link Map}.
	 * If {@link Iterator}s are not of same length then null elements will be used into the {@link Map}.
	 * This leads to a cumulative null key if {@code values} {@link Iterator} is larger than {@code keys} {@link Iterator} because all overflowing
	 * elements will be put onto a null key.
	 * 
	 * @param keys {@link Iterator} which elements will be used as keys
	 * @param values {@link Iterator} which elements will be used as values
	 * @param <K> type of keys
	 * @param <V> type of values
	 * @return a new {@link HashMap} composed of keys and values from both {@link Iterator}s
	 */
	public static <K, V> Map<K, V> pair(Iterable<K> keys, Iterable<V> values) {
		return pair(keys, values, HashMap::new);
	}
	
	/**
	 * Puts 2 {@link Iterator}s side by side in a {@link Map}.
	 * If {@link Iterator}s are not of same length then null elements will be used into the {@link Map}.
	 * This leads to a cumulative null key if {@code values} {@link Iterator} is larger than {@code keys} {@link Iterator} because all overflowing
	 * elements will be put onto a null key.
	 *
	 * @param keys {@link Iterator} which elements will be used as keys
	 * @param values {@link Iterator} which elements will be used as values
	 * @param target a provider of a {@link Map}
	 * @param <K> type of keys
	 * @param <V> type of values
	 * @return a new {@link HashMap} composed of keys and values from both {@link Iterator}s
	 */
	public static <K, V, M extends Map<K, V>> M pair(Iterable<K> keys, Iterable<V> values, Supplier<M> target) {
		UntilBothIterator<K, V> bothIterator = new UntilBothIterator<>(keys, values);
		return map(() -> bothIterator, Duo::getLeft, Duo::getRight, target);
	}
	
	/**
	 * Creates an {@link Iterable} by applying a mapping function on each element of the {@link Iterator} given by
	 * the source {@link Iterable}.
	 *
	 * @param iterable the {@link Iterable} that gives the source of the {@link Iterator} on which the mapping function will be applied
	 * @param mapper the mapping function of each element
	 * @return an {@link Iterable} which {@link Iterator} is one backed by given {@link Iterable} {@link Iterator} combined with mapping function
	 * @param <E> {@link Iterable} {@link Iterator} source element type
	 * @param <O> {@link Iterable} {@link Iterator} result element type
	 * @see #mappingIterator(Iterator, Function)
	 */
	public static <E, O> Iterable<O> mappingIterator(Iterable<E> iterable, Function<E, O> mapper) {
		return () -> mappingIterator(iterable.iterator(), mapper);
	}
	
	/**
	 * Creates an {@link Iterator} by applying a mapping function on each element of the given {@link Iterator}.
	 *
	 * @param iterator the {@link Iterator} on which the mapping function will be applied
	 * @param mapper the mapping function of each element
	 * @return an {@link Iterator} backed by given {@link Iterator} combined with mapping function
	 * @param <E> {@link Iterator} source element type
	 * @param <O> {@link Iterator} result element type
	 * @see #mappingIterator(Iterable, Function)
	 */
	public static <E, O> Iterator<O> mappingIterator(Iterator<E> iterator, Function<E, O> mapper) {
		return new Iterator<O>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}
			
			@Override
			public O next() {
				return mapper.apply(iterator.next());
			}
		};
	}
	
	/**
	 * Converts an {@link Iterator} to a {@link Stream}.
	 * If the {@link Iterator} comes from a {@link Collection}, then prefer usage of {@link Collection#stream()}
	 * 
	 * @param iterator an {@link Iterator}, not null
	 * @return a {@link Stream} than will iterate over the {@link Iterator} passed as argument
	 */
	public static <E> Stream<E> stream(Iterator<E> iterator) {
		return stream(() -> iterator);
	}
	
	/**
	 * Converts an {@link Iterable} to a {@link Stream}.
	 * If the {@link Iterable} is a {@link Collection}, then prefer usage of {@link Collection#stream()}
	 * 
	 * @param iterable an {@link Iterable}, not null
	 * @return a {@link Stream} than will iterate over the {@link Iterable} passed as argument
	 */
	public static <E> Stream<E> stream(Iterable<? extends E> iterable) {
		// StreamSupport knows how to convert an Iterable to a stream
		return (Stream<E>) StreamSupport.stream(iterable.spliterator(), false);
	}
	
	/**
	 * Concatenates given {@link Iterable}s as a single one to have a linear view of all of them.
	 *
	 * @param iterables some {@link Iterable}s
	 * @return a {@link Iterable} than will iterate over all given {@link Iterable}s
	 */
	public static <E> Iterator<E> concat(Iterable<E>... iterables) {
		return new IteratorIterator<>(iterables);
	}
	
	/**
	 * Concatenates given {@link Iterator}s as a single one to have a linear view of all of them.
	 *
	 * @param iterators some {@link Iterator}s
	 * @return a {@link Iterator} than will iterate over all given {@link Iterator}s
	 */
	public static <E> Iterator<E> concat(Iterator<E>... iterators) {
		return new IteratorIterator<>(iterators);
	}
	
	/**
	 * Wraps given array of elements into an {@link Iterator}.
	 *
	 * @param elements elements to be iterated
	 * @return an {@link Iterator} of given elements
	 * @param <E> the elements type
	 */
	@SafeVarargs // method body doesn't handle improperly varargs parameter so it would generate ClassCastException
	public static <E> Iterator<E> ofElements(E... elements ) {
		return new ArrayIterator<>(elements);
	}
	
	/**
	 * Consumes an {@link Iterable} with an action that gets index of each element of the {@link Iterable}
	 * 
	 * @param iterable any {@link Iterable}
	 * @param action an action that needs index and element as argument
	 * @param <E> {@link Iterable} elements type
	 */
	public static <E> void iterate(Iterable<E> iterable, BiConsumer<Integer, E> action) {
		int i = 0;
		for (E e : iterable) {
			action.accept(i++, e);
		}
	}
	
	/**
	 * Consumes an {@link Iterator} with an action that gets index of each element of the {@link Iterator}
	 * 
	 * @param iterator any {@link Iterator}
	 * @param action an action that needs index and element as argument
	 * @param <E> {@link Iterator} elements type
	 */
	public static <E> void iterate(Iterator<E> iterator, BiConsumer<Integer, E> action) {
		iterate(() -> iterator, action);
	}
	
	/**
	 * Keep elements of an {@link Iterable} that doesn't match a {@link Predicate}
	 * 
	 * @param iterable any {@link Iterable}
	 * @param includer any {@link Predicate}
	 * @param <E> type of elements
	 * @return given iterable without elements that doesn't match the given predicate
	 */
	public static <E> Iterable<E> filter(Iterable<E> iterable, Predicate<E> includer) {
		return () -> filter(iterable.iterator(), includer);
	}
	
	/**
	 * Keep elements of an {@link Iterator} that doesn't match a {@link Predicate}
	 *
	 * @param iterator any {@link Iterator}
	 * @param includer any {@link Predicate}
	 * @param <E> type of elements
	 * @return given iterator without elements that doesn't match the given predicate
	 */
	public static <E> Iterator<E> filter(Iterator<E> iterator, Predicate<E> includer) {
		return new Iterator<E>() {
			
			private boolean hasNext = true;
			private E currentItem = null;
			private final Iterator<E> delegate = iterator;
			
			private void lookAhead() {
				while (hasNext = delegate.hasNext()) {
					E item = delegate.next();
					if (!includer.test(item)) {
						hasNext = false;
						currentItem = null;
					} else {
						hasNext = true;
						currentItem = item;
					}
					if (hasNext) {
						break;
					}
				}
			}
			
			@Override
			public boolean hasNext() {
				lookAhead();
				return hasNext;
			}
			
			@Override
			public E next() {
				if (!hasNext) {
					// this is necessary to be compliant with Iterator#next(..) contract
					throw new NoSuchElementException();
				}
				return currentItem;
			}
			
			@Override
			public void remove() {
				delegate.remove();
			}
		};
	}
	
	/**
	 * Finds the first predicate-matching element into an {@link Iterable}
	 *
	 * @param iterable the {@link Iterable} to scan
	 * @param predicate the test to execute for equality
	 * @param <I> input type
	 * @return null if no element matches the predicate
	 */
	public static <I> I find(Iterable<I> iterable, Predicate<I> predicate) {
		return find(iterable.iterator(), predicate);
	}
	
	/**
	 * Finds the first predicate-matching element into an {@link Iterator}
	 *
	 * @param iterator the {@link Iterator} to scan
	 * @param predicate the test to execute for equality
	 * @param <I> input type
	 * @return null if no element matches the predicate
	 */
	@SuppressWarnings("squid:AssignmentInSubExpressionCheck" /* simple algorithm, doesn't require to extract the assignment from if */)
	public static <I> I find(Iterator<I> iterator, Predicate<I> predicate) {
		I result = null;
		boolean found = false;
		while (iterator.hasNext() && !found) {
			I step = iterator.next();
			if (found = predicate.test(step)) {
				result = step;
			}
		}
		return result;
	}
	
	/**
	 * Finds the first predicate-matching element (according to mapper) into the {@link Iterator} of an {@link Iterable}
	 *
	 * @param iterable the {@link Iterable} to scan
	 * @param mapper the mapper to extract the value to test
	 * @param predicate the test to execute for equality
	 * @param <I> input type
	 * @param <O> output type
	 * @return null if no mapped values matches the predicate
	 */
	public static <I, O> Duo<I, O> find(Iterable<I> iterable, Function<I, O> mapper, Predicate<O> predicate) {
		return find(iterable.iterator(), mapper, predicate);
	}
	
	/**
	 * Finds the first predicate-matching element (according to mapper) into an {@link Iterator}
	 * 
	 * @param iterator the {@link Iterator} to scan
	 * @param mapper the mapper to extract the value to test
	 * @param predicate the test to execute for equality
	 * @param <I> input type
	 * @param <O> output type
	 * @return null if no mapped values matches the predicate
	 */
	@SuppressWarnings("squid:AssignmentInSubExpressionCheck" /* simple algorithm, doesn't require to extract the assignment from if */)
	public static <I, O> Duo<I, O> find(Iterator<I> iterator, Function<I, O> mapper, Predicate<O> predicate) {
		Duo<I, O> result = null;
		boolean found = false;
		while (iterator.hasNext() && !found) {
			I step = iterator.next();
			O mapperResult = mapper.apply(step);
			// voluntary variable reassignment
			//noinspection ReassignedVariable
			if (found = predicate.test(mapperResult)) {
				result = new Duo<>(step, mapperResult);
			}
		}
		return result;
	}
	
	/**
	 * Consumes all predicate-matching elements of an {@link Iterable}
	 *
	 * @param iterable the {@link Iterable} to scan
	 * @param matcher the test to execute for equality
	 * @param foundConsumer will be called with every matching element and its index
	 * @param <E> input type
	 */
	public static <E> void consume(Iterable<E> iterable, Predicate<E> matcher, BiConsumer<E, Integer> foundConsumer) {
		consume(iterable.iterator(), matcher, foundConsumer);
	}
	
	/**
	 * Consumes all predicate-matching elements of an {@link Iterator}
	 *
	 * @param iterator the {@link Iterator} to scan
	 * @param matcher the test to execute for equality
	 * @param foundConsumer will be called with every matching element and its index
	 * @param <E> input type
	 */
	public static <E> void consume(Iterator<E> iterator, Predicate<E> matcher, BiConsumer<E, Integer> foundConsumer) {
		int index = 0;
		while (iterator.hasNext()) {
			E step = iterator.next();
			if (matcher.test(step)) {
				foundConsumer.accept(step, index);
			}
			index++;
		}
	}
	
	/**
	 * Equivalent of {@link #consume(Iterator, Predicate, BiConsumer)} with a {@link Stream} as input
	 * 
	 * @param stream the {@link Stream} to scan
	 * @param matcher the test to execute for equality
	 * @param foundConsumer will be called with every matching element and its index
	 * @param <E> input type
	 */
	public static <E> void consume(Stream<E> stream, Predicate<E> matcher, BiConsumer<E, Integer> foundConsumer) {
		final MutableInt index = new MutableInt(-1);
		stream.map(e -> new Duo<>(e, index.increment()))
				.filter(d -> matcher.test(d.getLeft()))
				.forEach(d -> foundConsumer.accept(d.getLeft(), d.getRight()));
	}
	
	/**
	 * Indicates if an {@link Iterator} contains a predicate-matching element
	 *
	 * @param iterator the {@link Iterator} to scan
	 * @param <I> input type
	 * @return true if a value that matches the {@link Predicate} is found
	 */
	public static <I> boolean contains(Iterator<I> iterator, Predicate<I> predicate) {
		return find(iterator, predicate) != null;
	}
	
	/**
	 * Indicates if an {@link Iterable} contains a predicate-matching element
	 *
	 * @param iterable the {@link Iterable} to scan
	 * @param <I> input type
	 * @return true if a value that matches the {@link Predicate} is found
	 */
	public static <I> boolean contains(Iterable<I> iterable, Predicate<I> predicate) {
		return find(iterable.iterator(), predicate) != null;
	}
	
	/**
	 * Indicates if an {@link Iterator} contains a predicate-matching element after applying a mapper to the elements
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
	
	/**
	 * Indicates if an {@link Iterable} contains a predicate-matching element after applying a mapper to the elements
	 *
	 * @param iterable the {@link Iterable} to scan
	 * @param mapper the mapper to extract the value to test
	 * @param <I> input type
	 * @param <O> output type
	 * @return true if a value that matches the {@link Predicate} is found
	 */
	public static <I, O> boolean contains(Iterable<I> iterable, Function<I, O> mapper, Predicate<O> predicate) {
		return find(iterable.iterator(), mapper, predicate) != null;
	}
	
	public static <E> Iterator<E> reverseIterator(List<E> list) {
		return new ReverseListIterator<>(list);
	}
	
	/**
	 * Creates an {@link Iterator} in reverse order of the given {@link AbstractCollection}. Be aware that it requires to create a copy of the
	 * collection as an array, hence the collection can't be modified by the resulting iterator so it is {@link ReadOnlyIterator}.
	 * 
	 * @param collection any (non null) {@link AbstractCollection}
	 * @param <E> collection elements type
	 * @return a reverse-order {@link Iterator} of the given collection
	 */
	public static <E> ReadOnlyIterator<E> reverseIterator(AbstractCollection<E> collection) {
		return new ReverseArrayIterator<>((E[]) collection.toArray());
	}
	
	private Iterables() {}
	
	/**
	 * Divides the iven {@link Iterable} into consecutive, equally-sized pieces (except possibly the last, which may be smaller)
	 *
	 * @param data an Iterable
	 * @param blockSize the size of blocks to be created (the last will contain remaining elements)
	 * @return a {@link List} of blocks of elements
	 */
	public static <E> List<List<E>> chunk(Iterable<E> data, int blockSize) {
		final List<List<E>> blocks = new ArrayList<>();
		// we ensure to have a list to allow sublist(..) usage afterward
		List<E> dataAsList = asList(data);
		int i = 0;
		int dataSize = dataAsList.size();
		int blockCount = dataSize / blockSize;
		// if some remain, an additional block must be created
		if (dataSize % blockSize != 0) {
			blockCount++;
		}
		// parcelling
		while (i < blockCount) {
			blocks.add(new ArrayList<>(dataAsList.subList(i * blockSize, Math.min(dataSize, ++i * blockSize))));
		}
		return blocks;
	}
}

package org.codefilarete.tool.collection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Splits a stream into some others according to given filters.
 *
 * @author Guillaume Mary
 */
public class StreamSplitter<T> {
	
	private final Stream<T> stream;
	
	private final Set<Splitter> splitters = new HashSet<>();
	
	public StreamSplitter(Stream<T> stream) {
		this.stream = stream;
	}
	
	/**
	 * Defines a rule to dispatch elements to a {@link Consumer}
	 *
	 * @param acceptanceFilter should returns true for sending element to given {@link Consumer}
	 * @param consumer function called with accepted element
	 * @return this
	 */
	public StreamSplitter<T> dispatch(Predicate<T> acceptanceFilter, Consumer<T> consumer) {
		this.splitters.add(new Splitter(acceptanceFilter, consumer));
		return this;
	}
	
	/**
	 * Triggers dispatching of elements into other {@link Stream} as defines through {@link #dispatch(Predicate, Consumer)}.
	 * This is a terminal operation.
	 */
	public void split() {
		stream.forEach(t -> splitters.forEach(splitter -> {
			if (splitter.splitter.test(t)) {
				splitter.consumer.accept(t);
			}
		}));
	}
	
	/**
	 * Internal storage of condition and consumer
	 */
	private class Splitter {
		
		private final Predicate<T> splitter;
		private final Consumer<T> consumer;
		
		private Splitter(Predicate<T> splitter, Consumer<T> consumer) {
			this.splitter = splitter;
			this.consumer = consumer;
		}
	}
}




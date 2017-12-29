package org.gama.lang.trace;

/**
 * A simple incrementable int. Not thread-safe. Prefer {@link java.util.concurrent.atomic.AtomicInteger} for thread safety.
 *
 * @author Guillaume Mary
 */
public class IncrementableInt {
	
	private int value;
	
	public IncrementableInt() {
		this(0);
	}
	
	public IncrementableInt(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public int increment() {
		return ++value;
	}
	
	public int increment(int increment) {
		return value += increment;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
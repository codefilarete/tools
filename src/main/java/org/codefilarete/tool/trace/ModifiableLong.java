package org.codefilarete.tool.trace;

/**
 * A simple modifiable long. Not thread-safe. Prefer {@link java.util.concurrent.atomic.AtomicLong} for thread safety.
 *
 * @author Guillaume Mary
 */
public class ModifiableLong {
	
	private long value;
	
	public ModifiableLong() {
		this(0);
	}
	
	public ModifiableLong(long value) {
		this.value = value;
	}
	
	public long getValue() {
		return value;
	}
	
	public long increment() {
		return ++value;
	}
	
	public long increment(long increment) {
		return value += increment;
	}
	
	public long decrement() {
		return --value;
	}
	
	public long decrement(long decrement) {
		return value -= decrement;
	}
	
	public void reset(long value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
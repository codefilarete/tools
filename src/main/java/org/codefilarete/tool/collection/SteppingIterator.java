package org.codefilarete.tool.collection;

import java.util.Iterator;

/**
 * {@link Iterator} that invokes a method every N iterations
 * {@link #hasNext()} triggers the invokation to {@link #onStep()} to mimic a while(hasNext) or a foreach : {@link #next()} doesn't do it because
 * it seems to be one iteration too late in this context of usage.
 * {@link #onStep()} os also invoked when {@link #hasNext()} returns false (except if it is the very first call) to treat the remaining objects.
 * 
 * @author Guillaume Mary
 */
public abstract class SteppingIterator<E> implements Iterator<E> {
	
	private final Iterator<? extends E> delegate;
	private long stepCount = 0;
	private final long stepSize;
	
	public SteppingIterator(Iterable<? extends E> delegate, long stepSize) {
		this(delegate.iterator(), stepSize);
	}
	
	public SteppingIterator(Iterator<? extends E> delegate, long stepSize) {
		this.delegate = delegate;
		this.stepSize = stepSize;
	}
	
	@Override
	public boolean hasNext() {
		boolean hasNext = delegate.hasNext();
		if (	// step reached
				stepCount == stepSize
				// or has remainings (end reached and not started)
				|| (!hasNext && stepCount != 0)) {
			onStep();
			stepCount = 0;
		}
		return hasNext;
	}
	
	@Override
	public E next() {
		stepCount++;
		return delegate.next();
	}
	
	public long getStepSize() {
		return stepSize;
	}
	
	public long getStepCount() {
		return stepCount;
	}
	
	protected abstract void onStep();
	
	@Override
	public void remove() {
		delegate.remove();
	}
}

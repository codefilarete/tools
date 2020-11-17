package org.gama.lang.collection;

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
	private long stepCounter = 0;
	private final long step;
	
	public SteppingIterator(Iterable<? extends E> delegate, long step) {
		this(delegate.iterator(), step);
	}
	
	public SteppingIterator(Iterator<? extends E> delegate, long step) {
		this.delegate = delegate;
		this.step = step;
	}
	
	@Override
	public boolean hasNext() {
		boolean hasNext = delegate.hasNext();
		if (	// step reached
				stepCounter == step
				// or has remainings (end reached and not started)
				|| (!hasNext && stepCounter != 0)) {
			onStep();
			stepCounter = 0;
		}
		return hasNext;
	}
	
	@Override
	public E next() {
		stepCounter++;
		return delegate.next();
	}
	
	protected abstract void onStep();
	
	@Override
	public void remove() {
		delegate.remove();
	}
}

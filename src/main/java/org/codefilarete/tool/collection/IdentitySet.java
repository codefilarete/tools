package org.codefilarete.tool.collection;

public class IdentitySet<E> {
	
	private static final Object PRESENCE_MARK = new Object();
	
	private final IdentityMap<E, Object> identityMap;
	
	/**
	 * Creates an {@link IdentitySet} with default sizing.
	 */
	public IdentitySet() {
		this.identityMap = new IdentityMap<>();
	}
	
	/**
	 * Creates an {@link IdentitySet} with the given sizing.
	 *
	 * @param size the size of the set to create
	 */
	public IdentitySet(int size) {
		this.identityMap = new IdentityMap<>(size);
	}
	
	public boolean contains(E o) {
		return identityMap.get(o) == PRESENCE_MARK;
	}
	
	public boolean add(E o) {
		return identityMap.put(o, PRESENCE_MARK) == null;
	}
	
	public boolean remove(E o) {
		return identityMap.remove(o) == PRESENCE_MARK;
	}
	
	public int size() {
		return this.identityMap.size();
	}
	
	public void clear() {
		identityMap.clear();
	}
}
package org.gama.lang;

import java.util.Objects;

/**
 * @author Guillaume Mary
 */
public class Duo<A, B> {
	
	private final A left;
	private final B right;
	
	public Duo(A left, B right) {
		this.left = left;
		this.right = right;
	}
	
	public A getLeft() {
		return left;
	}
	
	public B getRight() {
		return right;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Duo)) return false;
		Duo<?, ?> duo = (Duo<?, ?>) o;
		return Objects.equals(left, duo.left) &&
				Objects.equals(right, duo.right);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}
	
	/**
	 * Implemented for easier debug
	 * @return a left and right values print
	 */
	@Override
	public String toString() {
		return "{" + left + ", " + right + "}";
	}
}

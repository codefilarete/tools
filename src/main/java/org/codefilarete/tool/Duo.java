package org.codefilarete.tool;

import org.codefilarete.tool.bean.Objects;

/**
 * Stores a couple. Expected to be used for a very temporary structure of an algorithm or an internal process. For anyelse kind of usage please prefer
 * the creation of a dedicated class with better name and on-purpose methods. Since an anonymous couple class such as this one shouldn't exist beyond
 * a very local usage, a trio one shouldn't exist even more, as such a Trio class will never be created.
 * 
 * {@link #equals(Object)} and {@link #hashCode()} are bounded to content.
 * 
 * @author Guillaume Mary
 */
public class Duo<A, B> {
	
	private A left;
	private B right;
	
	public Duo() {
	}
	
	public Duo(A left, B right) {
		this.left = left;
		this.right = right;
	}
	
	public A getLeft() {
		return left;
	}
	
	public void setLeft(A left) {
		this.left = left;
	}
	
	public B getRight() {
		return right;
	}
	
	public void setRight(B right) {
		this.right = right;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Duo<?, ?> duo = (Duo<?, ?>) o;
		return Objects.equals(left, duo.left)
				&& Objects.equals(right, duo.right);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(left, right);
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

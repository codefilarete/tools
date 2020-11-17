package org.gama.lang.test;

import java.lang.reflect.Field;

import org.gama.lang.bean.InstanceFieldIterator;

/**
 * @author Guillaume Mary
 */
public class HashCodeBuilder {
	
	public static int hash(Object instance) {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
		InstanceFieldIterator instanceFieldIterator = new InstanceFieldIterator(instance.getClass());
		while (instanceFieldIterator.hasNext()) {
			Field next = instanceFieldIterator.next();
			next.setAccessible(true);
			try {
				hashCodeBuilder.append(next.get(instance));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return hashCodeBuilder.hash;
	}
	
	private int hash;
	
	public HashCodeBuilder append(Object o) {
		this.hash = 31 * this.hash + (o != null ? o.hashCode() : 0);
		return this;
	}
	
	public HashCodeBuilder append(long o) {
		this.hash = 31 * this.hash + (int) o;
		return this;
	}
	
	public HashCodeBuilder append(int o) {
		this.hash = 31 * this.hash + o;
		return this;
	}
	
	public HashCodeBuilder append(byte o) {
		this.hash = 31 * this.hash + o;
		return this;
	}
	
	public HashCodeBuilder append(char o) {
		this.hash = 31 * this.hash + o;
		return this;
	}
	
	public HashCodeBuilder append(double o) {
		this.hash = 31 * this.hash + (int) o;
		return this;
	}
	
	public HashCodeBuilder append(float o) {
		this.hash = 31 * this.hash + (int) o;
		return this;
	}
}

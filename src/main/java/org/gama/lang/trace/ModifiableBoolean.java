package org.gama.lang.trace;

/**
 * @author Guillaume Mary
 */
public class ModifiableBoolean {
	
	private Boolean value;
	
	public ModifiableBoolean() {
	}
	
	public ModifiableBoolean(Boolean value) {
		this.value = value;
	}
	
	public Boolean getValue() {
		return value;
	}
	
	public void setValue(Boolean value) {
		this.value = value;
	}
	
	public void setTrue() {
		this.value = true;
	}
	
	public void setFalse() {
		this.value = false;
	}
	
	public void setNull() {
		this.value = null;
	}
	
	public boolean isTrue() {
		return Boolean.TRUE.equals(value);
	}
	
	public boolean isFalse() {
		return Boolean.FALSE.equals(value);
	}
	
	public boolean isNull() {
		return value == null;
	}
}

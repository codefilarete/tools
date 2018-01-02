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
}

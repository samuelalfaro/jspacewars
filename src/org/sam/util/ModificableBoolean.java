package org.sam.util;

/**
 * @author samuel
 *
 */
public class ModificableBoolean {
	
	private boolean value;
	
	public ModificableBoolean(){
		this(false);
	}
	
	public ModificableBoolean(boolean value){
		this.value = value;
	}
	
	public boolean isTrue(){
		return value;
	}
	
	public boolean isFalse(){
		return !value;
	}
	
	public void setTrue(){
		this.value = true;
	}
	
	public void setFalse(){
		this.value = false;
	}
}

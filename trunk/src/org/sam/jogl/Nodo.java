/**
 * 
 */
package org.sam.jogl;

/**
 * @author samuel
 *
 */
public interface Nodo extends Dibujable, Cloneable{
	
	public void setParent(Nodo parent);
	
	public Nodo getParent();
	
	public Nodo[] getChilds();
	
	public Nodo clone();

}

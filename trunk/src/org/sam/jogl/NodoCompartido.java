package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * @author samuel
 *
 */
public class NodoCompartido implements Nodo {

	private Nodo child;
	
	public NodoCompartido(Nodo child) {
		this.child = child;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL)
	 */
	public void draw(GL gl) {
		child.draw(gl);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#getParent()
	 */
	public Nodo getParent() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#setParent(org.sam.jogl.Nodo)
	 */
	public void setParent(Nodo parent) {
	}
	
	private transient Nodo[] nodos; 
	public Nodo[] getChilds(){
		if(nodos == null){
			nodos = new Nodo[]{child};
		}
		return nodos;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public NodoCompartido clone(){
		return this;
	}
}

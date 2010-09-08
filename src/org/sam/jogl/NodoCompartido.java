package org.sam.jogl;

import javax.media.opengl.GL;

/**
 * Clase que representa un {@code Nodo} que tiene varios antecesores.
 */
public class NodoCompartido implements Nodo {

	private Nodo child;
	
	/**
	 * Constructor que crea {@code NodoCompartido} que contiene
	 * un {@code Nodo} descendiente, permitiendo a este último
	 * tener varios antecesores.
	 * @param child {@code Nodo} descendiente.
	 */
	public NodoCompartido(Nodo child) {
		this.child = child;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		child.draw(gl);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Este método siempre devuelve {@code null}, puesto que
	 * los nodos de esta clase son empleados para que tengan
	 * varios antecesores.</p>
	 * @return {@code null}.
	 */
	@Override
	public Nodo getParent() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>Este método no hace nada, puesto que los nodos de esta 
	 * clase son empleados para que tengan varios antecesores.</p>
	 */
	@Override
	public void setParent(Nodo parent) {
	}
	
	private transient Nodo[] nodos; 
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Nodo[] getChilds(){
		if(nodos == null){
			nodos = new Nodo[]{child};
		}
		return nodos;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodoCompartido clone(){
		return this;
	}
}

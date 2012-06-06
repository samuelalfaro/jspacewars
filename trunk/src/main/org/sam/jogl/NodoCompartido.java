/* 
 * NodoCompartido.java
 * 
 * Copyright (c) 2008-2010
 * Samuel Alfaro Jiménez <samuelalfaro at gmail.com>.
 * All rights reserved.
 * 
 * This file is part of jSpaceWars.
 * 
 * jSpaceWars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jSpaceWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jSpaceWars. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl;

import javax.media.opengl.GL2;

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
	public NodoCompartido( Nodo child ){
		this.child = child;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw( GL2 gl ){
		child.draw( gl );
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
	public void setParent( Nodo parent ){
	}
	
	private transient Nodo[] nodos; 
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Nodo#getChilds()
	 */
	@Override
	public Nodo[] getChilds(){
		if(nodos == null){
			nodos = new Nodo[]{child};
		}
		return nodos;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public NodoCompartido clone(){
		return this;
	}
}

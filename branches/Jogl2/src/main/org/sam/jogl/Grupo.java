/* 
 * Grupo.java
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

import java.util.*;

import javax.media.opengl.GL;

/**
 * Implementación de un {@code Nodo}, que premite tratar un grupo de
 * nodos descencientes, como un {@code Nodo} propiamente dicho.
 */
public class Grupo implements Nodo {
	private transient Nodo parent;
	private final Collection<Nodo> childs;

	/**
	 * Constructor por defecto que crea {@code Grupo} vacío.
	 */
	public Grupo() {
		childs = new LinkedList<Nodo>();
	}

	/**
	 * Constructor que crea un {@code Grupo}, a partir de 
	 * los datos de otro {@code Grupo} que se emplea como
	 * prototipo.
	 * 
	 * @param me {@code Grupo} empleado como prototipo.
	 */
	protected Grupo(Grupo me) {
		childs = new LinkedList<Nodo>();
		for(Nodo nodo: me.childs){
			Nodo clon = nodo.clone();
			clon.setParent(this);
			childs.add(clon);
		}
	}
	
	/**
	 * Método que añade un nuevo {@code Nodo} al conjunto de descendientes.
	 * @param e {@code Nodo} a añadir.
	 * @return <ul>
	 * 		<li>{@code true} si el nuevo {@code Nodo} se ha añadido correctamente.</li>
	 * 		<li>{@code false} en caso contrario.</li>
	 * </ul>
	 */
	public boolean add(Nodo e) {
		if(childs.add(e)){
			e.setParent(this);
			return true;
		}
		return false;
	}

	/**
	 * Método que elimina un {@code Nodo} del conjunto de descendientes.
	 * @param e {@code Nodo} a eliminar.
	 * @return <ul>
	 * 		<li>{@code true} si el {@code Nodo} se ha eliminado correctamente.</li>
	 * 		<li>{@code false} en caso contrario.</li>
	 * </ul>
	 */
	public boolean remove(Nodo e) {
		if(childs.remove(e)){
			e.setParent(null);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(GL gl) {
		for( Nodo  child: childs )
			 child.draw(gl);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Nodo getParent() {
		return parent;
	}
	
	private transient Nodo[] nodos;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Nodo[] getChilds(){
		if(nodos == null || nodos.length != childs.size()){
			nodos = new Nodo[childs.size()];
		}
		return childs.toArray(nodos);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grupo clone(){
		return new Grupo(this);
	}
}

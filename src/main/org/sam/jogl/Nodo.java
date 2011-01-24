/* 
 * Nodo.java
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

/**
 * Interface que indica que las clases que lo implementan son un <i>nodo</i> del grafo de escena,
 * proporcionando los métodos necesarios para poder recorrer dicho grafo.
 */
public interface Nodo extends Dibujable, Cloneable{
	
	/**
	 * <i>Setter</i> que asigna el {@code Nodo} antecesor de este {@code Nodo}.
	 * @param parent El {@code Nodo} antecesor.
	 */
	public void setParent(Nodo parent);
	
	/**
	 * <i>Getter</i> que devuelve el {@code Nodo} antecesor de este {@code Nodo}.
	 * @return El {@code Nodo} antecesor.
	 */
	public Nodo getParent();
	
	/**
	 * <i>Getter</i> que devuelve un vector con los descendientes de este {@code Nodo}.
	 * @return El vector de {@code Nodo} descendientes.
	 */
	public Nodo[] getChilds();
	
	/**
	 * Método que crea una copia de este {@code Nodo}.
	 * @return El {@code Nodo} creado.
	 */
	public Nodo clone();

}

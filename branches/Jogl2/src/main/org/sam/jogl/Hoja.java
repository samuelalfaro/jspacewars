/* 
 * Hoja.java
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
 * Clase que representa un {@code Nodo} final (hoja) del grafo de escena.
 */
public abstract class Hoja implements Nodo {
	private transient Nodo parent;

	/**
	 * Construtor por defecto que crea un {@code Objeto3DAbs}.
	 */
	protected Hoja() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setParent(Nodo parent) {
		this.parent = parent;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Nodo getParent() {
		return parent;
	}
	
	/**
	 * Vector de nodos vacío que será devuelto cuando 
	 * se invoque el método {@linkplain #getChilds()}.
	 */
	private static transient final Nodo[] nodos = new Nodo[]{}; 

	/**
	 * {@inheritDoc}
	 * <p>Este método siempre devuelve un vector de nodos vacío, puesto que
	 * los elementos de esta clase y sus derivadas, representan los nodos 
	 * finales (hojas), del grafo de escena.</p>
	 * @return Un vector de nodos vacío.
	 */
	@Override
	public final Nodo[] getChilds(){
		return nodos;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Hoja clone();
}

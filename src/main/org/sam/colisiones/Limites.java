/* 
 * Limites.java
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
package org.sam.colisiones;

/**
 * Interface que proporciona los métodos para tratar los límites de los objetos.
 */
public interface Limites {

	/**
	 * @return El valor mínimo de X para cualquier punto contenido dentro de los límites.
	 */
	public float getXMin();
	/**
	 * @return El valor máximo de X para cualquier punto contenido dentro de los límites.
	 */
	public float getXMax();
	/**
	 * @return El valor máximo de Y para cualquier punto contenido dentro de los límites.
	 */
	public float getYMin();
	/**
	 * @return El valor máximo de Y para cualquier punto contenido dentro de los límites.
	 */
	public float getYMax();
	
}

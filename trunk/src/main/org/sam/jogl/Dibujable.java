/* 
 * Dibujable.java
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
 * Interface indica que los objeto, de las clases que lo implementan,
 * pueden ser dibujados, y proporciona el método para realizar dicha acción.
 */
public interface Dibujable {
	
	/**
	 * Método encargado de dibujar el objeto correspondiente.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public void draw( GL2 gl );
}

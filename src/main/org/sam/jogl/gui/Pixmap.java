/* 
 * Pixmap.java
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
/**
 * 
 */
package org.sam.jogl.gui;

import javax.media.opengl.GL2;

/**
 * Clase que representa un área rectangular de una textura.
 */
public class Pixmap{
	
	/** Coordenada U de una de las esquinas del área rectangular. */
	public float u1;
	/** Coordenada V de una de las esquinas del área rectangular. */
	public float v1;
	/** Coordenada U de la esquina opuesta. */
	public float u2;
	/** Coordenada V de la esquina opuesta. */
	public float v2;

	/**
	 * Crea e inicializa un nuevo objeto Pixmap cuya área incluye toda la textura.
	 */
	public Pixmap(){
		this( 0, 0, 1, 1 );
	}

	/**
	 * Crea e inicializa un nuevo objeto Pixmap cuya área viene indicanda por el punto
	 * con las coordenadas menores y sus dimensiones.
	 * 
	 * @param u Coordenada U de una de las esquinas del área rectangular.
	 * @param v Coordenada V de una de las esquinas del área rectangular.
	 * @param w Anchura del área rectangular.
	 * @param h Altura del área rectangular.
	 */
	public Pixmap( float u, float v, float w, float h ){
		u1 = u;
		v1 = v;
		u2 = u + w;
		v2 = v + h;
	}

	/**
	 * Método que dibuja el área de textura en la zona rectagular
	 * delimitada por las coordenadas correspondientes.
	 * 
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 * @param x1 coordenada X de una de las esquinas.
	 * @param y1 coordenada Y de una de las esquinas.
	 * @param x2 coordenada X de la esquina opuesta.
	 * @param y2 coordenada Y de la esquina opuesta.
	 */
	public void draw( GL2 gl, float x1, float y1, float x2, float y2 ){
		//System.out.printf( "\t%3.1f %3.1f [ %2.2f %2.2f ] -> %3.1f %3.1f [ %2.2f %2.2f ]\n",  x1, y1, u1, v1, x2, y2, u2, v2 );
		gl.glBegin( GL2.GL_QUADS );
			gl.glTexCoord2f( u1, v1 );
			gl.glVertex2f  ( x1, y1 );
			gl.glTexCoord2f( u2, v1 );
			gl.glVertex2f  ( x2, y1 );
			gl.glTexCoord2f( u2, v2 );
			gl.glVertex2f  ( x2, y2 );
			gl.glTexCoord2f( u1, v2 );
			gl.glVertex2f  ( x1, y2 );
		gl.glEnd();
	}
}

/* 
 * Insets.java
 * 
 * Copyright (c) 2012 Samuel Alfaro Jiménez <samuelalfaro at gmail dot com>.
 * All rights reserved.
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jogl.gui;

/**
 * Una representación de los márgenes de un recuadro.
 * Se especifica el espacio que un recuadro debe dejar en cada uno de sus lados.
 */
public class Insets{
	
	/** Espacio de la parte izquierda de los márgenes.*/
	public final float left;
	/** Espacio de la parte superior de los márgenes.*/
	public final float top;
	/** Espacio de la parte derecha de los márgenes.*/
	public final float right;
	/** Espacio de la parte inferior de los márgenes.*/
	public final float bottom;
	
	/**
	 * Crea e inicializa un nuevo objeto Insets especificado el valor 0 para la parte izquierda, superior,
	 * derecha e inferior de los márgenes.
	 */
	public Insets(){
		this( 0.0f, 0.0f, 0.0f, 0.0f );
	}
	
	/**
	 * Crea e inicializa un nuevo objeto Insets especificado el mismo valor para la parte izquierda, superior,
	 * derecha e inferior de los márgenes.
	 * 
	 * @param insets espacio de la parte izquierda, superior, derecha e inferior.
	 */
	public Insets( float insets ){
		this( insets, insets, insets, insets );
	}
	
	/**
	 * Crea e inicializa un nuevo objeto Insets especificado de forma simetrica la parte izquierda y derecha,
	 * y superior e inferior de los márgenes.
	 * 
	 * @param horizontal espacio de la parte izquierda y derecha.
	 * @param vertical espacio de la parte superior e inferior.
	 */
	public Insets( float horizontal, float vertical ){
		this( horizontal, vertical, horizontal, vertical );
	}
	
	/**
	 * Crea e inicializa un nuevo objeto Insets especificado la parte izquierda, superior, derecha e inferior
	 * de los márgenes.
	 * 
	 * @param left espacio de la parte izquierda.
	 * @param top espacio de la parte superior.
	 * @param right espacio de la parte derecha.
	 * @param bottom espacio de la parte inferior.
	 */
	public Insets( float left, float top, float right, float bottom ){
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}
	
}

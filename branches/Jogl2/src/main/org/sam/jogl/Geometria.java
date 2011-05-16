/* 
 * Geometria.java
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
 * Interface que indica que las clases lo implementan,
 * contienen los datos que respresentan la geometría de un objeto.
 */
public interface Geometria extends Dibujable {
	
	/**
	 * Constante que indica que sólo se almacenará la referencia
	 * de los vectores, cuando se asignen los valores de los distintos
	 * componentes.<br/>
	 * En caso de no hacer uso de esta constante los vectores de
	 * copiarán completamente.
	 */
	public static final int POR_REFERENCIA      = 0x0010;
	/**
	 * Constante que indica que los valores de los distintos
	 * componentes serán almacenados en buffers.<br/>
	 * El uso de esta constante es <b>incompatible</b> con el uso
	 * de {@link #POR_REFERENCIA}.
	 */
	public static final int USAR_BUFFERS        = 0x0020;
	/**
	 * Constante que indica que la {@code Geometria} contendrá
	 * coordenadas de textura.
	 */
	//TODO preparar para coordenadas de textura de más de dos dimensiones.
	public static final int COORDENADAS_TEXTURA = 0x0040;
	/**
	 * Constante que indica que la {@code Geometria} contendrá
	 * colores RGB.
	 */
	public static final int	COLOR_3             = 0x0080;
	/**
	 * Constante que indica que la {@code Geometria} contendrá
	 * colores RGBA.
	 */
	public static final int	COLOR_4             = 0x0081;
	/**
	 * Constante que indica que la {@code Geometria} contendrá
	 * normales.
	 */
	public static final int NORMALES            = 0x0100;
	/**
	 * Constante que indica que la {@code Geometria} contendrá
	 * atributos de vertice.
	 */
	//TODO refactorizar para indicar las dimensiones de cada atributo.
	public static final int ATRIBUTOS_VERTICES  = 0x0200;
	
}

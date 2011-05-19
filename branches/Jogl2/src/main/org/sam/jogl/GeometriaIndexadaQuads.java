/* 
 * GeometriaIndexadaQuads.java
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
 * Implementación de una {@code Geometria} diseñada para almacenar cuadriláteros indexados.
 */
public class GeometriaIndexadaQuads extends GeometriaIndexada {

	/**
	 * Constructor que genera una {@code GeometriaIndexadaQuads} con los parámetros correspondientes.
	 * @param nVertex  {@link GeometriaAbs#nVertex Número de vértices} de la {@code Geometria} creada.
	 * @param att_mask {@link GeometriaAbs#att_mask Máscara de atributos} de la {@code Geometria} creada.
	 */
	public GeometriaIndexadaQuads(int nVertex, int att_mask) {
		super(nVertex, att_mask);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {
		// TODO Auto-generated method stub
	}
}

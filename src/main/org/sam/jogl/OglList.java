/* 
 * OglList.java
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
 * Clase que almacena en una lista memoria de video, la información de la
 * geometría que se genera mediante llamadas contenidas entre
 * {@code gl.glBegin(Primitive)} y {@code gl.glEnd()}.
 */
public class OglList implements Geometria {
	
	private final transient int idList;
	
	/**
	 * Constructor que crea un {@code OglList}, almacenando su indentificador
	 * y activando el contexto, para comenzar a almacenar la geometría 
	 * en memoria de video.<br/>
	 * <u>Nota:</u> Es inpresdible invocar {@link #endList(GL2)}, tras llamar a
	 * este constructor, para indicar cuando se ha finalizado de almacenar la
	 * lista.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public OglList(GL2 gl){
		this.idList = gl.glGenLists(1);
		gl.glNewList(this.idList, GL2.GL_COMPILE);
	}
	
	/**
	 * Método que indica se ha finalizado de almacenar la lista.
	 * @param gl Contexto gráfico en el que se realiza a acción.
	 */
	public static void endList(GL2 gl) {
		gl.glEndList();
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {
		gl.glCallList(idList);
	}
}

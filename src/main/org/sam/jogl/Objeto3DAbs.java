/* 
 * Objeto3DAbs.java
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
 * Clase base sobre la que crear, clases descendientes de
 * un {@code Nodo} con {@code Apariencia}.
 */
public abstract class Objeto3DAbs extends Hoja {

	private Apariencia apariencia;

	/**
	 * Construtor por defecto que crea un {@code Objeto3DAbs}.
	 */
	protected Objeto3DAbs() {
	}
	
	/**
	 * Construtor que crea un {@code Objeto3DAbs} y le
	 * asigna {@code Apariencia} indicada.
	 * @param apariencia  La {@code Apariencia} asignada.
	 */
	protected Objeto3DAbs(Apariencia apariencia) {
		this.apariencia = apariencia;
	}

	/**
	 * Construtor que crea un {@code Objeto3DAbs} copiando los
	 * datos de otro {@code Objeto3DAbs} que sirve como prototipo.
	 * @param me {@code Objeto3DAbs} prototipo.
	 */
	protected Objeto3DAbs(Objeto3DAbs me) {
		this.apariencia = me.apariencia;
	}
	
	/**
	 * <i>Getter</i> que devuelve la {@code Apariencia} de este {@code Objeto3DAbs}.
	 * @return La {@code Apariencia} solicitada.
	 */
	public final Apariencia getApariencia() {
		return apariencia;
	}

	/**
	 * <i>Setter</i> que asigna una {@code Apariencia} a este {@code Objeto3DAbs}.
	 * @param apariencia La {@code Apariencia} asignada.
	 */
	public final void setApariencia(Apariencia apariencia) {
		this.apariencia = apariencia;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jogl.Dibujable#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {
		if( apariencia != null )
			apariencia.usar(gl);
	}
}

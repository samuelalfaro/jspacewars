/* 
 * Objeto3D.java
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
 * Clase que representa un {@code Nodo} final {@code Dibujable},
 * compuesto por una {@code Geometria} y una {@code Apariencia}.
 */
public class Objeto3D extends Objeto3DAbs{
	private Geometria  geometria;

	/**
	 * Construtor por defecto que crea un {@code Objeto3D}.
	 */
	public Objeto3D() {
	}
	
	/**
	 * Construtor que crea un {@code Objeto3D} con la
	 * {@code forma3D} y la {@code Apariencia} indicadas.
	 * 
	 * @param geometria La {@code Geometria} asignada.
	 * @param apariencia  La {@code Apariencia} asignada.
	 */
	public Objeto3D(Geometria geometria, Apariencia apariencia) {
		super(apariencia);
		this.geometria = geometria;
	}
	
	/**
	 * Construtor que crea un {@code Objeto3D} copiando los
	 * datos de otro {@code Objeto3D} que sirve como prototipo.
	 * @param me {@code Objeto3D} prototipo.
	 */
	protected Objeto3D(Objeto3D me) {
		super(me);
		this.geometria = me.geometria;
	}
	
	/**
	 * <i>Getter</i> que devuelve la {@code Geometria} de este {@code Objeto3D}.
	 * @return La {@code Geometria} solicitada.
	 */
	public Geometria getGeometria() {
		return geometria;
	}
	
	/**
	 * <i>Setter</i> que asigna una {@code Geometria} a este {@code Objeto3D}.
	 * @param geometria La {@code Geometria} asignada.
	 */
	public void setGeometria(Geometria geometria) {
		this.geometria = geometria;
	}
	

	/* (non-Javadoc)
	 * @see org.sam.jogl.Objeto3DAbs#draw(javax.media.opengl.GL2)
	 */
	@Override
	public void draw(GL2 gl) {
		super.draw(gl);
		if(geometria != null)
			geometria.draw(gl);
	}

	/* (non-Javadoc)
	 * @see org.sam.jogl.Hoja#clone()
	 */
	@Override
	public Objeto3D clone(){
		return new Objeto3D(this);
	}
}

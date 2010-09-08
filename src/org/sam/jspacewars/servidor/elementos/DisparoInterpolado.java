/* 
 * DisparoInterpolado.java
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
package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;
import org.sam.interpoladores.Trayectoria;

/**
 * Implementación de un {@link Disparo} que sigue una trayectoria predefinida.
 */
public class DisparoInterpolado extends Disparo {

	private transient Trayectoria.Float<float[]> trayectoria;
	private transient float despX, despY;
	private transient float scaleX, scaleY;
	private transient long time;
	
	DisparoInterpolado(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code DisparoInterpolado} copiando los
	 * datos de otro {@code DisparoInterpolado} que sirve como prototipo.
	 * @param prototipo {@code DisparoInterpolado} prototipo.
	 */
	protected DisparoInterpolado(DisparoInterpolado prototipo) {
		super(prototipo);
	}

	public DisparoInterpolado clone() {
		return new DisparoInterpolado(this);
	}

	public void setValues(float posX, float posY, Trayectoria.Float<float[]> trayectoria, float scaleX, float scaleY) {
		super.setPosicion(posX, posY);
		despX = posX;
		despY = posY;
		this.trayectoria = trayectoria;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.time = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Disparo#actua(long)
	 */
	public void actua(long nanos) {
		time += nanos;
		float posTan[][] = trayectoria.getPosTan((time));
		setAngulo((float)Math.atan2(posTan[1][1] * scaleY, posTan[1][0] * scaleX));
		setPosicion( posTan[0][0] * scaleX + despX, posTan[0][1] * scaleY + despY );
	}
}

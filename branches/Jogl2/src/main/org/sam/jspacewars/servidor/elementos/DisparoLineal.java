/* 
 * DisparoLineal.java
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

/**
 * Implementación de un {@link Disparo} que sigue una trayectoria recta.
 */
public class DisparoLineal extends Disparo {

	private transient float incX, incY;

	DisparoLineal(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code DisparoLineal} copiando los
	 * datos de otro {@code DisparoLineal} que sirve como prototipo.
	 * @param prototipo {@code DisparoLineal} prototipo.
	 */
	protected DisparoLineal(DisparoLineal prototipo) {
		super(prototipo);
	}

	public DisparoLineal clone() {
		return new DisparoLineal(this);
	}

	/**
	 * @param posX
	 * @param posY
	 * @param angulo 
	 * @param velocidad
	 */
	public void setValues(float posX, float posY, float angulo, float velocidad) {
		super.setPosicion(posX, posY);
		super.setAngulo(angulo);
		this.incX = (float) Math.cos(angulo) * velocidad;
		this.incY = (float) Math.sin(angulo) * velocidad;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Disparo#actua(long)
	 */
	public void actua(long nanos) {
		setPosicion( getX() + incX * nanos, getY() + incY * nanos );
	}
}

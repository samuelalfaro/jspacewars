/* 
 * CanionLineal.java
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

import java.util.Collection;

/**
 * Implementación de un {@link Canion} que lanza {@link DisparoLineal disparos} con una trayectoria recta.
 */
public class CanionLineal extends Canion {
	/**
	 * Ángulo, en radianes, del cañón.
	 */
	private float angulo;

	/**
	 * Constructor que crea un {@code CanionLineal} y asigna los valores correspondientes.
	 * @param data {@link #data Datos del cañón} asignados.
	 */
	public CanionLineal(CanionData data) {
		super(data);
	}

	/**
	 * Construtor que crea un {@code CanionLineal} copiando los
	 * datos de otro {@code CanionLineal} que sirve como prototipo.
	 * @param prototipo {@code CanionLineal} prototipo.
	 */
	private CanionLineal(CanionLineal prototipo) {
		super(prototipo);
		this.angulo = prototipo.angulo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canion clone() {
		return new CanionLineal(this);
	}

	/**
	 * <i>Setter</i> que asigna el {@link #angulo ángulo}, en grados, de este {@code CanionLineal}.
	 * @param angulo Ángulo asignado.
	 */
	public void setAngulo(float angulo) {
		this.angulo = (float) (angulo * Math.PI / 180.0 );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispara(float mX, float nX, float mY, float nY, long nanos, long stopTime, Collection<? super Disparo> dst){

		long t = tRecarga - tTranscurrido;

		tTranscurrido += nanos;
		while( tTranscurrido >= tRecarga ){
			DisparoLineal disparo = (DisparoLineal) cache.newObject(idDisparo);

			disparo.setValues(t * mX + nX + posX, t * mY + nY + posY, angulo, velocidad);
			disparo.actua(nanos - t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}

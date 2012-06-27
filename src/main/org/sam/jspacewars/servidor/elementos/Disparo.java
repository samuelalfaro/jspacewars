/* 
 * Disparo.java
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

import java.nio.ByteBuffer;

import org.sam.colisiones.Poligono;

public abstract class Disparo extends Elemento {

	private transient float angulo;

	protected Disparo(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code Disparo} copiando los
	 * datos de otro {@code Disparo} que sirve como prototipo.
	 * @param prototipo {@code Disparo} prototipo.
	 */
	protected Disparo(Disparo prototipo) {
		super(prototipo);
	}

	/**
	 * @return el angulo solicitado.
	 */
	public float getAngulo() {
		return angulo;
	}

	/**
	 * @param angulo valor del angulo asignado.
	 */
	public void setAngulo(float angulo) {
		this.getForma().rotar(angulo);
		this.getForma().actualizarLimiteRectangular();
		this.angulo = angulo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
	
	boolean destruido = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(){
		destruido = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recibirImpacto(int fuerzaDeImpacto) {
		destruido = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDestruido() {
		return destruido;
	}
}

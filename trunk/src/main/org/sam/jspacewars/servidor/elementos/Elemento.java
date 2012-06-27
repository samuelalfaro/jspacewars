/* 
 * Elemento.java
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
import java.util.Comparator;

import org.sam.colisiones.Colisionable;
import org.sam.colisiones.Limites;
import org.sam.colisiones.Poligono;
import org.sam.elementos.Dinamico;
import org.sam.elementos.Enviable;
import org.sam.elementos.PrototipoCacheable;

public abstract class Elemento implements PrototipoCacheable<Elemento>, Enviable, Dinamico, Colisionable, Destruible {

	/**
	 * {@code Comparator} que usa sus identificadores para que comparar dos elementos.
	 */
	public static Comparator<Elemento> COMPARADOR = new Comparator<Elemento>() {
		public int compare(Elemento e1, Elemento e2) {
			int comparacion = e1.type - e2.type;
			return comparacion != 0 ? comparacion: e1.id - e2.id;
		}
	};
	
	private static int compareFloats(float f1, float f2){
		return
			f1 < f2 ? -1:
			f1 > f2 ?  1:
			0;
	}
	
	/**
	 * {@code Comparator} que usa sus posiciones para que comparar dos elementos.
	 */
	public static Comparator<Elemento> COMPARADOR_POSICIONES = new Comparator<Elemento>() {

		public int compare(Elemento e1, Elemento e2) {
			int comparacion = compareFloats(e1.getLimites().getXMin(), e2.getLimites().getXMin());
			return comparacion != 0 ? comparacion: e1.id - e2.id;
		}
	};

	private interface AutoIncrementable{
		short getNextId();
	}
	private transient AutoIncrementable incrementador;
	
	private final AutoIncrementable getAutoIncrementable(){
		if(incrementador == null)
			incrementador = new AutoIncrementable(){
				private transient short id = 0;
	
				public short getNextId() {
					if( ++id == Short.MAX_VALUE )
						id = 1;
					return id;
				}
			};
		return incrementador;
	}
	
	private final Elemento prototipo;
	private final short type;
	private final Poligono forma;
	
	private short id;
	private transient float posX, posY;

	Elemento(short type, Poligono forma) {
		this.prototipo = null;
		this.type = type;
		this.forma = forma;
		this.id = 0;
		this.posX = 0.0f;
		this.posY = 0.0f;
	}

	/**
	 * Construtor que crea un {@code Elemento} copiando los
	 * datos de otro {@code Elemento} que sirve como prototipo.
	 * @param prototipo {@code Elemento} prototipo.
	 */
	protected Elemento(Elemento prototipo) {
		this.prototipo = prototipo;
		this.type = prototipo.type;
		this.forma = prototipo.forma.clone();
		this.incrementador = null;
		this.id = prototipo.getAutoIncrementable().getNextId();
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
	}

	public final int hashCode() {
		return type;
	}

	public void setPosicion(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
		this.forma.trasladar(posX, posY);
	}

	/**
	 * @return el {@code Poligono} solicitado.
	 */
	protected final Poligono getForma() {
		return forma;
	}
	
	public final float getX() {
		return this.posX;
	}

	public final float getY() {
		return this.posY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Limites getLimites(){
		return forma == null ? null : forma.getLimites();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hayColision(Colisionable otro){
		// TODO mirar
		return forma.hayColision(((Elemento)otro).forma);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void colisionar(Colisionable otro) {
		if( otro instanceof Destruible )
			((Destruible)otro).recibirImpacto(1);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enviar(ByteBuffer buff) {
		buff.putShort(type);
		buff.putShort(id);
		buff.putFloat(posX);
		buff.putFloat(posY);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Elemento clone();
	
	@Override
	public final void reset() {
		this.id = prototipo.getAutoIncrementable().getNextId();
		init();
	}
}

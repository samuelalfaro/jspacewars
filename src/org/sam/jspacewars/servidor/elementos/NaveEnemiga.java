/* 
 * NaveEnemiga.java
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
/**
 * 
 */
package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;
import org.sam.jspacewars.servidor.tareas.Tarea;

public class NaveEnemiga extends Nave {

	private Tarea tarea;
	
	/**
	 * @param tipo
	 */
	public NaveEnemiga(short code, Poligono forma) {
		super(code, forma);
		dstDisparos = null;
	}

	/**
	 * Construtor que crea una {@code NaveEnemiga} copiando los
	 * datos de otra {@code NaveEnemiga} que sirve como prototipo.
	 * @param prototipo {@code NaveEnemiga} prototipo.
	 */
	protected NaveEnemiga(NaveEnemiga prototipo) {
		super(prototipo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NaveEnemiga clone() {
		return new NaveEnemiga(this);
	}

	private transient long time;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void iniciar() {
		super.inicializar();
		time = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Dinamico#actua(long)
	 */
	public void actua(long nanos) {
		long startTime = time;
		time += nanos;
		if(tarea != null)
			tarea.realizar(this, startTime, time);
	}
}

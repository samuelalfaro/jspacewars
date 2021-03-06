/* 
 * Esperar.java
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
package org.sam.jspacewars.servidor.tareas;

import org.sam.jspacewars.servidor.elementos.NaveEnemiga;

/**
 * Clase que define un periodo de espera que permite retrasar otras tareas posteriores.
 */
public final class Esperar extends TareaAbs {

	private final long duracionMin, duracionMax;
	
	/**
	 * Constructor que asigna un tiempo espera fijo.
	 *  
	 * @param duracion Tiempo que dura la espera.
	 */
	public Esperar( long duracion ){
		this( duracion, duracion );
	}

	/**
	 * Constructor que asigna un tiempo espera aleatorio comprendido entre los valores
	 * que se pasan como parámetros.
	 * 
	 * @param duracionMin Tiempo de espera mínimo.
	 * @param duracionMax Tiempo de espera máximo.
	 */
	public Esperar( long duracionMin, long duracionMax ){
		super( (long)( Math.random() * ( duracionMax - duracionMin ) + 0.5 ) + duracionMin );
		this.duracionMin = duracionMin;
		this.duracionMax = duracionMax;
	}

	/**
	 * Devuelve la duración mínima de esta tarea.
	 * 
	 * @return el valor solicitado.
	 */
	public long getDuracionMin() {
		return duracionMin;
	}

	/**
	 * Devuelve la duración máxima de esta tarea.
	 * 
	 * @return el valor solicitado.
	 */
	public long getDuracionMax() {
		return duracionMax;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jspacewars.servidor.tareas.Tarea#realizar(org.sam.jspacewars.servidor.elementos.NaveEnemiga, long, long)
	 */
	@Override
	public void realizar( NaveEnemiga owner, long startTime, long stopTime ){
		// TODO hacer realmente
		realizarTest( startTime, stopTime );
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Esperando...";
	}

}

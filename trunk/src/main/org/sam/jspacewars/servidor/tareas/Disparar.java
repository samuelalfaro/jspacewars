/* 
 * Disparar.java
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
 * Clase que define la {@code Tarea} de disparar un cañon, durante un periodo de tiempo.
 */
public class Disparar extends TareaAbs {

	private int canion;
	
	/**
	 * Constructor que asigna los valores necesarios para realizar la {@code Tarea} de disparar un cañon.
	 *  
	 * @param canion Índice que indica el cañon disparado.
	 * @param duracion Tiempo que dura la tarea de disparar.
	 */
	public Disparar( int canion, long duracion ){
		super( duracion );
		this.canion = canion;
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
	public String toString(){
		return String.format("Disparando cañon %d ...",canion);
	}
	
}

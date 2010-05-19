/* 
 * TareaAbs.java
 * 
 * Copyright (c) 2010 Samuel Alfaro <samuelalfaro at gmail.com>.
 * All rights reserved
 * 
 * This file is part of jspacewars.
 * 
 * jspacewars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * jspacewars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with jspacewars.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sam.jspacewars.tareas;

public abstract class TareaAbs implements Tarea {
	
	final private long duracion;
	
	public TareaAbs(long duracion){
		if(duracion < 0)
			throw new IllegalArgumentException("Duracion < 0");
		this.duracion = duracion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public long getDuracion() {
		return duracion;
	}
	
	/**
	 * Metodo que muestra por consola las llamadas para test.
	 * 
	 * @see org.sam.jspacewars.tareas.Tarea#realizar(long, long)
	 */
	final protected void realizarTest(long startTime, long stopTime){
		long desfase =  startTime;
		long tTrabajo = Math.min(this.getDuracion() - startTime, stopTime - startTime);
		long tEspera =  stopTime -tTrabajo - desfase;
		System.out.format("   %-22s [ %2d, %2d ] [ %2d : %2d nanosegundos : %2d ]\n", toString(), startTime, stopTime, desfase, tTrabajo, tEspera );
	}
}
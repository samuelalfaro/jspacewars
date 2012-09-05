/* 
 * TareaAbs.java
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

/**
 * Implementación Abstracta del interface {@code Tarea}.
 */
public abstract class TareaAbs implements Tarea {
	
	final private long duracion;
	
	/**
	 * Constructor que asigna la duración de la tarea.
	 * @param duracion Tiempo que dura la {@code Tarea}.
	 */
	public TareaAbs( long duracion ){
		if( duracion < 0 )
			throw new IllegalArgumentException( "Duracion < 0" );
		this.duracion = duracion;
	}

	/* (non-Javadoc)
	 * @see org.sam.jspacewars.servidor.tareas.Tarea#getDuracion()
	 */
	@Override
	final public long getDuracion() {
		return duracion;
	}
	
	/**
	 * Método que simula la realización de la {@code Tarea} durante el periodo comprendido
	 * entre {@code startTime} y {@code stopTime}, ambos relativos a la duracion de dicha {@code Tarea}.
	 * Mostrando por consola las llamadas para test.
	 * 
	 * @param startTime Tiempo inicial.<br/>
	 * @param stopTime Tiempo final.<br/>
	 * 
	 * @see org.sam.jspacewars.servidor.tareas.Tarea#realizar(org.sam.jspacewars.servidor.elementos.NaveEnemiga, long, long)
	 */
	final protected void realizarTest( long startTime, long stopTime ){
		long desfase = startTime;
		long tTrabajo = Math.min( this.getDuracion() - startTime, stopTime - startTime );
		long tEspera = stopTime - tTrabajo - desfase;
		System.out.format("   %-22s [ %2d, %2d ] [ %2d : %2d nanosegundos : %2d ]\n", toString(), startTime, stopTime, desfase, tTrabajo, tEspera );
	}
}

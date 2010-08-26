/* 
 * Tarea.java
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
package org.sam.jspacewars.servidor.tareas;

/**
 * @author Samuel Alfaro
 *
 */
public interface Tarea {
	
	/**
	 * Devuelve la duracion de la {@code Tarea}.
	 * @return duracion de la {@code Tarea}.
	 */
	public long getDuracion();
	
	/**
	 * Realiza la {@code Tarea} durante el periodo comprendido entre {@code startTime} y {@code stopTime},
	 * ambos relativos a la duracion de dicha {@code Tarea}.
	 * 
	 * @param startTime Tiempo inicial.<br/>
	 * Si es negativo, o mayor de {@code stopTime}, el comportamiento no esta definido.<br/>
	 * Si es mayor de la duracion de la {@code Tarea}, se interpreta que dicha {@code Tarea},
	 * se ha realizado en un periodo anterior, y no se hace nada.
	 * 
	 * @param stopTime Tiempo final.<br/>
	 * Si negativo, o menor de {@code startTime}, el comportamiento no esta definido.<br/>
	 * Si es mayor que la duracion que la {@code Tarea}, se completara dicha {@code Tarea},
	 * teniendo en cuenta el tiempo transcurrido tras su finalizacion.
	 */
	public void realizar(long startTime, long stopTime);
	
}
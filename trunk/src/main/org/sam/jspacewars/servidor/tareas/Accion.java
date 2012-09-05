/* 
 * Accion.java
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
 * Clase que define una {@code Tarea} instantánea.
 */
public abstract class Accion implements Tarea {

	/**
	 * Método que devuelve la duración de la {@code Tarea}, como se considera {@code Accion} a una {@code Tarea} instantánea,
	 * devuelve simpre 0.
	 * @return 0
	 */
	@Override
	final public long getDuracion() {
		return 0;
	}
	
	/**
	 * Método que realiza la {@code Tarea} durante el periodo comprendido entre {@code startTime} y {@code stopTime},
	 * como se considera {@code Accion} a una {@code Tarea} instantánea, este método, hace una llamada
	 * al método abstracto
	 * {@link org.sam.jspacewars.servidor.tareas.Accion#realizar(org.sam.jspacewars.servidor.elementos.NaveEnemiga)
	 * realizar(NaveEnemiga owner)},
	 * ignorando los parámetros {@code startTime} y {@code stopTime}.
	 * 
	 * @param owner {@code NaveEnemiga} que realiza la {@code Tarea}.
	 * @param startTime ignorado
	 * @param stopTime ignorado
	 */
	@Override
	final public void realizar( NaveEnemiga owner, long startTime, long stopTime ){
		realizar( owner );
	}
	
	/**
	 * Método que realiza la {@code Accion}.
	 * 
	 * @param owner {@code NaveEnemiga} que realiza la {@code Accion}.
	 */
	public abstract void realizar( NaveEnemiga owner );
	
	/**
	 *  Método que simula la realización de la {@code Accion}, mostrando por consola las llamadas para test.
	 * 
	 * @see org.sam.jspacewars.servidor.tareas.Accion#realizar(org.sam.jspacewars.servidor.elementos.NaveEnemiga)
	 */
	final protected void realizarTest(){
		System.out.println( " * " + this.toString() );
	}
	
}

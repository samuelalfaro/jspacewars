/* 
 * Accion.java
 * 
 * Copyright (c) 2010 Samuel Alfaro <samuelalfaro at gmail dot com>.
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

import org.sam.jspacewars.servidor.elementos.NaveEnemiga;

/**
 * @author samuel
 */
public abstract class Accion implements Tarea {

	/**
	 * {@inheritDoc}<br/>
	 * Como una {@code Accion} se considera una {@code Tarea} instantenea, devuelve simpre 0.
	 * @return 0
	 */
	@Override
	final public long getDuracion() {
		return 0;
	}
	
	/**
	 * {@inheritDoc}<br/>
	 * Como una {@code Accion} se considera una {@code Tarea} instantenea, este metodo, encapsula una llamada
	 * al metodo abstracto {@link org.sam.jspacewars.servidor.tareas.Accion#realizar() realizar()},
	 * ignorando los parametros {@code startTime} y {@code stopTime}.
	 * 
	 * @param startTime ignorado
	 * @param stopTime ignorado
	 */
	@Override
	final public void realizar(NaveEnemiga owner, long startTime, long stopTime){
		realizar(owner);
	}
	
	/**
	 * Realiza la  {@code Accion}.
	 * 
	 * @param owner {@code NaveEnemiga} que realiza la {@code Accion}.
	 */
	public abstract void realizar(NaveEnemiga owner);
	
	/**
	 * Metodo que muestra por consola las llamadas para test.
	 * 
	 * @see org.sam.jspacewars.servidor.tareas.Accion#realizar()
	 */
	final protected void realizarTest() {
		System.out.println(" * "+this.toString());
	}
	
}
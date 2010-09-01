/* 
 * SetAnguloCanion.java
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

import org.sam.jspacewars.servidor.elementos.NaveEnemiga;

/**
 * @author samuel
 *
 */
public class SetAnguloCanion extends Accion {

	int canion;
	float angulo;
	
	public SetAnguloCanion(int canion, float angulo) {
		this.canion = canion;
		this.angulo = angulo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void realizar(NaveEnemiga owner) {
		// TODO hacer realmente
		realizarTest();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return String.format("Asignando angulo %4.1f al cañon %d", angulo, canion );
	}
	
}
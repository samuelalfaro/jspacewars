/* 
 * Esperar.java
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
 * @author samuel
 * 
 */
public final class Esperar extends TareaAbs {

	public Esperar(long duracion) {
		super(duracion);
	}

	public Esperar(long duracionMin, long duracionMax) {
		super( (long)(Math.random() * (duracionMax - duracionMin) + 0.5) + duracionMin );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void realizar(long startTime, long stopTime) {
		realizarTest(startTime, stopTime);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Esperando...";
	}

}
/* 
 * TareasParalelas.java
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

public final class TareasParalelas extends TareaAbs {
	
	private final Tarea[] tareas;

	private static long calcularDuracion(Tarea[] tareas){
		long max = -1;
		for(Tarea t:tareas)
			if(max < t.getDuracion())
				max = t.getDuracion();
		return max;
	}
	
	public TareasParalelas(Tarea[] tareas){
		super( calcularDuracion(tareas) );
		this.tareas = tareas;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void realizar(long startTime, long stopTime){
		if( startTime >= this.getDuracion() )
			return;
		for(Tarea t:tareas)
			if(startTime < t.getDuracion() )
				t.realizar( startTime, stopTime );
	}
}

/* 
 * Secuencia.java
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

public final class Secuencia implements Tarea {
	
	private final Tarea[] tareas;
	private final long[] finales;
	private final long duracion;

	Secuencia(Tarea[] tareas){
		this.tareas = tareas;
		this.finales = new long[tareas.length];
		
		finales[0] = tareas[0].getDuracion();
		for(int i= 1; i < tareas.length; i++)
			finales[i] = finales[i-1] + tareas[i].getDuracion();
		this.duracion = finales[finales.length-1];
		
//		System.out.print("Finales: \t[");
//		for(long f:finales)
//			System.out.print(" "+f);
//		System.out.println(" ]");
	}
	
	@Override
	public void realizar(long nanos, long desfase){
		if(desfase < 0 || desfase >= duracion )
			return;
		// Se realiza una búsqueda secuencial pues puede haber valores repetidos 
		// y no es acosejable una búsqueda dicotómica.
		int tarea = 0;
		if(desfase > 0){
			while(finales[tarea] <= desfase)
				tarea ++;
			if( tarea > 0)
				desfase -= finales[tarea-1];
		}
		
		while(tarea < tareas.length){
			if(desfase + nanos < tareas[tarea].getDuracion()){
				if(nanos > 0 )
					tareas[tarea].realizar(nanos, desfase);
				break;
			}else{
				long tRestante = tareas[tarea].getDuracion() - desfase;
				tareas[tarea].realizar(tRestante, desfase);
				nanos -= tRestante;
				desfase = 0;
				tarea++;
			}
		}
	}

	@Override
	public long getDuracion() {
		return duracion;
	}
}

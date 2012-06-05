/* 
 * PruebaTareas.java
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

import java.util.Random;

import org.sam.jspacewars.serialization.TareasConverters;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Clase de prueba.
 */
public class PruebaTareas {
	
	/**
	 * Función principal
	 * @param args ignorados.
	 */
	public static void main( String[] args ){
	
		/*
		Tarea tarea = new TareasParalelas( new Tarea[]{
			new Mover(30),
			new Secuencia( new Tarea[]{
				new Esperar(10),
				new Esperar(3,5),
				new TareasParalelas( new Tarea[]{
					new Secuencia( new Tarea[]{
						new SetAnguloCanion(1,0),
						new OrientarCanion(1, 1, 5),
					}),
					new Disparar(1,5)
				})
			}),
			new Secuencia( new Tarea[]{
				new Esperar(15),
				new Esperar(3,5),
				new TareasParalelas( new Tarea[]{
					new Secuencia( new Tarea[]{
						new SetAnguloCanion(2,0),
						new OrientarCanion(2,-1,5),
					}),
					new Disparar(2,5)
				})	
			}),
		});
		/*/
		String xmlIN = 
			"<TareasParalelas>"+
			"  <Mover duracion=\"30\"/>"+
			"  <Secuencia>"+
			"    <Esperar duracion=\"10\"/>"+
			"    <Esperar duracionMin=\"3\" duracionMax=\"5\"/>"+
			"    <TareasParalelas>"+
			"      <Secuencia>"+
			"        <SetAnguloCanion/>"+
			"        <OrientarCanion duracion=\"5\"/>"+
			"      </Secuencia>"+
			"      <Disparar duracion=\"5\"/>"+
			"    </TareasParalelas>"+
			"  </Secuencia>"+
			"  <Secuencia>"+
			"    <Esperar duracion=\"15\"/>"+
			"    <Esperar duracionMin=\"3\" duracionMax=\"5\"/>"+
			"    <TareasParalelas>"+
			"      <Secuencia>"+
			"        <SetAnguloCanion/>"+
			"        <OrientarCanion duracion=\"5\"/>"+
			"      </Secuencia>"+
			"      <Disparar duracion=\"5\"/>"+
			"    </TareasParalelas>"+
			"  </Secuencia>"+
			"</TareasParalelas>";
		
		XStream xStream = new XStream(new DomDriver());
		TareasConverters.register( xStream );
		
		Tarea tarea = (Tarea)xStream.fromXML( xmlIN );

		System.out.println( xStream.toXML( tarea ) );
		//*/
		
		Random r = new Random();
		long i = 0;
		while( i <= 30 ){
			System.out.println( "\nInstante: " + i );
			long f = i + r.nextInt( 4 ) + 1;
			tarea.realizar( null, i, f );
			i = f;
		}
	}
}

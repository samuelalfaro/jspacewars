/* 
 * CanionData.java
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
package org.sam.jspacewars.servidor.elementos;

/**
 * Clase que almacena los:<ul>
 * <li>{@link Canion#idDisparo Identificadores de disparo}.</li>
 * <li>{@link Canion#tRecarga Tiempos de recarga}.</li>
 * <li>{@link Canion#velocidad Velocidades de disparo}.</li>
 * </ul>
 * Para distintos grados de un determinado {@code Canion}.
 */
public class CanionData {
	
	/**
	 * Vector con los {@link Canion#idDisparo identificadores de disparo}
	 * de un {@code Canion}, para los distintos grados.
	 */
	private final int[] vIdDisparos;

	/**
	 * Vector con los {@link Canion#tRecarga tiempos de recarga}
	 * de un {@code Canion}, para los distintos grados.
	 */
	private final long[] vTRecarga;

	/**
	 * Vector con las {@link Canion#velocidad velocidades de disparo}
	 * de un {@code Canion}, para los distintos grados.
	 */
	private final float[] vVelocidades;

	/**
	 * Constructor que crea un {@code CanionData} y asigna los valores correspondientes.
	 * @param vIdDisparos {@link #vIdDisparos} asignado.
	 * @param vTRecarga {@link #vTRecarga} asignado.
	 * @param vVelocidades {@link #vVelocidades} asignado.
	 */
	public CanionData(int[] vIdDisparos, long[] vTRecarga, float[] vVelocidades) {
		if( vIdDisparos  == null || vTRecarga == null || vVelocidades == null )
			throw new IllegalArgumentException("Vector nulo");
		if( vIdDisparos.length == 0 || vTRecarga.length == 0 || vVelocidades.length == 0 )
			throw new IllegalArgumentException("Vector vacio");
		if( (vIdDisparos.length != vTRecarga.length) || (vTRecarga.length != vVelocidades.length) )
			throw new IllegalArgumentException("Los vectores no tienen el mismo tamaño");
		this.vIdDisparos = vIdDisparos;
		this.vTRecarga = vTRecarga;
		this.vVelocidades = vVelocidades;
	}

	/**
	 * Devuelve el Id del {@code Disparo} que lanzara el {@code Canion} con el {@code grado} actual.
	 * @param grado indice del vector {@code IdDisparos}.
	 * @return Id del {@code Disparo}.
	 */
	public int getIdDisparo(int grado) {
		try{
			return vIdDisparos[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return vIdDisparos[grado < 0 ? 0 : vIdDisparos.length -1];
		}
	}

	/**
	 * Devuelve el tiempo de recarga del {@code Canion} con el {@code grado} actual.
	 * @param grado indice del vector {@code vTRecarga}.
	 * @return tiempo de recarga.
	 */
	public long getTRecarga(int grado) {
		try{
			return vTRecarga[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return vTRecarga[grado < 0 ? 0 : vTRecarga.length -1];
		}
	}

	/**
	 * Devuelve la velocidad a la que sale el {@code Disparo} del {@code Canion} con el {@code grado} actual.
	 * @param grado indice del vector {@code vVelocidades}.
	 * @return velocidad a la que sale el {@code Disparo} del {@code Canion}.
	 */
	public float getVelocidad(int grado) {
		try{
			return vVelocidades[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			return vVelocidades[grado < 0 ? 0 : vVelocidades.length -1];
		}
	}
}

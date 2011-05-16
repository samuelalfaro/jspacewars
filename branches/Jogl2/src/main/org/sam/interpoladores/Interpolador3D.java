/* 
 * Interpolador3D.java
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
package org.sam.interpoladores;

/**
 * Clase optimizada para interpolar valores {@code double} de tres dimensiones.
 * @see Interpolador
 */
final class Interpolador3D implements Trayectoria.Double<double[]>{

	private final transient double[] keys;
	private final transient double[][] valorInicial, valorFinal;
	private final transient double[][] compartido;
	private final transient Funcion.Double funcionesX[];
	private final transient Funcion.Double funcionesY[];
	private final transient Funcion.Double funcionesZ[];

	/**
	 * Constructor que crea un {@code Interpolador3D}.
	 * @param keys Claves asociadas a los valores.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador3D(double keys[], double[][] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.EAD(values), params );
		this.funcionesX = funciones[0];
		this.funcionesY = funciones[1];
		this.funcionesZ = funciones[2];
		this.valorInicial = new double[][]{ values[0].clone(), {0, 0, 0} };
		this.valorFinal = new double[][]{ values[values.length-1].clone(), {0, 0, 0} };
		this.compartido = new double[][]{ {0, 0, 0}, { 0, 0, 0} };
	}

	/**
	 * Constructor que crea un {@code Interpolador3D}.
	 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
	 * Los valores posibles son:<ul>
	 * <li>{@linkplain Keys#HOMOGENEAS}</li>
	 * <li>{@linkplain Keys#PROPORCIONALES}</li>
	 * </ul>
	 * @param length Valor de escalado que se aplicará a las claves generadas.
	 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador3D(int genKey, double scale, double translation, double[][] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.EAD(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Funcion.ajustarFunciones(keys, funciones);

		this.keys = keys;
		this.funcionesX = funciones[0];
		this.funcionesY = funciones[1];
		this.funcionesZ = funciones[2];
		this.valorInicial = new double[][]{ values[0].clone(), {0, 0, 0} };
		this.valorFinal = new double[][]{ values[values.length-1].clone(), {0, 0, 0} };
		this.compartido = new double[][]{{0, 0, 0},{ 0, 0, 0}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] get(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial[0];
		if(index < funcionesX.length){
			compartido[0][0] = funcionesX[index].f(key);
			compartido[0][1] = funcionesY[index].f(key);
			compartido[0][2] = funcionesZ[index].f(key);
			return compartido[0];
		}
		return valorFinal[0];
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] getTan(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial[1];
		if(index < funcionesX.length){
			compartido[1][0] = funcionesX[index].f1(key);
			compartido[1][1] = funcionesY[index].f1(key);
			compartido[1][2] = funcionesZ[index].f1(key);
			return compartido[1];
		}
		return valorFinal[1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[][] getPosTan(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			Funcion.Double fX = funcionesX[index];
			Funcion.Double fY = funcionesY[index];
			Funcion.Double fZ = funcionesZ[index];
			compartido[0][0] = fX.f(key);
			compartido[0][1] = fY.f(key);
			compartido[0][2] = fZ.f(key);
			compartido[1][0] = fX.f1(key);
			compartido[1][1] = fY.f1(key);
			compartido[1][2] = fZ.f1(key);
			return compartido;
		}
		return valorFinal;
	}
}

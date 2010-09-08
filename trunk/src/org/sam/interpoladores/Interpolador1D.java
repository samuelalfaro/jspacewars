/* 
 * Interpolador1D.java
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
 * Clase optimizada para interpolar valores {@code double} de una dimensión.
 * @see Interpolador
 */
final class Interpolador1D implements Trayectoria.Double<Double>{

	private final transient double keys[];
	private final transient Double valorInicial, valorFinal;
	private final transient Funcion.Double[] funciones;

	/**
	 * Constructor que crea un {@code Interpolador1D}.
	 * @param keys Claves asociadas a los valores.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1D(double keys[], Double[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.Numerico<Double>(values), params )[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * Constructor que crea un {@code Interpolador1D}.
	 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
	 * Los valores posibles son:<ul>
	 * <li>{@linkplain Keys#HOMOGENEAS}</li>
	 * <li>{@linkplain Keys#PROPORCIONALES}</li>
	 * </ul>
	 * @param scale Valor de escalado que se aplicará a las claves generadas.
	 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1D(int genKey, double scale, double translation, Double[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Numerico<Double>(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Funcion.ajustarFunciones(keys, funciones);

		this.keys = keys;
		this.funciones = funciones[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * Constructor que crea un {@code Interpolador1D}.
	 * @param keys Claves asociadas a los valores.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1D(double keys[], double[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.E1D(values), params )[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * Constructor que crea un {@code Interpolador1D}.
	 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
	 * Los valores posibles son:<ul>
	 * <li>{@linkplain Keys#HOMOGENEAS}</li>
	 * <li>{@linkplain Keys#PROPORCIONALES}</li>
	 * </ul>
	 * @param scale Valor de escalado que se aplicará a las claves generadas.
	 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1D(int genKey, double scale, double translation, double[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1D(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Funcion.ajustarFunciones(keys, funciones);

		this.keys = keys;
		this.funciones = funciones[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double get(double key) {
		int index = Keys.findIndexKey(key,keys);
		return (index < 0) ?
			valorInicial :
			(index < funciones.length) ?
				funciones [index].f(key) :
				valorFinal;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double getTan(double key) {
		int index = Keys.findIndexKey(key,keys);
		return (index < 0) ?
			0 :
			(index < funciones.length) ?
				funciones [index].f1(key) :
				0;
	}
	
	private transient final Double[] shared = new Double[2];
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double[] getPosTan(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0) {
			shared[0] = valorInicial;
			shared[1] = 0.0;
		}else if(index < funciones.length){
			shared[0] = funciones [index].f(key);
			shared[1] = funciones [index].f1(key);
		}else{
			shared[0] = valorFinal;
			shared[1] = 0.0;
		}
		return shared;
	}
}

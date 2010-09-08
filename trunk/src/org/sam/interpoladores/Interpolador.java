/* 
 * Interpolador.java
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
 * Clase contenedora con las implementaciones de un <i>Interpolador</i> genérico, tanto en precisión {@code double},
 * como {@code float}.
 * <p>Un <i>Interpolador</i> almacena las funciones generadas a través del {@code MetodoDeInterpolacion}.
 * Este {@code MetodoDeInterpolacion} obtiene los valores numéricos a través de un {@code Conversor<T, número[]>},
 * que se encarga de extrerlos del tipo genérico {@code T}.<br/>
 * A partir de estas funciones, se obtienen los datos interpolados en fución de la clave {@code key}
 * del método <i>T get(key)</i> y estos valores se emplean para devolver el dato del tipo genérico {@code T} a través
 * del {@code Conversor<número[], T>}.</p>
 */
final class Interpolador{

	private Interpolador(){}

    /**
     * Clase que implementa un {@code Interpolador} con precisión {@code double}.
     * @param <T> Tipo genérico de datos empleados.
     */
	static final class Double<T> implements Getter.Double<T>{

		private transient final double keys[];
		private transient final Conversor<double[], T> introductor;
		private transient final double compartido[];
		private transient final T valorInicial, valorFinal;
		private transient final Funcion.Double[][] funciones;

		/**
		 * Constructor que crea un {@code Interpolador} con precisión {@code double}.
		 * @param keys Claves asociadas a los valores.
		 * @param introductor {@code Conversor} encargado de convetir los resultados de las funciones {@code double[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param extractor {@code Conversor} encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 */
		Double (double keys[], Conversor<double[], T> introductor, T[] values, Conversor<? super T, double[]> extractor, MetodoDeInterpolacion mdi, Object... params) {

			this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.GenericoDouble<T>(values, extractor), params );

			this.keys = keys;
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new double[extractor.convert(values[0]).length];
		}

		/**
		 * Constructor que crea un {@code Interpolador} con precisión {@code double}.
		 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
		 * Los valores posibles son:<ul>
		 * <li>{@linkplain Keys#HOMOGENEAS}</li>
		 * <li>{@linkplain Keys#PROPORCIONALES}</li>
		 * </ul>
		 * @param scale Valor de escalado que se aplicará a las claves generadas.
		 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
		 * @param introductor {@code Conversor} encargado de convetir los resultados de las funciones {@code double[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param extractor {@code Conversor} encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 */
		Double(int genKey, double scale, double translation, Conversor<double[], T> introductor, 
				T[] values,	Conversor<? super T, double[]> extractor, MetodoDeInterpolacion mdi, Object... params) {

			this.funciones = mdi.generarFunciones( new ArrayExtractor.GenericoDouble<T>(values, extractor), params );
			double keys[];
			if(genKey == Keys.PROPORCIONALES)
				keys = Keys.generateKeys(funciones);
			else
				keys = Keys.generateKeys(funciones[0].length +1);
			Keys.scale(keys, scale);
			Keys.translate(keys, translation);
			Funcion.ajustarFunciones(keys, funciones);

			this.keys = keys;
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new double[extractor.convert(values[0]).length];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(double key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.convert(compartido);
			}
			return valorFinal;
		}
	}

    /**
     * Clase que implementa un {@code Interpolador} con precisión {@code float}.
     * @param <T> Tipo genérico de datos empleados.
     */
	static final class Float<T> implements Getter.Float<T>{

		private transient final float keys[];
		private transient final Conversor<float[], T> introductor;
		private transient final float compartido[];
		private transient final T valorInicial, valorFinal;
		private transient final Funcion.Float[][] funciones;

		/**
		 * Constructor que crea un {@code Interpolador} con precisión {@code float}.
		 * @param keys Claves asociadas a los valores.
		 * @param introductor {@code Conversor} encargado de convetir los resultados de las funciones {@code float[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param extractor {@code Conversor} encargado de extraer los datos en forma {@code float[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 */
		Float (float keys[], Conversor<float[], T> introductor, T[] values, Conversor<? super T, float[]> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.GenericoFloat<T>(values, extractor), params );

			this.keys = keys;
			this.funciones = new Funcion.Float[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toFloatFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new float[extractor.convert(values[0]).length];
		}

		/**
		 * Constructor que crea un {@code Interpolador} con precisión {@code float}.
		 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
		 * Los valores posibles son:<ul>
		 * <li>{@linkplain Keys#HOMOGENEAS}</li>
		 * <li>{@linkplain Keys#PROPORCIONALES}</li>
		 * </ul>
		 * @param scale Valor de escalado que se aplicará a las claves generadas.
		 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
		 * @param introductor {@code Conversor} encargado de convetir los resultados de las funciones {@code float[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param extractor {@code Conversor} encargado de extraer los datos en forma {@code float[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 */
		Float(int genKey, float scale, float translation, Conversor<float[], T> introductor, T[] values,
				Conversor<? super T, float[]> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.GenericoFloat<T>(values, extractor), params );
			double keys[];
			if(genKey == Keys.PROPORCIONALES)
				keys = Keys.generateKeys(funciones);
			else
				keys = Keys.generateKeys(funciones[0].length +1);
			Keys.scale(keys, scale);
			Keys.translate(keys, translation);
			Funcion.ajustarFunciones(keys, funciones);

			this.keys = Keys.toFloat(keys);
			this.funciones = new Funcion.Float[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toFloatFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new float[extractor.convert(values[0]).length];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(float key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.convert(compartido);
			}
			return valorFinal;
		}
	}
}

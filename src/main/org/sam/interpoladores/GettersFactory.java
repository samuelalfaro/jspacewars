/* 
 * GettersFactory.java
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
 * Clase contenedora con las clases <i>Factory</i> para crear <i>Getters</i> tanto en precisión {@code double},
 * como {@code float}.
 */
public final class GettersFactory {

	private GettersFactory(){}

    /**
     * <i>Factory</i> con los métodos estáticos para crear <i>Getters</i> precisión {@code double}.
     */
    public static class Double{
    	
    	private static final Conversor<double[], double[]> DoubleArrayPipe = new Conversor<double[], double[]>(){
    		@Override
    		public double[] convert( double[] v ){
    			return v;
    		}
    	};
    	
		private Double(){}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param keys Claves asociadas a los valores.
		 * @param values Valores a interpolar.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Double<java.lang.Double> create(double keys[], java.lang.Double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new GetterKeySelector.Double<java.lang.Double>( keys, values);
			return new Interpolador1D(keys,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
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
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Double<java.lang.Double> create(int genKey, double scale, double translation, java.lang.Double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new GetterArraySelector.Double<java.lang.Double>( scale, translation, values);
			return new Interpolador1D(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param keys Claves asociadas a los valores.
		 * @param values Valores a interpolar.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Double<java.lang.Double> create(double keys[], double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON){
				java.lang.Double[] valuesDouble = new java.lang.Double[values.length];
				for(int i=0; i< values.length; i++)
					valuesDouble[i] = values[i];
				return new GetterKeySelector.Double<java.lang.Double>( keys, valuesDouble);
			}
			return new Interpolador1D(keys,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
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
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Double<java.lang.Double> create(int genKey, double scale, double translation, double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON){
				java.lang.Double[] valuesDouble = new java.lang.Double[values.length];
				for(int i=0; i< values.length; i++)
					valuesDouble[i] = values[i];
				return new GetterArraySelector.Double<java.lang.Double>( scale, translation, valuesDouble);
			}
			return new Interpolador1D(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param keys Claves asociadas a los valores.
		 * @param values Valores a interpolar.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Double<double[]> create(double keys[], double[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2D(keys,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3D(keys,values,mdi,params);
			return new Interpolador.Double<double[]>(keys, DoubleArrayPipe, values, DoubleArrayPipe, mdi, params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
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
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Double<double[]> create(int genKey, double scale, double translation, double[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2D(genKey,scale,translation,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3D(genKey,scale,translation,values,mdi,params);
			return new Interpolador.Double<double[]>(genKey,scale,translation, DoubleArrayPipe, values, DoubleArrayPipe, mdi, params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param <T> Tipo genérico de datos empleados.
		 * @param keys Claves asociadas a los valores.
		 * @param in {@code Conversor} encargado de convetir los resultados de las funciones {@code double[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param ex {@code Conversor} encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static <T> Getter.Double<T> create(double keys[], Conversor<double[], T> in, T values[], Conversor<? super T, double[]> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new GetterKeySelector.Double<T>(keys, values);
			return new Interpolador.Double<T>(keys,in, values, ex, mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param <T> Tipo genérico de datos empleados.
		 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
		 * Los valores posibles son:<ul>
		 * <li>{@linkplain Keys#HOMOGENEAS}</li>
		 * <li>{@linkplain Keys#PROPORCIONALES}</li>
		 * </ul>
		 * @param scale Valor de escalado que se aplicará a las claves generadas.
		 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
		 * @param in {@code Conversor} encargado de convetir los resultados de las funciones {@code double[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param ex {@code Conversor} encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static <T> Getter.Double<T> create(int genKey, double scale, double translation, Conversor<double[], T> in, T values[], Conversor<? super T, double[]> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new GetterArraySelector.Double<T>( scale, translation, values);
			return new Interpolador.Double<T>(genKey,scale,translation, in, values, ex, mdi,params);
		}
	}

    /**
     * <i>Factory</i> con los métodos estáticos para crear <i>Getters</i> precisión {@code float}.
     */
    public static class Float{
    	
    	private static final Conversor<float[], float[]> FloatArrayPipe = new Conversor<float[], float[]>(){
    		@Override
    		public float[] convert( float[] v ){
    			return v;
    		}
    	};
    	
    	private Float(){}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param keys Claves asociadas a los valores.
		 * @param values Valores a interpolar.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Float<java.lang.Float> create(float keys[], java.lang.Float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null)
				return new GetterKeySelector.Float<java.lang.Float>( keys, values);
			return new Interpolador1F(keys,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
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
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Float<java.lang.Float> create(int genKey, float scale, float translation, java.lang.Float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null)
				return new GetterArraySelector.Float<java.lang.Float>( scale, translation, values);
			return new Interpolador1F(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param keys Claves asociadas a los valores.
		 * @param values Valores a interpolar.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Float<java.lang.Float> create(float keys[], float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null){
				java.lang.Float[] valuesFloat = new java.lang.Float[values.length];
				for(int i=0; i< values.length; i++)
					valuesFloat[i] = values[i];
				return new GetterKeySelector.Float<java.lang.Float>( keys, valuesFloat);
			}
			return new Interpolador1F(keys,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
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
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Float<java.lang.Float> create(int genKey, float scale, float translation, float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null){
				java.lang.Float[] valuesFloat = new java.lang.Float[values.length];
				for(int i=0; i< values.length; i++)
					valuesFloat[i] = values[i];
				return new GetterArraySelector.Float<java.lang.Float>( scale, translation, valuesFloat);
			}
			return new Interpolador1F(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param keys Claves asociadas a los valores.
		 * @param values Valores a interpolar.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Float<float[]> create(float keys[], float[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2F(keys,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3F(keys,values,mdi,params);
			return new Interpolador.Float<float[]>(keys, FloatArrayPipe, values, FloatArrayPipe, mdi, params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
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
		 * @return El <i>Getter</i> creado.
		 */
		public static Getter.Float<float[]> create(int genKey, float scale, float translation, float[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2F(genKey,scale,translation,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3F(genKey,scale,translation,values,mdi,params);
			return new Interpolador.Float<float[]>(genKey,scale,translation, FloatArrayPipe, values, FloatArrayPipe, mdi, params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param <T> Tipo genérico de datos empleados.
		 * @param keys Claves asociadas a los valores.
		 * @param in {@code Conversor} encargado de convetir los resultados de las funciones {@code double[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param ex {@code Conversor} encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static <T> Getter.Float<T> create(float keys[], Conversor<float[], T> in, T values[], Conversor<? super T, float[]> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new GetterKeySelector.Float<T>(keys, values);
			return new Interpolador.Float<T>(keys, in, values, ex, mdi,params);
		}

		/**
		 * Método estático que crea el <i>Getter</i> apropiado en función de los parámetros pasados.
		 * @param <T> Tipo genérico de datos empleados.
		 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
		 * Los valores posibles son:<ul>
		 * <li>{@linkplain Keys#HOMOGENEAS}</li>
		 * <li>{@linkplain Keys#PROPORCIONALES}</li>
		 * </ul>
		 * @param scale Valor de escalado que se aplicará a las claves generadas.
		 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
		 * @param in {@code Conversor} encargado de convetir los resultados de las funciones {@code double[]} en un valor del tipo genérico {@code T}.
		 * @param values Valores a interpolar.
		 * @param ex {@code Conversor} encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
		 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
		 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
		 * @return El <i>Getter</i> creado.
		 */
		public static <T> Getter.Float<T> create(int genKey, float scale, float translation, Conversor<float[], T> in, T values[], Conversor<? super T, float[]> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new GetterArraySelector.Float<T>( scale, translation, values);
			return new Interpolador.Float<T>(genKey,scale,translation, in, values, ex, mdi,params);
		}
	}
}

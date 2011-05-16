/* 
 * Keys.java
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
 * Clase que proporciona las constantes para definir la forma de autogenerar claves
 * y los métodos estáticos para generarlas.
 */
public final class Keys{

	private Keys(){}
	
    /**
     * Constante que indica que las claves autogeneradas, estarán distrubidas homogeneamente.
     */
    public static final int HOMOGENEAS = 0;

    /**
     * Constante que indica que la distribución de las claves autogeneradas, será propocional
     * a la longitud de los segmentos de la curva. 
     */
    public static final int PROPORCIONALES = 1;

	/**
	 * Método que escala un vector de claves multiplicandolo por un valor escalar.
	 * @param keys Vector de claves que será escalado.
	 * @param length Valor escalar.
	 */
	static void scale(double[] keys, double scale) {
		for (int i = 0; i < keys.length; i++)
			keys[i] *= scale;
	}

	/**
	 * Método que despalza un vector de claves sumandole un valor de despalzamiento.
	 * @param keys Vector de claves que será despalzado.
	 * @param translation Valor de despalzamiento.
	 */
	static void translate(double[] keys, double translation) {
		for (int i = 0; i < keys.length; i++)
			keys[i] += translation;
	}
	
	/**
	 * Método que normaliza un vector de claves, despalzando y escalando sus valores, de tal forma
	 * que el valor mínimo sea {@code 0} y el valor máximo sea {@code 1}.
	 * @param keys Vector de claves que será normalizado.
	 */
	static void normalize(double[] keys){
		double scaleKeys = 1/(keys[keys.length-1]-keys[0]);
		for (int i = 1; i < keys.length; i++){
			keys[i] -= keys[0];
			keys[i] *= scaleKeys;
		}
	}

	/**
	 * Método que calcula, mediante aproximación por subdivisión, la longitud del
	 * tramo unitario, de una curva paramétrica de varias dimensiones.
	 * @param f Vector que contiene las funciones para cada una de las dimensiones.
	 * @param pasos Número de subdivisiones empleadas para calcular la longitud.
	 * @return La longitud del tramo.
	 */
	static double longitudCurva(Funcion.Double f[], int pasos){
		double ant[] = new double[f.length];
		for(int i=0; i< f.length; i++)
			ant[i] = f[i].f(0);
		
		double len = 0;
		for(int p =1; p<= pasos; p++){
			double t = p/pasos;
			double len_seg  = 0.0;
			for(int i=0; i< f.length; i++){
				double ft = f[i].f(t);
				len_seg += Math.pow(ant[i] - ft, 2);
				ant[i] = ft;
			}
			len += Math.sqrt(len_seg);
		}
		return len;
	}
	
	/**
	 * Método que genera un vector de claves, distribuidas homogeneamente entre los valores {@code 0} y {@code 1}.
	 * @param tam Tamaño del vector a generar.
	 * @return El vector de claves geneado.
	 */
	static double[] generateKeys(int tam){
		double keys[] = new double[tam];
		double k = 0.0, iK = 1.0/(tam-1);
		for (int i=0; i<tam; i++, k+=iK)
			keys[i] = k;
		return keys;
	}
	
	/**
	 * Método que gerena un vector de claves, distribuidas entre los valores {@code 0} y {@code 1}, proporcionales a la
	 * longitud de los tramos de las curva, definidos por el vector bidimensional de {@linkplain Funcion.Double
	 * funciones con precisión double}, que recibe como parámetro.
	 * 
	 * @param funciones Funciones que definen los tramos.
	 * @return El vector de claves geneado.
	 */
	static double[] generateKeys(Funcion.Double[][] funciones){
		Funcion.Double f[] = new Funcion.Double[funciones.length];
		int len = funciones[0].length;
		double keys[] = new double[len+1];
		keys[0] = 0.0;
		for (int i=0; i< len; i++){
			for(int j=0; j < funciones.length; j++)
				f[j] = funciones[j][i];
			keys[i+1] = keys[i] + longitudCurva(f, 512);
		}
		normalize(keys);
		return keys;
	}
	
	/**
	 * Método que genera un vector de claves {@code float} a partir de un vector de claves {@code double}.
	 * @param keys El vector de claves {@code double} a convertir.
	 * @return El vector de claves {@code float} generado.
	 */
	static float[] toFloat(double keys[]){
		float[] fKeys = new float[keys.length];
		for(int i= 0; i< keys.length; i++)
			fKeys[i] = (float)keys[i];
		return fKeys;
	}

	/**
	 * Método que indica si un vector de claves está ordenado.
	 * @param keys El vector de claves a evaluar.
	 * @return <ul>
	 * <li>{@code true} si el vector está ordenado.</li>
	 * <li>{@code false} en caso contrario.</li>
	 * </ul>
	 */
	static boolean estaOrdenado(double keys[]){
		for(int i = 0, len = keys.length -1; i < len; )
			if(keys[i] > keys[++i])
				return false;
		return true;
	}
	
	/**
	 * Método que indica si un vector de claves está ordenado.
	 * @param keys El vector de claves a evaluar.
	 * @return <ul>
	 * <li>{@code true} si el vector está ordenado.</li>
	 * <li>{@code false} en caso contrario.</li>
	 * </ul>
	 */
	static boolean estaOrdenado(float keys[]){
		for(int i = 0, len = keys.length -1; i < len; )
			if(keys[i] > keys[++i])
				return false;
		return true;
	}
	
	/**
	 * Método que indica si un vector de claves está ordenado.
	 * @param keys El vector de claves a evaluar.
	 * @return <ul>
	 * <li>{@code true} si el vector está ordenado.</li>
	 * <li>{@code false} en caso contrario.</li>
	 * </ul>
	 */
	static boolean estaOrdenado(int keys[]){
		for(int i = 0, len = keys.length -1; i < len; )
			if(keys[i] > keys[++i])
				return false;
		return true;
	}
	
	/**
	 * Realiza una busqueda dicotómica en un vector.
	 * @param key El elemento buscado.
	 * @param keys El vector donde buscar el elemento.
	 * @return <ul>
	 * <li> El índice del mayor elemento del vector que es menor o igual al elemento buscado.</li>
	 * <li>{@code -1} en caso de que el vector no contenga ningun elemento menor que el buscado.</li>
	 * </ul>
	 */
	static int findIndexKey(double key, double keys[]){
		int izq = 0;
		int der = keys.length - 1;
		int med = der >> 1;

		if(key < keys[0])
			return -1;
		if(key == keys[0])
			return 0;
		if(key >= keys[der])
			return der;
		// busqueda dicotomica
		while(izq < der){
			if(key == keys[med])
				return med;
			if(key < keys[med])
				der = med;
			else
				izq = med+1;
			med = (izq + der) >> 1;
		}
		return med-1;
	}

	/**
	 * Realiza una busqueda dicotómica en un vector.
	 * @param key El elemento buscado.
	 * @param keys El vector donde buscar el elemento.
	 * @return <ul>
	 * <li> El índice del mayor elemento del vector que es menor o igual al elemento buscado.</li>
	 * <li>{@code -1} en caso de que el vector no contenga ningun elemento menor que el buscado.</li>
	 * </ul>
	 */
	static int findIndexKey(float key, float keys[]){
		int izq = 0;
		int der = keys.length - 1;
		int med = der >> 1;

		if(key < keys[0])
			return -1;
		if(key == keys[0])
			return 0;
		if(key >= keys[der])
			return der;
		// busqueda dicotomica
		while(izq < der){
			if(key == keys[med])
				return med;
			if(key < keys[med])
				der = med;
			else
				izq = med+1;
			med = (izq + der) >> 1;
		}
		return med-1;
	}
	
	/**
	 * Realiza una busqueda dicotómica en un vector.
	 * @param key El elemento buscado.
	 * @param keys El vector donde buscar el elemento.
	 * @return <ul>
	 * <li> El índice del mayor elemento del vector que es menor o igual al elemento buscado.</li>
	 * <li>{@code -1} en caso de que el vector no contenga ningun elemento menor que el buscado.</li>
	 * </ul>
	 */
	static int findIndexKey(int key, int keys[]){
		int izq = 0;
		int der = keys.length - 1;
		int med = der >> 1;

		if(key < keys[0])
			return -1;
		if(key == keys[0])
			return 0;
		if(key >= keys[der])
			return der;
		// busqueda dicotomica
		while(izq < der){
			if(key == keys[med])
				return med;
			if(key < keys[med])
				der = med;
			else
				izq = med+1;
			med = (izq + der) >> 1;
		}
		return med-1;
	}
}

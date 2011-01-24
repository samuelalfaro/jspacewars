/* 
 * ArrayExtractor.java
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
 * Clase abstracta que permite encapsular un vector de cualquier tipo,
 * y poder acceder a sus valores como si fuera una matriz de {@code double}.
 */
public abstract class ArrayExtractor{
	
	private ArrayExtractor(){}
	
    /**
     * Impementación de {@code ArrayExtractor} que extrae los valores del tipo genérico {@code T}
     * en forma de un vector {@code double[]}.
     * 
     * @param <T> Tipo genérico de datos empleados.
     */
    static final class GenericoDouble<T> extends ArrayExtractor{
		private final T v[];
		private final int dimensions;
		private final Conversor<? super T, double[]> extractor;
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
         * @param extractor encargado de extraer los datos en forma {@code double[]} del tipo genérico {@code T}.
         */
        public GenericoDouble(T[] v, Conversor<? super T, double[]> extractor){
			this.v = v;
			this.dimensions = extractor.convert(v[0]).length;
			this.extractor = extractor;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return dimensions;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return extractor.convert(v[index])[dim];
		}
	}

    /**
     * Impementación de {@code ArrayExtractor} que extrae los valores del tipo genérico {@code T}
     * en forma de un vector {@code float[]}.
     * 
     * @param <T> Tipo genérico de datos empleados.
     */
    static final class GenericoFloat<T> extends ArrayExtractor{
    	private final T v[];
		private final int dimensions;
		private final Conversor<? super T, float[]> extractor;
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
         * @param extractor encargado de extraer los datos en forma {@code float[]} del tipo genérico {@code T}.
         */
        public GenericoFloat(T[] v, Conversor<? super T, float[]> extractor){
			this.v = v;
			this.dimensions = extractor.convert(v[0]).length;
			this.extractor = extractor;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return dimensions;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
    	public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
    		return extractor.convert(v[index])[dim];
		}
	}
	
    /**
     * Impementación de {@code ArrayExtractor} optimizada para un vector {@code <T extends Number>[]}.
     * @param <T> Tipo numérico genérico de datos empleados.
     */
    static final class Numerico<T extends Number> extends ArrayExtractor{
		private final T v[];
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public Numerico(T v[]){
			this.v = v;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index].doubleValue();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index].doubleValue();
		}
	}
	
	/**
	 * Impementación de {@code ArrayExtractor} optimizada para un vector {@code double[]}.
	 */
    static final class E1D extends ArrayExtractor{
		private final double v[];
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public E1D(double v[]){
			this.v = v;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}
        
    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index];
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index];
		}
	}
	
	/**
	 * Impementación de {@code ArrayExtractor} optimizada para un vector {@code float[]}.
	 */
    static final class E1F extends ArrayExtractor{
		private final float v[];
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public E1F(float v[]){
			this.v = v;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index];
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index];
		}
	}

	/**
	 * Impementación de {@code ArrayExtractor} optimizada para un vector {@code int[]}.
	 */
    static final class E1I extends ArrayExtractor{
		private final int v[];
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public E1I(int v[]){
			this.v = v;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index];
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index];
		}
	}
    
	/**
	 * Impementación de {@code ArrayExtractor} optimizada para un vector {@code double[][]}.
	 */
    static final class EAD extends ArrayExtractor{
		private final double[] v[];
		private final int dimensions;
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public EAD(double[] v[]){
			this.v = v;
			this.dimensions = v[0].length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public final int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return dimensions;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
        public final double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public final double at(int index, int dim){
			return v[index][dim];
		}
	}
	
	/**
	 * Impementación de {@code ArrayExtractor} optimizada para un vector {@code float[][]}.
	 */
    static final class EAF extends ArrayExtractor{
		private final float[] v[];
		private final int dimensions;
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public EAF(float[] v[]){
			this.v = v;
			this.dimensions = v[0].length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public final int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return dimensions;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public final double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public final double at(int index, int dim){
			return v[index][dim];
		}
	}

	/**
	 * Impementación de {@code ArrayExtractor} optimizada para un vector {@code int[][]}.
	 */
    static final class EAI extends ArrayExtractor{
    	
		private final int[] v[];
		private final int dimensions;
		
		/**
		 * Constructor que crea una {@code ArrayExtractor} que encapsula un 
		 * vector {@code v} que recibe como parámetro.
		 * 
		 * @param v vector a encapsular.
		 */
        public EAI(int[] v[]){
			this.v = v;
			this.dimensions = v[0].length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public final int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return dimensions;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public final double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public final double at(int index, int dim){
			return v[index][dim];
		}
	}
    
    /**
     * @return longitud del vector encapsulado.
     */
    public abstract int length();
	
    /**
     * @return dimensiones de los elementos que contiene el vector.
     */
    public abstract int dimensions();
	
    /**
     * Método que devuelve un valor {@code double} a partir del vector encapusulado.
     * 
     * @param index índice dentro del vector encapusulado.
     * @return el valor de esa posición.
     * @throws UnsupportedOperationException cuando la operación no está soportada.
     * Puesto que este método es una optimización para los valores de una única dimensión.
     */
    public abstract double at(int index) throws UnsupportedOperationException;
	
    /**
     * Método que devuelve un valor {@code double} a partir del vector encapusulado.
     * 
     * @param index índice dentro del vector encapusulado.
     * @param dim índice dentro de las dimensiónes del elemento correspondiente.
     * @return el valor de esa posición.
     */
    public abstract double at(int index, int dim);
}

/* 
 * PolinomioLineal.java
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
 * Polinomio de primer grado que reprensenta la función lineal {@code f(x) = A·x + B} .
 */
public final class PolinomioLineal{

	private PolinomioLineal(){}

	/**
	 * Implementacion con precisión Double de la función {@link PolinomioLineal}.
	 */
	public static final class Double implements Funcion.Double {
		private double A, B;

		/**
		 * Crea el polinomio lineal con precisión {@code double}, correspondiente a {@code f(x) = A·x + B} .
         *
         * @param A pendiente de la recta.
         * @param B desplazamiento.
         */
		public Double(double A, double B){
			this.A = A;
			this.B = B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f(double x) {
			return A*x + B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f1(double x) {
			return A;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f2(double x) {
			return 0;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleIn(double scale) {
			/*
			 * A(K*x) + B  =  AKx + B
			 */
			A*= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleOut(double scale) {
			/*
			 * (Ax + B)*K = KAx + KB
			 */
			A*= scale;
			B*= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateIn(double translation) {
			/*
			 * A(x +K) + B  =  Ax + (AK + B)
			 */
			B += A * translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateOut(double translation) {
			/*
			 * (Ax + B) + K = Ax + (B + K)
			 */
			B += translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public Funcion.Float toFloatFunction() {
			return new PolinomioLineal.Float( (float)A, (float)B );
		}
	}

	/**
	 * Implementacion con precisión Float de la función {@link PolinomioLineal}.
	 */
	public static final class Float implements Funcion.Float {
		private float A, B;

		/**
		 * Crea el polinomio lineal con precisión {@code float}, correspondiente a {@code f(x) = A·x + B} .
         *
         * @param A pendiente de la recta.
         * @param B desplazamiento.
         */
		public Float(float A, float B){
			this.A = A;
			this.B = B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f(float x) {
			return A*x + B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f1(float x) {
			return A;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f2(float x) {
			return 0;
		}
	}
}

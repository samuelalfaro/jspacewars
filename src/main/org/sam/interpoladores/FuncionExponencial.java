/* 
 * FuncionExponencial.java
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
 * Función exponencial correspondiente a {@code f(x) = A·Math.pow( (C·x + D), gamma ) + B}.
 */
public final class FuncionExponencial{

	private FuncionExponencial(){}

	/**
	 * Implementación con precisión Double de la {@linkplain FuncionExponencial función exponencial}.
	 */
	public static final class Double implements Funcion.Double {
		private double A, B, C, D, gamma;

		/**
		 * Crea la función exponencial, con precisión {@code double} correspondiente
		 * a {@code f(x) = A·Math.pow( (C·x + D), gamma ) + B}.
         *
         * @param A escala de la potencia.
         * @param B desplazamiento de la potencia.
         * @param C factor lineal.
         * @param D factor independiente.
         * @param gamma exponente.
         */
		public Double(double A ,double B, double C, double D, double gamma){
			this.A = A;
			this.B = B;
			this.C = C;
			this.D = D;
			this.gamma = gamma;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f(double x) {
			return A * Math.pow( C * x + D, gamma) + B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f1(double x) {
			//TODO calcular la derivada
			throw new UnsupportedOperationException();
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f2(double x) {
			//TODO calcular la segunda derivada
			throw new UnsupportedOperationException();
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleIn(double scale) {
			/*
			 *  A·(C·(K·x) + D)^gamma + B = A(C·K·x + D)^gamma + B
			 */
			C*= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleOut(double scale) {
			/*
			 *  (A·(C·x + D)^gamma + B)·K = A·K·(C·x + D)^gamma + B·K
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
			 *  A·(C·(x + K) + D)^gamma + B = A·(C·x + C·K + D)^gamma + B
			 */
			D += C * translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateOut(double translation) {
			/*
			 *  (A·(C·x + D)^gamma + B) + K = A·(C·x + D)^gamma + B + K
			 */
			B += translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public Funcion.Float toFloatFunction() {
			return new FuncionExponencial.Float( (float)A, (float)B, (float)C, (float)D, (float)gamma );
		}
	}

	/**
	 * Implementación con precisión Float de la {@linkplain FuncionExponencial función exponencial}.
	 */
	public static final class Float implements Funcion.Float {

		private float A, B, C, D, gamma;

		/**
		 * Crea la función exponencial, con precisión {@code float} correspondiente
		 * a {@code f(x) = A·Math.pow( (C·x + D), gamma ) + B}.
         *
         * @param A escala de la potencia.
         * @param B desplazamiento de la potencia.
         * @param C factor lineal.
         * @param D factor independiente.
         * @param gamma exponente.
         */
		public Float(float A ,float B, float C, float D, float gamma){
			this.A = A;
			this.B = B;
			this.C = C;
			this.D = D;
			this.gamma = gamma;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f(float x) {
			return A * (float)Math.pow( C * x + D, gamma) + B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f1(float x) {
			//TODO calcular la derivada
			throw new UnsupportedOperationException();
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f2(float x) {
			//TODO calcular la segunda derivada
			throw new UnsupportedOperationException();
		}
	}
}

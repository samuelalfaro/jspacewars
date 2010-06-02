package org.sam.interpoladores;

/**
 * Función que representa el siguiente exponencial: <code>f(x) = A·Math.pow( (C·x + D), gamma ) + B</code>
 */
public final class Exponencial{

	private Exponencial(){}

	/**
	 * Implementación con precisión Double de la función {@link Exponencial}.
	 */
	public static final class Double implements Funcion.Double {
		private double A, B, C, D, gamma;

		/**
		 * Crea la función exponencial correspondiente a <code>f(x) = A·Math.pow( (C·x + D), gamma ) + B</code>
         *
         * @param A
         * @param B
         * @param C 
         * @param gamma
         * @param D
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
			throw new UnsupportedOperationException();
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f2(double x) {
			throw new UnsupportedOperationException();
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleIn(double scale) {
			/*
			 *  A(C*( x * K ) + D)^gamma + B = A(CK*x + D)^gamma + B
			 */
			C*= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleOut(double scale) {
			/*
			 *  ( A(Cx + D)^gamma + B ) * K = AK(Cx + D)^gamma + BK
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
			 *  A( C( x + K ) + D)^gamma + B = A(Cx + (CK + D) )^gamma + B
			 */
			D += C * translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateOut(double translation) {
			/*
			 *  ( A(Cx + D)^gamma + B ) + K = A(Cx + D)^gamma + (B+K)
			 */
			B += translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public Funcion.Float toFloatFunction() {
			return new Exponencial.Float(A,B,C,D,gamma);
		}
	}

	/**
	 * Implementación con precisión Float de la función {@link Exponencial}.
	 */
	public static final class Float implements Funcion.Float {

		private float A, B, C, D, gamma;

		/**
		 * Crea la función exponencial correspondiente a <code>f(x) = A·Math.pow( (C·x + D), gamma ) + B</code>
         *
         * @param A
         * @param B
         * @param C
         * @param D
         * @param gamma
         */
		public Float(double A ,double B, double C, double D, double gamma){
			this.A = (float)A;
			this.B = (float)B;
			this.C = (float)C;
			this.D = (float)D;
			this.gamma = (float)gamma;
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
			throw new UnsupportedOperationException();
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f2(float x) {
			throw new UnsupportedOperationException();
		}
	}
}

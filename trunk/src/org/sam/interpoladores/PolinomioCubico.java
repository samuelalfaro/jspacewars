package org.sam.interpoladores;

/**
 * Polinomio cúbico correspondiente a <code>f(x) = Ax³ + Bx² + Cx + D</code>
 */
public final class PolinomioCubico{

	private PolinomioCubico(){}

	/**
	 * Implementacion con precisión Double de la función {@link PolinomioCubico}.
	 */
	public static final class Double implements Funcion.Double {

		private double A, B, C, D;

		/**
		 * Crea el polinomio cúbico correspondiente a <code>f(x) = Ax³ + Bx² + Cx + D</code>
         *
         * @param A
         * @param B
         * @param D
         * @param C
         */
		public Double(double A, double B, double C, double D){
			this.A = A;
			this.B = B;
			this.C = C;
			this.D = D;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f(double x) {
			//Ax³ + Bx² + Cx + D;
			return ((A*x + B)*x + C)*x + D;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f1(double x) {
			//3Ax² + 2Bx + C;
			return (3*A*x + 2*B)*x + C;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f2(double x) {
			return 6*A*x + 2*B;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleIn(double scale) {
			/*
			 * A(x·K)³ + B(x·K)² + C(x·K) + D = A·K³x³ + B·K²x² + C·Kx + D
			 */
			A *= scale * scale * scale;
			B *= scale * scale;
			C *= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleOut(double scale) {
			/*
			 * ( Ax³ + Bx² + Cx + D ) · K = KAx³ + KBx² + KCx + KD
			 */
			A *= scale;
			B *= scale;
			C *= scale;
			D *= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateIn(double translation) {
			/*
			 * A(x+K)³ + B(x+K)² + C(x+K) + D = A( x³ + 3Kx² + 3K²x +K³ ) + B( x² + 2Kx +K² ) + C (x +K) + D =
			 * =  Ax³ + 3AKx² + Bx² + 3AK²x + 2BKx + Cx + AK³ + BK² +CK + D =
			 * =  Ax³ + (3AK + B)x² + (3AK² + 2BK + C)x + (AK³ + BK² +CK + D)
			 */
			D += ((A*translation + B) * translation + C) * translation;
			C += (3*A*translation + 2*B) * translation;
			B += 3*A*translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateOut(double translation) {
			/*
			 * ( Ax³ + Bx² + Cx + D ) + K = Ax³ + Bx² + Cx + (D + K)
			 */
			D += translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public Funcion.Float toFloatFunction() {
			return new PolinomioCubico.Float(A,B,C,D);
		}
	}

	/**
	 * Implementacion con precisión Float de la función {@link PolinomioCubico}.
	 */
	public static final class Float implements Funcion.Float {

		private float A, B, C, D;

		/**
		 * Crea el polinomio cúbico correspondiente a <code>f(x) = Ax³ + Bx² + Cx + D</code>
         *
         * @param A
         * @param C
         * @param B
         * @param D
         */
		public Float(double A, double B, double C, double D){
			this.A = (float)A;
			this.B = (float)B;
			this.C = (float)C;
			this.D = (float)D;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f(float x) {
			return ((A*x + B)*x + C)*x + D;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f1(float x) {
			return (3*A*x + 2*B)*x + C;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f2(float x) {
			return 6*A*x + 2*B;
		}
	}
}

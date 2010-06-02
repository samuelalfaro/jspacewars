package org.sam.interpoladores;

/**
 * Polinomio de primer grado que reprensenta la función lineal <code>f(x) = A*x + B</code>
 */
public final class PolinomioLineal{

	private PolinomioLineal(){}

	/**
	 * Implementacion con precisión Double de la función {@link PolinomioLineal}.
	 */
	public static final class Double implements Funcion.Double {
		double A, B;

		/**
		 * Crea el polinomio lineal correspondiente a <code>f(x) = A*x + B</code>
         *
         * @param A
         * @param B
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
			return new PolinomioLineal.Float(A,B);
		}
	}

	/**
	 * Implementacion con precisión Float de la función {@link PolinomioLineal}.
	 */
	public static final class Float implements Funcion.Float {
		float A, B;

		/**
		 * Crea el polinomio lineal correspondiente a <code>f(x) = A*x + B</code>
         *
         * @param A
         * @param B
         */
		public Float(double A, double B){
			this.A = (float)A;
			this.B = (float)B;
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

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

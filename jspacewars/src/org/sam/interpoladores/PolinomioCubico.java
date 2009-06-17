package org.sam.interpoladores;

/**
 * Polinomio cúbico correspondiente a <code>f(x) = Ax³ + Bx² + Cx + D</code>
 */
public final class PolinomioCubico{

	private PolinomioCubico(){}

	/**
	 * Implementacion con precision Double del polinomio cúbico correspondiente.
	 */
	public static final class Double implements Funcion.Double {

		double A, B, C, D;

		/**
		 * Crea el polinomio cúbico correspondiente a <code>f(x) = A*x^3 + B*x^2 + C*x + D</code>
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

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f(double)
		 */
		public double f(double x) {
			//Ax³ + Bx² + Cx + D;
			return ((A*x + B)*x + C)*x + D;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f1(double)
		 */
		public double f1(double x) {
			//3Ax² + 2Bx + C;
			return (3*A*x + 2*B)*x + C;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f2(double)
		 */
		public double f2(double x) {
			return 6*A*x + 2*B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleIn(double)
		 */
        /**
         *
         * @param scale
         */
        public void scaleIn(double scale) {
			/*
			 * A(x·K)³ + B(x·K)² + C(x·K) + D = A·K³x³ + B·K²x² + C·Kx + D
			 */
			A *= scale * scale * scale;
			B *= scale * scale;
			C *= scale;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleOut(double)
		 */
        /**
         *
         * @param scale
         */
        public void scaleOut(double scale) {
			/*
			 * ( Ax³ + Bx² + Cx + D ) · K = KAx³ + KBx² + KCx + KD
			 */
			A *= scale;
			B *= scale;
			C *= scale;
			D *= scale;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#translateIn(double)
		 */
        /**
         *
         * @param translation
         */
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

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#translateOut(double)
		 */
        /**
         *
         * @param translation
         */
        public void translateOut(double translation) {
			/*
			 * ( Ax³ + Bx² + Cx + D ) + K = Ax³ + Bx² + Cx + (D + K)
			 */
			D += translation;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toFloat()
		 */
        /**
         *
         * @return
         */
        public Funcion.Float toFloatFunction() {
			return new PolinomioCubico.Float(A,B,C,D);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toInteger()
		 */
        /**
         *
         * @return
         */
        public Funcion.Integer toIntegerFunction() {
			return new PolinomioCubico.Integer(A,B,C,D);
		}
	}

	/**
	 * Implementacion con precision Float del polinomio cúbico correspondiente.
	 */
	public static final class Float implements Funcion.Float {

		float A, B, C, D;

		/**
		 * Crea el polinomio cúbico correspondiente a <code>f(x) = A*x^3 + B*x^2 + C*x + D</code>
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

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f(float)
		 */
		public float f(float x) {
			return ((A*x + B)*x + C)*x + D;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f1(float)
		 */
		public float f1(float x) {
			return (3*A*x + 2*B)*x + C;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f2(float)
		 */
		public float f2(float x) {
			return 6*A*x + 2*B;
		}
	}

	/**
	 * Implementacion con precision Integer del polinomio cúbico correspondiente.
	 */
	public static final class Integer implements Funcion.Integer {
//		TODO Implementar
//		float A, B, C, D;

		/**
		 * Crea el polinomio cúbico correspondiente a <code>f(x) = A*x^3 + B*x^2 + C*x + D</code>
         *
         * @param A
         * @param B
         * @param D
         * @param C
         */
		public Integer(double A, double B, double C, double D){
//			this.A = (float)A;
//			this.B = (float)B;
//			this.C = (float)C;
//			this.D = (float)D;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f(int)
		 */
		public int f(int x) {
//			return ((A*x + B)*x + C)*x + D;
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f1(int)
		 */
		public int f1(int x) {
//			return (3*A*x + 2*B)*x + C;
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f2(int)
		 */
		public int f2(int x) {
//			return 6*A*x + 2*B;
			return 0;
		}
	}
}

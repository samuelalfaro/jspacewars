package org.sam.interpoladores;

/**
 * Función que representa el siguiente cociente polinómico: <code>(A*x + B)/(C*x + D)</code>
 */
public final class CocientePolinomico{

	private CocientePolinomico(){}

	/**
	 * Implementación con precisión Double de la función {@link CocientePolinomico}.
	 */
	public static final class Double implements Funcion.Double {

		double A, B, C, D;

		/**
		 * Crea el cociente polinómico correspondiente a: <code>(A*x + B)/(C*x + D)</code>
         *
         * @param A
         * @param B
         * @param C
         * @param D
         */
		public Double(double A ,double B, double C, double D){
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
			return (A*x + B)/(C*x + D);
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f1(double x) {
			/*	(f/g)' = f'·g -f·g'/ g² ->
			 *	-> ( A·(Cx+D) - C·(Ax +B) )/ (Cx+D)² =
			 *  =  (A·D - C·B )/ (Cx+D)²
			 */
			double g = C*x + D;
			return (A*D - B*C) /(g*g);
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public double f2(double x) {
			/*	(f/g)' = f'·g -f·g'/ g² ->
			 *  ->  ( (A·D - C·B )/ (Cx+D)² )' =
			 *  = ( 0 · (Cx+D)² -  (A·D - C·B ) · ( (Cx+D)² )' )/ (Cx+D)^4 =
			 *  = ( (A·D - C·B ) · ( (Cx+D)² )' ) / (Cx+D)^4 =
			 *  = ( (A·D - C·B ) · 2 · C · (Cx+D) ) / (Cx+D)^4 =
			 *  = ( (A·D - C·B ) · 2C ) / (Cx+D)³
			 */
			double g = C*x + D;
			return ((A*D - B*C)*2*C)/(g*g*g);
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleIn(double scale) {
			/*
			 * A(K·x) + B    AKx + B
			 * ----------- = -------
			 * C(K·x) + D    CKx + D
			 */
			A*= scale;
			C*= scale;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void scaleOut(double scale) {
			/*
			 * Ax + B       KAx + KB
			 * ------ · K = ---------
			 * Cx + D        Cx + D
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
			 * A(x + K) + B    Ax + (AK + B)
			 * ------------ = -------------
			 * C(x + K) + D    Cx + (CK + D)
			 */
			B += A * translation;
			D += C * translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public void translateOut(double translation) {
			/*
			 * Ax + B       Ax + B + K(Cx + D)   (A +KC)x + B + KD
			 * ------ + K = ------------------ = -----------------
			 * Cx + D              Cx + D             Cx + D
			 */
			A += C*translation;
			B += D*translation;
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
        public Funcion.Float toFloatFunction() {
			return new CocientePolinomico.Float(A,B,C,D);
		}
	}

	/**
	 * Implementación con precisión Float de la función {@link CocientePolinomico}.
	 */
	public static final class Float implements Funcion.Float {

		float A, B, C, D;

		/**
		 * Crea el cociente polinómico correspondiente a: <code>(A*x + B)/(C*x + D)</code>
         *
         * @param A
         * @param D
         * @param B
         * @param C
         */
		public Float(double A ,double B, double C, double D){
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
			return (A*x + B)/(C*x + D);
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f1(float x) {
			float g = C*x + D;
			return (A*D - B*C) /(g*g);
		}

		/**
		 *{@inheritDoc}
		 */
		@Override
		public float f2(float x) {
			float g = C*x + D;
			return ((A*D - B*C)*2*C)/(g*g*g);
		}
	}
}

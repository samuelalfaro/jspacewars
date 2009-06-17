package org.sam.interpoladores;

/**
 * Función que representa el siguiente cociente polinómico: <code>(A*x + B)/(C*x + D)</code>
 */
public final class CocientePolinomico{

	private CocientePolinomico(){}

	/**
	 * Implementación con precisión Double del cociente polinómico.
	 * @author samuel
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

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f(double)
		 */
		public double f(double x) {
			return (A*x + B)/(C*x + D);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f1(double)
		 */
		public double f1(double x) {
			/*	(f/g)' = f'·g -f·g'/ g² ->
			 *	-> ( A·(Cx+D) - C·(Ax +B) )/ (Cx+D)² =
			 *  =  (A·D - C·B )/ (Cx+D)²
			 */
			double g = C*x + D;
			return (A*D - B*C) /(g*g);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f2(double)
		 */
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

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleIn(double)
		 */
        /**
         *
         * @param scale
         */
        public void scaleIn(double scale) {
			/*
			 * A(K·x) + B    AKx + B
			 * ----------- = -------
			 * C(K·x) + D    CKx + D
			 */
			A*= scale;
			C*= scale;
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
			 * Ax + B       KAx + KB
			 * ------ · K = ---------
			 * Cx + D        Cx + D
			 */
			A*= scale;
			B*= scale;
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
			 * A(x + K) + B    Ax + (AK + B)
			 * ------------ = -------------
			 * C(x + K) + D    Cx + (CK + D)
			 */
			B += A * translation;
			D += C * translation;
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
			 * Ax + B       Ax + B + K(Cx + D)   (A +KC)x + B + KD
			 * ------ + K = ------------------ = -----------------
			 * Cx + D              Cx + D             Cx + D
			 */
			A += C*translation;
			B += D*translation;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toFloat()
		 */
        /**
         *
         * @return
         */
        public Funcion.Float toFloatFunction() {
			return new CocientePolinomico.Float(A,B,C,D);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toInteger()
		 */
        /**
         *
         * @return
         */
        public Funcion.Integer toIntegerFunction() {
			return new CocientePolinomico.Integer(A,B,C,D);
		}
	}

	/**
	 * Implementación con precisión Float del cociente polinómico.
	 * @author samuel
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

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f(float)
		 */
		public float f(float x) {
			return (A*x + B)/(C*x + D);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f1(float)
		 */
		public float f1(float x) {
			float g = C*x + D;
			return (A*D - B*C) /(g*g);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f2(float)
		 */
		public float f2(float x) {
			float g = C*x + D;
			return ((A*D - B*C)*2*C)/(g*g*g);
		}
	}

	/**
	 * Implementación con precisión Integer del cociente polinómico.
	 * @author samuel
	 */
	public static final class Integer implements Funcion.Integer {
		//TODO Implementar
//		int A, B, C, D;

		/**
		 * Crea el cociente polinómico correspondiente a: <code>(A*x + B)/(C*x + D)</code>
         *
         * @param A 
         * @param D
         * @param B
         * @param C
         */
		public Integer(double A ,double B, double C, double D){
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f(int)
		 */
		public int f(int x) {
//			return (A*x + B)/(C*x + D);
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f1(int)
		 */
		public int f1(int x) {
//			float g = C*x + D;
//			return (A*D - B*C) /(g*g);
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f2(int)
		 */
		public int f2(int x) {
//			float g = C*x + D;
//			return ((A*D - B*C)*2*C)/(g*g*g);
			return 0;
		}
	}
}

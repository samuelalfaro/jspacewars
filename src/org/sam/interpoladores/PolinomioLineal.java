package org.sam.interpoladores;

/**
 * Polinomio de primer grado que reprensenta la funcion lineal <code>f(x) = A*x + B</code>
 */
public final class PolinomioLineal{

	private PolinomioLineal(){}

	/**
	 * Implementacion con precisión Double de la funcion lineal correspondiente.
	 */
	public static final class Double implements Funcion.Double {
		double A, B;

		/**
		 * Crea el polinomio lineal correspondiente a <code>f(x) = A*x + B</code>
		 */
		public Double(double A, double B){
			this.A = A;
			this.B = B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f(double)
		 */
		public double f(double x) {
			return A*x + B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f1(double)
		 */
		public double f1(double x) {
			return A;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f2(double)
		 */
		public double f2(double x) {
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleIn(double)
		 */
		public void scaleIn(double scale) {
			/*
			 * A(K*x) + B  =  AKx + B
			 */
			A*= scale;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleOut(double)
		 */
		public void scaleOut(double scale) {
			/*
			 * (Ax + B)*K = KAx + KB
			 */
			A*= scale;
			B*= scale;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#translateIn(double)
		 */
		public void translateIn(double translation) {
			/*
			 * A(x +K) + B  =  Ax + (AK + B)
			 */
			B += A * translation;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#translateOut(double)
		 */
		public void translateOut(double translation) {
			/*
			 * (Ax + B) + K = Ax + (B + K)
			 */
			B += translation;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toFloat()
		 */
		public Funcion.Float toFloat() {
			return new PolinomioLineal.Float(A,B);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toInteger()
		 */
		public Funcion.Integer toInteger() {
			return new PolinomioLineal.Integer(A,B);
		}
	}

	/**
	 * Implementacion con precisión Float de la funcion lineal correspondiente.
	 */
	public static final class Float implements Funcion.Float {
		float A, B;

		/**
		 * Crea el polinomio lineal correspondiente a <code>f(x) = A*x + B</code>
		 */
		public Float(double A, double B){
			this.A = (float)A;
			this.B = (float)B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f(float)
		 */
		public float f(float x) {
			return A*x + B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f1(float)
		 */
		public float f1(float x) {
			return A;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f2(float)
		 */
		public float f2(float x) {
			return 0;
		}
	}

	/**
	 * Implementacion con precisión Integer de la funcion lineal correspondiente.
	 */
	public static final class Integer implements Funcion.Integer {
		int A, B;

		/**
		 * Crea el polinomio lineal correspondiente a <code>f(x) = A*x + B</code>
		 */
		public Integer(double A, double B){
			this.A = (int)A;
			this.B = (int)B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f(int)
		 */
		public int f(int x) {
			return A*x + B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f1(int)
		 */
		public int f1(int x) {
			return A;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f2(int)
		 */
		public int f2(int x) {
			return 0;
		}
	}
}

package org.sam.interpoladores;

/**
 * Función que representa el siguiente exponencial: <code>f(x) = A*Math.pow((C*x + D), gamma) + B</code>
 */
public final class Exponencial{

	private Exponencial(){};

	/**
	 * Implementación con precisión Double de la función exponencial.
	 * @author samuel
	 */
	public static final class Double implements Funcion.Double {
		double A, B, C, D, gamma;

		/**
		 * Crea la función exponencial correspondiente a <code>f(x) = A*Math.pow((C*x + D), gamma) + B</code>
		 */
		public Double(double A ,double B, double C, double D, double gamma){
			this.A = A;
			this.B = B;
			this.C = C;
			this.D = D;
			this.gamma = gamma;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f(double)
		 */
		public double f(double x) {
			return A * Math.pow( C * x + D, gamma) + B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f1(double)
		 */
		public double f1(double x) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#f2(double)
		 */
		public double f2(double x) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleIn(double)
		 */
		public void scaleIn(double scale) {
			/*
			 *  A(C*( x * K ) + D)^gamma + B = A(CK*x + D)^gamma + B
			 */
			C*= scale;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#scaleOut(double)
		 */
		public void scaleOut(double scale) {
			/*
			 *  ( A(Cx + D)^gamma + B ) * K = AK(Cx + D)^gamma + BK
			 */
			A*= scale;
			B*= scale;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#translateIn(double)
		 */
		public void translateIn(double translation) {
			/*
			 *  A( C( x + K ) + D)^gamma + B = A(Cx + (CK + D) )^gamma + B
			 */
			D += C * translation;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#translateOut(double)
		 */
		public void translateOut(double translation) {
			/*
			 *  ( A(Cx + D)^gamma + B ) + K = A(Cx + D)^gamma + (B+K)
			 */
			B += translation;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toFloat()
		 */
		public Funcion.Float toFloat() {
			return new Exponencial.Float(A,B,C,D,gamma);
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Double#toInteger()
		 */
		public Funcion.Integer toInteger() {
			return new Exponencial.Integer(A,B,C,D,gamma);
		}
	}

	/**
	 * Implementación con precisión Float de la función exponencial.
	 * @author samuel
	 */
	public static final class Float implements Funcion.Float {

		float A, B, C, D, gamma;

		/**
		 * Crea la función exponencial correspondiente a <code>f(x) = A*Math.pow((C*x + D), gamma) + B</code>
		 */
		public Float(double A ,double B, double C, double D, double gamma){
			this.A = (float)A;
			this.B = (float)B;
			this.C = (float)C;
			this.D = (float)D;
			this.gamma = (float)gamma;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f(float)
		 */
		public float f(float x) {
			return A * (float)Math.pow( C * x + D, gamma) + B;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f1(float)
		 */
		public float f1(float x) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Float#f2(float)
		 */
		public float f2(float x) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Implementación con precisión Integer de la función exponencial.
	 * @author samuel
	 */
	public static final class Integer implements Funcion.Integer {
		//TODO Implementar
		/**
		 * Crea la función exponencial correspondiente a <code>f(x) = A*Math.pow((C*x + D), gamma) + B</code>
		 */
		public Integer(double A ,double B, double C, double D, double gamma){
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f(int)
		 */
		public int f(int x) {
			return 0;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f1(int)
		 */
		public int f1(int x) {
			throw new UnsupportedOperationException();
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Funcion.Integer#f2(int)
		 */
		public int f2(int x) {
			throw new UnsupportedOperationException();
		}
	}
}

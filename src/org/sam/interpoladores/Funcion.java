package org.sam.interpoladores;

/**
 * Este interface representa una función de la cual se podrán obtener:
 * los valores de la funcion f(x)
 * de la tangencia, derivada f'(x)
 * y de la curvatura, segunda derivada f"(x)
 */
public final class Funcion {
	private Funcion(){};

	public interface Double {
		/**
		 * Funcion f(x)
		 *
		 * @param x valor del que se calcula la función.
		 *
		 * @return devuelve el valor correspondiente a x en la función.
		 */
		public double f(double x);

		/**
		 * Tangencia en la funcion f(x) = f'(x)dx
		 *
		 * @param x valor del que se calcula la tangencia
		 *
		 * @return devuelve la tangencia correspondiente a x.
		 */
		public double f1(double x);

		/**
		 * Curvatura en la funcion f(x) = f"(x)dx
		 *
		 * @param x valor del que se calcula la curvatura
		 *
		 * @return devuelve la curvatura correspondiente a x.
		 */
		public double f2(double x);

		public void scaleIn(double scale);

		public void scaleOut(double scale);

		public void translateIn(double translation);

		public void translateOut(double translation);

		public Funcion.Integer toInteger();

		public Funcion.Float toFloat();
	}

	public interface Float {
		/**
		 * Funcion f(x)
		 *
		 * @param x valor del que se calcula la función.
		 *
		 * @return devuelve el valor correspondiente a x en la función.
		 */
		public float f(float x);

		/**
		 * Tangencia en la funcion f(x) = f'(x)dx
		 *
		 * @param x valor del que se calcula la tangencia
		 *
		 * @return devuelve la tangencia correspondiente a x.
		 */
		public float f1(float x);

		/**
		 * Curvatura en la funcion f(x) = f"(x)dx
		 *
		 * @param x valor del que se calcula la curvatura
		 *
		 * @return devuelve la curvatura correspondiente a x.
		 */
		public float f2(float x);
	}

	public interface Integer {
		/**
		 * Funcion f(x)
		 *
		 * @param x valor del que se calcula la función.
		 *
		 * @return devuelve el valor correspondiente a x en la función.
		 */
		public int f(int x);

		/**
		 * Tangencia en la funcion f(x) = f'(x)dx
		 *
		 * @param x valor del que se calcula la tangencia
		 *
		 * @return devuelve la tangencia correspondiente a x.
		 */
		public int f1(int x);

		/**
		 * Curvatura en la funcion f(x) = f"(x)dx
		 *
		 * @param x valor del que se calcula la curvatura
		 *
		 * @return devuelve la curvatura correspondiente a x.
		 */
		public int f2(int x);
	}
	
	public static Funcion.Float[] toFloat(Funcion.Double[] funciones) {
		Funcion.Float[] funcionesFloat = new Funcion.Float[funciones.length];
		for(int i = 0; i < funciones.length; i++)
			funcionesFloat[i]  = funciones[i].toFloat();
		return funcionesFloat;
	}

	public static Funcion.Integer[] toInteger(Funcion.Double[] funciones) {
		Funcion.Integer[] funcionesInt = new Funcion.Integer[funciones.length];
		for(int i = 0; i < funciones.length; i++)
			funcionesInt[i]  = funciones[i].toInteger();
		return funcionesInt;
	}

}
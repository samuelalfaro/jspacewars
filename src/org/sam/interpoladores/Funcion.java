package org.sam.interpoladores;

/**
 * Este interface representa una función de la cual se podrán obtener:<ul>
 * <li>los valores de la funcion f(x)
 * <li>de la tangencia, derivada f'(x)
 * <li>y de la curvatura, segunda derivada f"(x)</ul>
 */
public final class Funcion {
	private Funcion(){}

    /**
     *
     */
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

        /**
         *
         * @param scale
         */
        public void scaleIn(double scale);

        /**
         *
         * @param scale
         */
        public void scaleOut(double scale);

        /**
         *
         * @param translation
         */
        public void translateIn(double translation);

        /**
         *
         * @param translation
         */
        public void translateOut(double translation);

        /**
		 * 
		 * @return
		 */
		public Funcion.Float toFloatFunction();

		/**
         *
         * @return
         */
        public Funcion.Integer toIntegerFunction();
	}

    /**
     *
     */
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

    /**
     *
     */
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
	
    /**
     *
     * @param funciones
     * @return
     */
    public static Funcion.Float[] toFloatFunctions(Funcion.Double[] funciones) {
		Funcion.Float[] funcionesFloat = new Funcion.Float[funciones.length];
		for(int i = 0; i < funciones.length; i++)
			funcionesFloat[i]  = funciones[i].toFloatFunction();
		return funcionesFloat;
	}

    /**
     *
     * @param funciones
     * @return
     */
    public static Funcion.Integer[] toIntegerFunctions(Funcion.Double[] funciones) {
		Funcion.Integer[] funcionesInt = new Funcion.Integer[funciones.length];
		for(int i = 0; i < funciones.length; i++)
			funcionesInt[i]  = funciones[i].toIntegerFunction();
		return funcionesInt;
	}

}
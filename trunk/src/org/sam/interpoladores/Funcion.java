package org.sam.interpoladores;

/**
 * Clase contenedora que provee los interfaces que representan una función,
 * tanto en precisión {@code double}, como {@code float}.
 * <p>La reprensentación de estas funciones proporciona los métodos para obtener:<ul>
 * <li>los valores de la funcion f(x)
 * <li>de la tangencia, derivada f'(x)
 * <li>y de la curvatura, segunda derivada f"(x)</ul></p>
 */
public final class Funcion {
	private Funcion(){}

    /**
     * Interface que representa una función con precisión {@code double}.
     */
    public interface Double {
		/**
		 * Funcion f(x)
		 *
		 * @param x valor del que se calcula la función.
		 *
		 * @return el valor correspondiente a x en la función.
		 */
		public double f(double x);

		/**
		 * Tangencia en la funcion f(x) = f'(x)dx
		 *
		 * @param x valor del que se calcula la tangencia
		 *
		 * @return la tangencia correspondiente a x.
		 */
		public double f1(double x);

		/**
		 * Curvatura en la funcion f(x) = f"(x)dx
		 *
		 * @param x valor del que se calcula la curvatura
		 *
		 * @return la curvatura correspondiente a x.
		 */
		public double f2(double x);

        /**
         * Método que modifica la función de tal forma que se escale su entrada.
         * <p><pre><i>
         * Sean la funciones f y g tal que
         *     g = f.scaleIn(k)  =>
         *     g(x) = f(k·x)
         * </i></pre></p>
         * @param scale valor de escala de la entrada.
         */
        public void scaleIn(double scale);

        /**
         * Método que modifica la función de tal forma que se escale su salida.
         * <p><pre><i>
         * Sean la funciones f y g tal que
         *     g = f.scaleOut(k)  =>
         *     g(x) = k·f(x)
         * </i></pre></p>
         * @param scale valor de escala de la salida.
         */
        public void scaleOut(double scale);

        /**
         * Método que modifica la función de tal forma que se desplace su entrada.
         * <p><pre><i>
         * Sean la funciones f y g tal que
         *     g = translateIn(k)  =>
         *     g(x) = f(x+k)
         * </i></pre></p>
         * @param translation valor de desplazamiento de la entrada.
         */
        public void translateIn(double translation);

        /**
         * Método que modifica la función de tal forma que se desplace su salida.
         * <p><pre><i>
         * Sean la funciones f y g tal que
         *     g = translateOut(k)  =>
         *     g(x) = f(x)+k
         * </i></pre></p>
         * @param translation valor de desplazamiento de la salida.
         */
        public void translateOut(double translation);

        /**
         * Método que genera una nueva {@linkplain Funcion.Float función con precisión float} equivalente a esta.
         * @return  la {@linkplain Funcion.Float función con precisión float} generada.
         */
		public Funcion.Float toFloatFunction();

	}

    /**
     * Interface que representa una función con precisión {@code float}.
     */
    public interface Float {
		/**
		 * Funcion f(x)
		 *
		 * @param x valor del que se calcula la función.
		 *
		 * @return el valor correspondiente a x en la función.
		 */
		public float f(float x);

		/**
		 * Tangencia en la funcion f(x) = f'(x)dx
		 *
		 * @param x valor del que se calcula la tangencia
		 *
		 * @return la tangencia correspondiente a x.
		 */
		public float f1(float x);

		/**
		 * Curvatura en la funcion f(x) = f"(x)dx
		 *
		 * @param x valor del que se calcula la curvatura
		 *
		 * @return la curvatura correspondiente a x.
		 */
		public float f2(float x);
	}

	/**
	 * Método estático que ajusta un vector bidimensional de {@linkplain Funcion.Double funciones con precisión double}, a las claves
	 * que recibe como parámetro.
	 * <p>
	 * Este método se emplea para poder hacer que las claves sean propocionales al tamaño de la curva.<br/>
	 * Esto se hace de la siguiente forma:<ul>
	 * <li>Se calculan todas las funciones tomando como valor inicial de entrada {@code 0} y como valor final de entrada
	 * {@code 1}.</li>
	 * <li>A partir de estas funciones generadas, se calcula la longitud de la curva para cada tramo de funciones, y se
	 * generan las claves, proporcionales a la longitud de estos tramos.</li>
	 * <li>Por último, se ajustan las funciones de cada tramo, para que sus valore inciales y finales de entrada, sean
	 * las claves previamente generadas.</li></ul>
	 * </p>
	 * 
	 * @param keys
	 *            El vector de claves a las que ajustar las funciones.
	 * @param funciones
	 *            El vector de funciones a ajustar.
	 */
	static void ajustarFunciones(double[] keys, Funcion.Double[][] funciones) {
		assert (keys.length == funciones[0].length+1);
		for (int i = 0; i < funciones[0].length; i++) {
			double s = 1 / (keys[i + 1] - keys[i]);
			double t = -keys[i];
			for (int j = 0; j < funciones.length; j++) {
				funciones[j][i].scaleIn(s);
				funciones[j][i].translateIn(t);
			}
		}
	}
    
    /**
     * Método estático que convierte un vector de {@linkplain Funcion.Double funciones con precisión double},
     * en un vector {@linkplain Funcion.Float funciones con precisión float}.
     *
     * @param funciones El vector de {@linkplain Funcion.Double funciones con precisión double} a convertir.
     * @return  El vector de {@linkplain Funcion.Float funciones con precisión float} generado.
     */
    static Funcion.Float[] toFloatFunctions(Funcion.Double[] funciones) {
		Funcion.Float[] funcionesFloat = new Funcion.Float[funciones.length];
		for(int i = 0; i < funciones.length; i++)
			funcionesFloat[i]  = funciones[i].toFloatFunction();
		return funcionesFloat;
	}
}
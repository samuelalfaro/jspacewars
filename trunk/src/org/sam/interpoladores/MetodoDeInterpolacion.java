package org.sam.interpoladores;

/**
 * Interface que representa un método de interpolación.<br/>
 * Proporciona métodos para generar las funciones de interpolación a partir de las claves ({@code keys }),
 * los valores ({@code values }) y los parámetros ({@code parametros }) necesarios.
 * 
 */
public interface MetodoDeInterpolacion{

    /**
     * Enumeración que contiene {@linkplain MetodoDeInterpolacion métodos de interpolación} predefinidos.
     */
    public enum Predefinido implements MetodoDeInterpolacion {
        /**
         * Este miembro de la enumeración, no es un método de interpolación propiamente dicho,
         * pues no se generan funciones. Se emplea para definir una búsqueda por aproximación
         * que devuelve el valor más cercano a partir de las claves y valores proporcionados .
         */
        ESCALON {
        	/**
        	 * Operación no soportada.
        	 * @throws UnsupportedOperationException puesto que no se generan funciones.
        	 */
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				throw new UnsupportedOperationException();
			}
        	/**
        	 * Operación no soportada.
        	 * @throws UnsupportedOperationException puesto que no se generan funciones.
        	 */
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				throw new UnsupportedOperationException();
			}
		},
        /**
         * Miembro que genera polinomios lineales a partir de los valores dados.
         */
        LINEAL {
			/**
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				int len = values.rows() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.columns()][len];

				for(int i=0; i< values.columns(); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = GeneradorDeFunciones.Predefinido.LINEAL.generaFuncion(
								0.0, values.at( j, i),
								1.0, values.at( j + 1, i)
						);
				return funciones;
			}
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				int len = keys.rows() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.columns()][len];

				for(int i=0; i< values.columns(); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = GeneradorDeFunciones.Predefinido.LINEAL.generaFuncion(
								keys.at(j), values.at( j, i),
								keys.at(j + 1), values.at( j + 1, i)
						);
				return funciones;
			}
		},
		/**
		 * Miembro que genera funciones exponenciales a partir de los valores dados.
		 */
		EXPONENCIAL_PUNTO_MEDIO {
			/**
			 * @param values {@inheritDoc}
			 * @param parametros Contiene los puntos medios entre los distintos valores.<br/>
			 * El valor de estos parámetros debe estar comprendido entre {@code (0 .. 1)},
			 * comportandose como una interpolación lineal cuando el punto medio corresponde a {@code 0.5}.<br/>
			 * Debe haber tantos puntos medios como  {@code values.length() - 1}.
			 * En caso contrario se producirá una excepción. 
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				int len = values.rows() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.columns()][len];

				for(int i=0; i< values.columns(); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = GeneradorDeFunciones.Predefinido.EXPONENCIAL_PUNTO_MEDIO.generaFuncion(
								0.0, values.at( j, i),
								1.0, values.at( j + 1, i),
								((Number)parametros[j]).doubleValue()
						);
				return funciones;
			}
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Contiene los puntos medios entre los distintos valores.<br/>
			 * El valor de estos parámetros debe estar comprendido entre {@code (0 .. 1)},
			 * comportandose como una interpolación lineal cuando el punto medio corresponde a {@code 0.5}.<br/>
			 * Debe haber tantos puntos medios como  {@code values.length() - 1}.
			 * En caso contrario se producirá una excepción. 
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				int len = keys.rows() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.columns()][len];

				for(int i=0; i< values.columns(); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = GeneradorDeFunciones.Predefinido.EXPONENCIAL_PUNTO_MEDIO.generaFuncion(
								keys.at(j), values.at( j, i),
								keys.at(j + 1), values.at( j + 1, i),
								((Number)parametros[j]).doubleValue()
						);
				return funciones;
			}
		},
		/**
		 * Miembro que genera cocientes polinómicos a partir de los valores dados.
		 */
		COCIENTE_POLINOMICO_PUNTO_MEDIO {
			/**
			 * @param values {@inheritDoc}
			 * @param parametros Contiene los puntos medios entre los distintos valores.<br/>
			 * El valor de estos parámetros debe estar comprendido entre {@code (0 .. 1)},
			 * comportandose como una interpolación lineal cuando el punto medio corresponde a {@code 0.5}.<br/>
			 * Debe haber tantos puntos medios como  {@code values.length() - 1}.
			 * En caso contrario se producirá una excepción. 
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				int len = values.rows() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.columns()][len];

				for(int i=0; i< values.columns(); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = GeneradorDeFunciones.Predefinido.COCIENTE_POLINOMICO_PUNTO_MEDIO.generaFuncion(
								0.0, values.at( j, i),
								1.0, values.at( j + 1, i),
								((Number)parametros[j]).doubleValue()
						);
				return funciones;
			}
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Contiene los puntos medios entre los distintos valores.<br/>
			 * El valor de estos parámetros debe estar comprendido entre {@code (0 .. 1)},
			 * comportandose como una interpolación lineal cuando el punto medio corresponde a {@code 0.5}.<br/>
			 * Debe haber tantos puntos medios como  {@code values.length() - 1}.
			 * En caso contrario se producirá una excepción. 
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				int len = keys.rows() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.columns()][len];

				for(int i=0; i< values.columns(); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = GeneradorDeFunciones.Predefinido.COCIENTE_POLINOMICO_PUNTO_MEDIO.generaFuncion(
								keys.at(j), values.at( j, i),
								keys.at(j + 1), values.at( j + 1, i),
								((Number)parametros[j]).doubleValue()
						);
				return funciones;
			}
		},
        /**
         *Consulte <a href="http://en.wikipedia.org/wiki/Cubic_Hermite_spline#Finite_difference">wikipedia</a>.
         */
        FINITE_DIFFERENCE_SPLINE {
        	/**
        	 * Operación no soportada.<br/>
        	 * Esta método de interpolación genera las funciones tomando los valores de las claves, por tanto, si se
        	 * quieren generar las claves posteriormente es recomendable usar otros métodos, como pueden ser:
        	 * <ul><li>{@link MetodoDeInterpolacion.Predefinido#CARDINAL_SPLINE CARDINAL_SPLINE}</li>
        	 * <li>{@link MetodoDeInterpolacion.Predefinido#CATMULL_ROM_SPLINE CATMULL_ROM_SPLINE}</li></ul>
        	 * 
        	 * @throws UnsupportedOperationException
        	 */
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				throw new UnsupportedOperationException(); 
			}
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				int len = keys.rows() - 1; 
				Funcion.Double funciones[][] = new Funcion.Double[values.columns()][len];

				double a, fa , b, fb, f1a, f1b, t;

				for(int i=0; i < len; i++ ){
					a = keys.at(i);
					b = keys.at(i+1);

					for(int j=0; j < values.columns();j++){
						fa = values.at( i, j);
						fb = values.at( i+1, j);

						t = (fb - fa) / (b - a);

						if (i == 0)
							f1a = t;
						else
							f1a = (t + (fa - values.at( i - 1, j)) / (a - keys.at(i - 1))) / 2;

						if (i == len - 1)
							f1b = t;
						else
							f1b = (t + (values.at( i + 2, j) - fb) / (keys.at(i + 2) - b)) / 2;

						funciones[j][i] = GeneradorDeFunciones.Predefinido.HERMITE.generaFuncion(
								a,fa,f1a,
								b,fb,f1b
						);
					}
				}
				return funciones;
			}
		},
        /**
         *Consulte <a href="http://en.wikipedia.org/wiki/Cubic_Hermite_spline#Cardinal_spline">wikipedia</a>.
         */
        CARDINAL_SPLINE {
			/**
			 * @param values {@inheritDoc}
			 * @param parametros Tensión: valor comprendido entre 0 y 1 que se resta a 1 para multiplicar las tangentes.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				double tension = (1.0 - ((Number)parametros[0]).doubleValue())/ 2.0;
				int len = values.rows() - 1; 
				Funcion.Double funciones[][] = new Funcion.Double[values.columns()][len];

				double fa, f1a, fb, f1b;

				for(int i=0; i < len; i++ ){
					for(int j=0; j < values.columns();j++){
						fa = values.at( i, j );
						fb = values.at( i+1, j );

						if (i == 0)
							f1a = fb - fa;
						else
							f1a = fb - values.at( i-1, j );

						if (i == len - 1)
							f1b = fb - fa;
						else
							f1b = values.at( i+2, j ) - fa;

						funciones[j][i] = GeneradorDeFunciones.Predefinido.HERMITE_UNITARIO.generaFuncion(
								fa, f1a * tension,
								fb, f1b * tension
						);
					}
				}
				return funciones;
			}
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Tensión: valor comprendido entre 0 y 1 que se resta a 1 para multiplicar las tangentes.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				double tension = (1.0 - ((Number)parametros[0]).doubleValue())/ 2.0;
				int len = keys.rows() - 1; 
				Funcion.Double funciones[][] = new Funcion.Double[values.columns()][len];

				double fa, f1a, fb, f1b, s, t;

				for(int i=0; i < len; i++ ){
					s = 1 / (keys.at(i+1) - keys.at(i));
					t = -keys.at(i);
					for(int j=0; j < values.columns();j++){
						fa = values.at( i, j );
						fb = values.at( i+1, j );

						if (i == 0)
							f1a = fb - fa;
						else
							f1a = fb - values.at( i-1, j );

						if (i == len - 1)
							f1b = fb - fa;
						else
							f1b = values.at( i+2, j ) - fa;

						funciones[j][i] = GeneradorDeFunciones.Predefinido.HERMITE_UNITARIO.generaFuncion(
								fa, f1a * tension,
								fb, f1b * tension
						);
						funciones[j][i].scaleIn(s);
						funciones[j][i].translateIn(t);
					}
				}
				return funciones;
			}
		},
        /**
         * Caso especial de {@link MetodoDeInterpolacion.Predefinido#CARDINAL_SPLINE CARDINAL_SPLINE} con tensión 1.
         */
        COSENOIDAL {
			/**
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(values, 1.0);
			}		
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(keys, values, 1.0);
			}
		},
        /**
         * Caso especial de {@link MetodoDeInterpolacion.Predefinido#CARDINAL_SPLINE CARDINAL_SPLINE} con tensión 0.<br/>
         * Puede encontrar más información consultando <a href="http://en.wikipedia.org/wiki/Cubic_Hermite_spline#Catmull.E2.80.93Rom_spline">wikipedia</a>.
         */
		CATMULL_ROM_SPLINE {
			/**
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones(ArrayExtractor values, Object... parametros) {
				return CARDINAL_SPLINE.generarFunciones(values, 0.0);
			}
			/**
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 * @see 
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(keys, values, 0.0);
			}
		},
        /**
         *Consulte <a href="http://en.wikipedia.org/wiki/Bézier_curve">wikipedia</a>.
         */
        BEZIER {
			/**
			 * {@inheritDoc}<br/>
			 * Implementacion del método que genera polinomios de tercer grado 
			 * {@link GeneradorDeFunciones.Predefinido#BEZIER_CUBICO BEZIER_CUBICO}.
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				assert(values.rows() / 3 > 0 && values.rows() % 3 == 1);

				int len = values.rows() / 3;
				Funcion.Double funciones[][] = new Funcion.Double[values.columns()][len];

				for (int i = 0; i < values.columns(); i++) {
					for(int j1=0, j2=0; j1 < len; j1++ ){
						funciones[i][j1] = GeneradorDeFunciones.Predefinido.BEZIER_CUBICO.generaFuncion(
								values.at( j2++, i),
								values.at( j2++, i),
								values.at( j2++, i),
								values.at( j2, i)
						);
					}
				}
				return funciones;
			}
			/**
			 * {@inheritDoc}<br/>
			 * Implementacion del método que genera polinomios de tercer grado 
			 * {@link GeneradorDeFunciones.Predefinido#BEZIER_CUBICO BEZIER_CUBICO}.
			 * @param keys {@inheritDoc}
			 * @param values {@inheritDoc}
			 * @param parametros Ignorados.
			 * @return {@inheritDoc}
			 */
			@Override
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				assert(keys.rows() > 1 && values.rows() / 3 == keys.rows() -1 && values.rows() % 3 == 1);

				int len = keys.rows() - 1;
				Funcion.Double funciones[][] = new Funcion.Double[values.columns()][len];

				for (int i = 0; i < values.columns(); i++) {
					for(int j1=0, j2=0; j1 < len; j1++ ){
						funciones[i][j1] = GeneradorDeFunciones.Predefinido.BEZIER_CUBICO.generaFuncion(
								values.at( j2++, i),
								values.at( j2++, i),
								values.at( j2++, i),
								values.at( j2, i)
						);
						funciones[i][j1].scaleIn( 1 / (keys.at(j1+1) - keys.at(j1)) );
						funciones[i][j1].translateIn(-keys.at(j1));
					}
				}
				return funciones;
			}
		}
	}
	
	/**
	 * Método que genera las funciones necesarias para interpolar los valores dados. Cada una de 
	 * las funciones devueltas estará ajustada entre 0 y 1, pudiendo ajustarlas posteriormente a 
	 * claves generadas automáticamente, tanto homogéneas como proporcionales.
	 * 
	 * @param values Valores a interpolar.
	 * @param parametros Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 * @return Las funciones generadas.
	 */
	public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros);
	
	/**
	 * Método que genera las funciones necesarias para interpolar los valores dados,
	 * entre los intervalos definidos por las claves.
	 * 
	 * @param keys Claves que definen los intervalos entre los que se interpoalan dos determinados valores.
	 * @param values Valores a interpolar.
	 * @param parametros Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 * @return Las funciones generadas.
	 */
	public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros);
	
}
package org.sam.interpoladores;

/**
 * @author Samuel
 *
 */
public interface MetodoDeInterpolacion{

    /**
     *
     */
    public enum Predefinido implements MetodoDeInterpolacion {
        /**
         *
         */
        ESCALON {
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				throw new UnsupportedOperationException();
			}

			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				throw new UnsupportedOperationException();
			}
		},
        /**
         *
         */
        LINEAL {
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				int len = values.length() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.LINEAL;

				for(int i=0; i< values.length(0); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = gdf.generaFuncion(
								0.0, values.at( j, i),
								1.0, values.at( j + 1, i)
						);
				return funciones;
			}
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				int len = keys.length() - 1;
				Funcion.Double[][] funciones = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.LINEAL;

				for(int i=0; i< values.length(0); i++)
					for(int j=0; j<len; j++)
						funciones[i][j] = gdf.generaFuncion(
								keys.at(j), values.at( j, i),
								keys.at(j + 1), values.at( j + 1, i)
						);
				return funciones;
			}
		},
        /**
         *
         */
        FINITE_DIFFERENCE_SPLINE {
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				throw new UnsupportedOperationException(); 
			}
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				int len = keys.length() - 1; 
				Funcion.Double funciones[][] = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.HERMITE;

				double a, fa , b, fb, f1a, f1b, t;

				for(int i=0; i < len; i++ ){
					a = keys.at(i);
					b = keys.at(i+1);

					for(int j=0; j < values.length(0);j++){
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

						funciones[j][i] = gdf.generaFuncion(a,fa,f1a,b,fb,f1b);
					}
				}
				return funciones;
			}
		},
        /**
         *
         */
        CARDINAL_SPLINE {
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				double tension = (1.0 - ((Number)parametros[0]).doubleValue())/ 2.0;
				int len = values.length() - 1; 
				Funcion.Double funciones[][] = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.HERMITE_UNITARIO;

				double fa, f1a, fb, f1b;

				for(int i=0; i < len; i++ ){
					for(int j=0; j < values.length(0);j++){
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

						funciones[j][i] = gdf.generaFuncion(fa, f1a * tension, fb, f1b * tension);
					}
				}
				return funciones;
			}
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				double tension = (1.0 - ((Number)parametros[0]).doubleValue())/ 2.0;
				int len = keys.length() - 1; 
				Funcion.Double funciones[][] = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.HERMITE_UNITARIO;

				double fa, f1a, fb, f1b, s, t;

				for(int i=0; i < len; i++ ){
					s = 1 / (keys.at(i+1) - keys.at(i));
					t = -keys.at(i);
					for(int j=0; j < values.length(0);j++){
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

						funciones[j][i] = gdf.generaFuncion(fa, f1a * tension, fb, f1b * tension);
						funciones[j][i].scaleIn(s);
						funciones[j][i].translateIn(t);
					}
				}
				return funciones;
			}
		},
        /**
         *
         */
        COSENOIDAL {
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(values, 1.0);
			}
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(keys, values, 1.0);
			}
		},
        /**
         * 
         */
        CATMULL_ROM_SPLINE {

			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(values, 0.0);
			}

			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				return CARDINAL_SPLINE.generarFunciones(keys, values, 0.0);
			}
		},
        /**
         *
         */
        BEZIER {
			public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros){
				assert(values.length() / 3 > 0 && values.length() % 3 == 1);

				int len = values.length() / 3;
				Funcion.Double funciones[][] = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.BEZIER_CUBICO;

				for (int i = 0; i < values.length(0); i++) {
					for(int j1=0, j2=0; j1 < len; j1++ ){
						funciones[i][j1] = gdf.generaFuncion(
								values.at( j2++, i),
								values.at( j2++, i),
								values.at( j2++, i),
								values.at( j2, i)
						);
					}
				}
				return funciones;
			}
			public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros){
				assert(keys.length() > 1 && values.length() / 3 == keys.length() -1 && values.length() % 3 == 1);

				int len = keys.length() - 1;
				Funcion.Double funciones[][] = new Funcion.Double[values.length(0)][len];
				GeneradorDeFunciones gdf = GeneradorDeFunciones.Predefinido.BEZIER_CUBICO;

				for (int i = 0; i < values.length(0); i++) {
					for(int j1=0, j2=0; j1 < len; j1++ ){
						funciones[i][j1] = gdf.generaFuncion(
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
	 * @param values
	 * @param parametros
	 * @return
	 */
	public Funcion.Double[][] generarFunciones( ArrayExtractor values, Object... parametros);
	
	/**
	 * @param keys
	 * @param values
	 * @param parametros
	 * @return
	 */
	public Funcion.Double[][] generarFunciones( ArrayExtractor keys, ArrayExtractor values, Object... parametros);
	
}

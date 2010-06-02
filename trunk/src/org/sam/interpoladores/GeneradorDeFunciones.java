package org.sam.interpoladores;

/**
 * Este interface representa generador de funciones 
 */
public interface GeneradorDeFunciones {

    /**
     *
     */
    public enum Predefinido implements GeneradorDeFunciones{
	
        /**
         *
         */
        LINEAL {
			/**
			 * Crea y calcula el polinomio lineal que pasa por los puntos (a,fa) (b,fb).<br>
			 * @return La función generada.
			 */
        	public Funcion.Double generaFuncion(double a, double fa, double b, double fb){
				double A = (fb - fa)/(b - a);
				double B = fa - A*a;
				return new PolinomioLineal.Double(A,B);
        	}
        	
        	/**
			 * Crea y calcula el polinomio lineal que pasa por los puntos (a,fa) (b,fb).<br>
			 * @param p {@inheritDoc} Siendo:<ul>
			 * <li>p[0] = <b>a</b></li>
			 * <li>p[1] = <b>fa</b></li>
			 * <li>p[2] = <b>b</b></li>
			 * <li>p[3] = <b>fb</b></li></ul>
			 * @return {@inheritDoc}
			 * @throws IllegalArgumentException {@inheritDoc}
			 */
			@Override
			public Funcion.Double generaFuncion(double... p) throws IllegalArgumentException {
				try{
					return generaFuncion(p[0], p[1], p[2], p[3]);
				}catch(ArrayIndexOutOfBoundsException faltanParametros){
					throw INSUFICIENTES_PARAMETROS;
				}
			}
		},
	
        /**
         *
         */
        EXPONENCIAL_PUNTO_MEDIO {
			/**
			 * Calcula los valores de la función exponencial que pasa por los puntos (a,fa) (b,fb)
			 * y cuyo punto medio es pm.<br>
			 * @return La funcion generada.
			 */
			public Funcion.Double generaFuncion(double a, double fa, double b, double fb, double pm){

				double A,B,C,D,gamma;
				// Calculamos la funcion para f(a) = fa, f(b) = fb y pm
				if(pm <= .5){
					gamma = Math.log(0.5)/Math.log(pm);
					A = fb - fa;
					B = fa;
					C = 1.0 /(b - a);
					D = -a * C;
				}else{
					gamma = Math.log(0.5)/Math.log(1.0 - pm);
					A = fa - fb;
					B = fb;
					C = 1.0 /(a - b);
					D = -b*C;
				}

				return new Exponencial.Double(A,B,C,D,gamma);
			}
			
			/**
			 * Calcula los valores de la función exponencial que pasa por los puntos (a,fa) (b,fb)
			 * y cuyo punto medio es pm.<br>
			 * @param p {@inheritDoc} Siendo:<ul>
			 * <li>p[0] = <b>a</b></li>
			 * <li>p[1] = <b>fa</b></li>
			 * <li>p[2] = <b>b</b></li>
			 * <li>p[3] = <b>fb</b></li>
			 * <li>p[4] = <b>pm</b></li></ul>
			 * @return {@inheritDoc}
			 * @throws IllegalArgumentException {@inheritDoc}
			 */
			@Override
			public Funcion.Double generaFuncion(double... p) throws IllegalArgumentException {
				try{
					return generaFuncion(p[0],p[1],p[2],p[3],p[4]);
				}catch(ArrayIndexOutOfBoundsException faltanParametros){
					throw INSUFICIENTES_PARAMETROS;
				}
			}
		},

        /**
         *
         */
        COCIENTE_POLINOMICO_PUNTO_MEDIO {
			/**
			 * Calcula los valores del cociente polinómico que pasa por los puntos (a,fa) (b,fb)
			 * y cuyo punto medio es pm.<br>
			 * @return La funcion generada.
			 */
			public Funcion.Double generaFuncion(double a, double fa, double b, double fb, double pm){
				double A,B,C,D;

				// Calculamos la funcion considerando a = 0 y b = 1 --> f(0) = fa, f(1) = fb
				C = (1 - 2*pm) / (1.0-pm);
				D = pm / (1.0-pm);
				A = (fb-fa) + fa*C; 
				B = fa*D;

				// Hacemos un cambio de variable u = (J*x + K) para
				// ajustar la funcion al intervalo f(a) = fa, f(b) = fb
				double J = 1.0 /(b - a);
				double K = -a*J;

				B += A*K;
				D += C*K;
				A *= J;
				C *= J;

				return new CocientePolinomico.Double(A,B,C,D);
			}
			
			/**
			 * Calcula los valores del cociente polinómico que pasa por los puntos (a,fa) (b,fb) y cuyo punto medio es pm.<br>
			 * @param p {@inheritDoc} Siendo:<ul>
			 * <li>p[0] = <b>a</b></li>
			 * <li>p[1] = <b>fa</b></li>
			 * <li>p[2] = <b>b</b></li>
			 * <li>p[3] = <b>fb</b></li>
			 * <li>p[4] = <b>pm</b></li></ul>
			 * @return {@inheritDoc}
			 * @throws IllegalArgumentException {@inheritDoc}
			 */
			@Override
			public Funcion.Double generaFuncion(double... p) throws IllegalArgumentException {
				try{
					return generaFuncion(p[0],p[1],p[2],p[3],p[4]);
				}catch(ArrayIndexOutOfBoundsException faltanParametros){
					throw INSUFICIENTES_PARAMETROS;
				}
			}
		},

        /**
         *
         */
        HERMITE{
			/**
			 * Crea y calcula el polinomio de tercer grado que pasa por los puntos (a,fa) (b,fb) 
			 * con las tangencias  f1a en a y  f1b en b.<br>
			 * @return La funcion generada.
			 */
			public Funcion.Double generaFuncion( double a, double fa, double f1a, double b, double fb, double f1b){
				double A, B, C, D;

				/*
				 * Se calcula la funcion considerando a = 0 y b = 1 --> f(0) = fa, f(1) = fb
				 * Al escalar la funcion posteriormente para que f(a) = fa y f(b) = fb tambien se
				 * escalaran los valores de la tangentes, por tanto se modifican para que conserven
				 * su valor tras el escalado.
				 */
				f1a *= (b-a);
				f1b *= (b-a);

				A = 2*(fa - fb) + f1a + f1b;
				B = 3*(fb - fa) - 2*f1a - f1b;
				C = f1a;
				D = fa;

				// Se hace un cambio de variable u = (J*x + K) para
				// ajustar la funcion al intervalo f(a) = fa, f(b) = fb
				double J = 1.0 /(b - a);
				double K = -a*J;

				//f'(x) = 3Ax² + 2Bx + C;

				D = ((A*K + B)*K + C)*K + D;
				C = J*(((3*A*K) + 2*B)*K + C);
				B = J*J*(3*A*K + B);
				A = J*J*J*A;

				return new PolinomioCubico.Double(A,B,C,D);
			}
			
			/**
			 * Crea y calcula el polinomio de tercer grado que pasa por los puntos (a,fa) (b,fb) 
			 * con las tangencias  f1a en a y  f1b en b.<br>
			 * @param p {@inheritDoc} Siendo:<ul>
			 * <li>p[0] = <b>a</b></li>
			 * <li>p[1] = <b>fa</b></li>
			 * <li>p[2] = <b>f1a</b></li>
			 * <li>p[3] = <b>b</b></li>
			 * <li>p[4] = <b>fb</b></li>
			 * <li>p[5] = <b>f1b</b></li></ul>
			 * @return {@inheritDoc}
			 * @throws IllegalArgumentException {@inheritDoc}
			 */
			@Override
			public Funcion.Double generaFuncion(double... p) throws IllegalArgumentException {
				try{
					return generaFuncion(p[0],p[1],p[2],p[3],p[4],p[5]);
				}catch(ArrayIndexOutOfBoundsException faltanParametros){
					throw INSUFICIENTES_PARAMETROS;
				}
			}
		},

        /**
         *
         */
        HERMITE_UNITARIO{
			/**
			 * Crea y calcula el polinomio de tercer grado que pasa por los puntos (0,f0) (1,f1) 
			 * con las tangencias  t0 en 0 y  t1 en 1.<br>
			 * @return La funcion generada.
			 */
			public Funcion.Double generaFuncion( double f0, double t0, double f1, double t1){
				return new PolinomioCubico.Double(
						2*(f0 - f1) + t0 + t1,
						3*(f1 - f0) - 2*t0 - t1,
						t0,
						f0
				);
			}
			
			/**
			 * Crea y calcula el polinomio de tercer grado que pasa por los puntos (0,f0) (1,f1) 
			 * con las tangencias  t0 en 0 y  t1 en 1.<br>
			 * @param p {@inheritDoc} Siendo:<ul>
			 * <li>p[0] = <b>f0</b></li>
			 * <li>p[1] = <b>t0</b></li>
			 * <li>p[2] = <b>f1</b></li>
			 * <li>p[3] = <b>t1</b></li></ul>
			 * @return {@inheritDoc}
			 * @throws IllegalArgumentException {@inheritDoc}
			 */
			@Override
			public Funcion.Double generaFuncion(double... p) throws IllegalArgumentException {
				try{
					return generaFuncion(p[0],p[1],p[2],p[3]);
				}catch(ArrayIndexOutOfBoundsException faltanParametros){
					throw INSUFICIENTES_PARAMETROS;
				}
			}
		},

        /**
         * 
         */
        BEZIER_CUBICO{
			/**
			 * Crea y calcula el polinomio de tercer grado correspondiente a la curva de Bezier (p0,p1,p2,p3).<br>
			 * @return La funcion generada.
			 */
			public Funcion.Double generaFuncion(double p0, double p1, double p2, double p3 ){
				return new PolinomioCubico.Double(
						-p0 +3*p1 -3*p2 +p3,
						 3*(p0 -2*p1 +p2),
						 3*(-p0 +p1),
						 p0
				);
			}
			
			/**
			 * Crea y calcula el polinomio de tercer grado correspondiente a la curva de Bezier (p0,p1,p2,p3).<br>
			 * @param p {@inheritDoc} Siendo:<ul>
			 * <li>p[0] = <b>p0</b></li>
			 * <li>p[1] = <b>p1</b></li>
			 * <li>p[2] = <b>p2</b></li>
			 * <li>p[3] = <b>p3</b></li></ul>
			 * @return {@inheritDoc}
			 * @throws IllegalArgumentException {@inheritDoc}
			 */
			@Override
			public Funcion.Double generaFuncion(double... p) throws IllegalArgumentException {
				try{
					return generaFuncion(p[0],p[1],p[2],p[3]);
				}catch(ArrayIndexOutOfBoundsException faltanParametros){
					throw INSUFICIENTES_PARAMETROS;
				}
			}
		};
		
		private final static IllegalArgumentException INSUFICIENTES_PARAMETROS =
			new IllegalArgumentException("Faltan parámetros para poder generar la función");
	}
	
	/**
	 * @param p Parámetros necesarios para calcular la función.
	 * @return La función generada.
	 * @throws IllegalArgumentException Excepcion que se lanza cuando no hay suficientes parámetros para calcular la función.
	 */
	public Funcion.Double generaFuncion(double... p)throws IllegalArgumentException;
}
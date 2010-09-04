package org.sam.interpoladores;

/**
 * Clase optimizada para interpolar valores {@code float} de una dimensión.
 * @see Interpolador
 */
final class Interpolador1F implements Trayectoria.Float<Float> {

	private final transient float keys[];
	private final transient Float valorInicial, valorFinal;
	private final transient Funcion.Float[] funciones;

	/**
	 * Constructor que crea un {@code Interpolador1F}.
	 * @param keys Claves asociadas a los valores.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1F(float keys[], Float[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = Funcion.toFloatFunctions( mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.Numerico<Float>(values), params )[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * Constructor que crea un {@code Interpolador1F}.
	 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
	 * Los valores posibles son:<ul>
	 * <li>{@linkplain Keys#HOMOGENEAS}</li>
	 * <li>{@linkplain Keys#PROPORCIONALES}</li>
	 * </ul>
	 * @param scale Valor de escalado que se aplicará a las claves generadas.
	 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1F(int genKey, float scale, float translation, Float[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Numerico<Float>(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Funcion.ajustarFunciones(keys, funciones);

		this.keys = Keys.toFloat(keys);
		this.funciones = Funcion.toFloatFunctions( funciones[0]);
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * Constructor que crea un {@code Interpolador1F}.
	 * @param keys Claves asociadas a los valores.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1F(float keys[], float[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = Funcion.toFloatFunctions( mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.E1F(values), params )[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * Constructor que crea un {@code Interpolador1F}.
	 * @param genKey Entero que indica como se gerenarán las claves a partir de las funciones obtenidas.<br/>
	 * Los valores posibles son:<ul>
	 * <li>{@linkplain Keys#HOMOGENEAS}</li>
	 * <li>{@linkplain Keys#PROPORCIONALES}</li>
	 * </ul>
	 * @param scale Valor de escalado que se aplicará a las claves generadas.
	 * @param translation Valor de desplazamiento que se aplicará a las claves generadas.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador1F(int genKey, float scale, float translation, float[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Funcion.ajustarFunciones(keys, funciones);

		this.keys = Keys.toFloat(keys);
		this.funciones = Funcion.toFloatFunctions( funciones[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Float get(float key) {
		int index = Keys.findIndexKey(key,keys);
		return (index < 0) ?
			valorInicial :
			(index < funciones.length) ?
				funciones [index].f(key) :
				valorFinal;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float getTan(float key) {
		int index = Keys.findIndexKey(key,keys);
		return (index < 0) ?
			0.0f :
			(index < funciones.length) ?
				funciones [index].f1(key) :
				0.0f;
	}
	
	private transient final Float[] shared = new Float[2];
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Float[] getPosTan(float key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0) {
			shared[0] = valorInicial;
			shared[1] = 0.0f;
		}else if(index < funciones.length){
			shared[0] = funciones [index].f(key);
			shared[1] = funciones [index].f1(key);
		}else{
			shared[0] = valorFinal;
			shared[1] = 0.0f;
		}
		return shared;
	}
}

package org.sam.interpoladores;

/**
 * Clase optimizada para interpolar valores {@code float} de tres dimensiones.
 * @see Interpolador
 */
final class Interpolador3F implements Trayectoria.Float<float[]>{

	private final transient float[] keys;
	private final transient float[][] valorInicial, valorFinal;
	private final transient float[][] compartido;
	private final transient Funcion.Float funcionesX[];
	private final transient Funcion.Float funcionesY[];
	private final transient Funcion.Float funcionesZ[];

	/**
	 * Constructor que crea un {@code Interpolador3F}.
	 * @param keys Claves asociadas a los valores.
	 * @param values Valores a interpolar.
	 * @param mdi {@code MetodoDeInterpolacion} empleado para calcular las funciones.
	 * @param params Parámetros necesarios para el método de interpolación correspondiente, puede estar vacio.
	 */
	Interpolador3F(float keys[], float[][] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.EAF(values), params );
		this.funcionesX = Funcion.toFloatFunctions(funciones[0]);
		this.funcionesY = Funcion.toFloatFunctions(funciones[1]);
		this.funcionesZ = Funcion.toFloatFunctions(funciones[2]);
		this.valorInicial = new float[][]{ values[0].clone(), {0, 0, 0} };
		this.valorFinal = new float[][]{ values[values.length-1].clone(), {0, 0, 0} };
		this.compartido = new float[][]{{0, 0, 0},{ 0, 0, 0}};
	}

	/**
	 * Constructor que crea un {@code Interpolador3F}.
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
	Interpolador3F(int genKey, float scale, float translation, float[][] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.EAF(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = Keys.toFloat(keys);
		this.funcionesX = Funcion.toFloatFunctions(funciones[0]);
		this.funcionesY = Funcion.toFloatFunctions(funciones[1]);
		this.funcionesZ = Funcion.toFloatFunctions(funciones[2]);
		this.valorInicial = new float[][]{ values[0].clone(), {0, 0, 0} };
		this.valorFinal = new float[][]{ values[values.length-1].clone(), {0, 0, 0} };
		this.compartido = new float[][]{{0, 0, 0},{ 0, 0, 0}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final float[] get(float key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial[0];
		if(index < funcionesX.length){
			compartido[0][0] = funcionesX[index].f(key);
			compartido[0][1] = funcionesY[index].f(key);
			compartido[0][2] = funcionesZ[index].f(key);
			return compartido[0];
		}
		return valorFinal[0];
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final float[] getTan(float key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial[1];
		if(index < funcionesX.length){
			compartido[1][0] = funcionesX[index].f1(key);
			compartido[1][1] = funcionesY[index].f1(key);
			compartido[1][2] = funcionesZ[index].f1(key);
			return compartido[1];
		}
		return valorFinal[1];
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final float[][] getPosTan(float key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			Funcion.Float fX = funcionesX[index];
			Funcion.Float fY = funcionesY[index];
			Funcion.Float fZ = funcionesZ[index];
			compartido[0][0] = fX.f(key);
			compartido[0][1] = fY.f(key);
			compartido[0][2] = fZ.f(key);
			compartido[1][0] = fX.f1(key);
			compartido[1][1] = fY.f1(key);
			compartido[1][2] = fZ.f1(key);
			return compartido;
		}
		return valorFinal;
	}
}

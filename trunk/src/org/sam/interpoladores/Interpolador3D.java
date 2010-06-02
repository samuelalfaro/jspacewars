package org.sam.interpoladores;

/**
 *
 */
final class Interpolador3D implements Trayectoria.Double<double[]>{

	private final transient double[] keys;
	private final transient double[][] valorInicial, valorFinal;
	private final transient double[][] compartido;
	private final transient Funcion.Double funcionesX[];
	private final transient Funcion.Double funcionesY[];
	private final transient Funcion.Double funcionesZ[];

	private static final double[][] tratarDatos(double[] value) {
		double[][] newVal = new double[2][3];
		newVal[0][0] = value[0];
		newVal[0][1] = value[1];
		newVal[0][2] = value[2];
		newVal[1][0] = 0; // No definimos la tangente para el valor inicial
		newVal[1][1] = 0;
		newVal[1][2] = 0;
		return newVal;
	}

	Interpolador3D(double keys[], double[][] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.E3D(values), params );
		this.funcionesX = funciones[0];
		this.funcionesY = funciones[1];
		this.funcionesZ = funciones[2];
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new double[][]{{0, 0, 0},{ 0, 0, 0}};
	}

	Interpolador3D(int genKey, double scale, double translation, double[][] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E3D(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = keys;
		this.funcionesX = funciones[0];
		this.funcionesY = funciones[1];
		this.funcionesZ = funciones[2];
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new double[][]{{0, 0, 0},{ 0, 0, 0}};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final double[] get(double key) {
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
	public final double[] getTan(double key) {
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
	public final double[][] getPosTan(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			Funcion.Double fX = funcionesX[index];
			Funcion.Double fY = funcionesY[index];
			Funcion.Double fZ = funcionesZ[index];
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

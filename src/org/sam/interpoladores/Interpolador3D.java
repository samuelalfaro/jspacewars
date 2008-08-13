package org.sam.interpoladores;

/**
 *
 */
final class Interpolador3D implements Trayectoria.Double<double[]>{

	private final transient double[] keys;
	private final transient double[] valorInicial, valorFinal;
	private final transient double[] compartido;
	private final transient Funcion.Double funcionesX[];
	private final transient Funcion.Double funcionesY[];
	private final transient Funcion.Double funcionesZ[];

	private static final double[] tratarDatos(double[] value) {
		double[] newVal = new double[5];
		newVal[0] = value[0];
		newVal[1] = value[1];
		newVal[2] = value[2];
		newVal[3] = 0; // No definimos la tangente para el valor inicial
		newVal[4] = 0;
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
		this.compartido = new double[]{0, 0, 0, 0, 0};
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
		this.compartido = new double[]{0, 0, 0, 0, 0};
	}

	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Getter#get(double)
	 */
	public final double[] get(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			compartido[0] = funcionesX[index].f(key);
			compartido[1] = funcionesY[index].f(key);
			compartido[2] = funcionesZ[index].f(key);
			return compartido;
		}
		return valorFinal;
	}

	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Trayectoria#getPosTang(double)
	 */
	public final double[] getPosTang(double key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			Funcion.Double fX = funcionesX[index];
			Funcion.Double fY = funcionesY[index];
			Funcion.Double fZ = funcionesZ[index];
			compartido[0] = fX.f(key);
			compartido[1] = fY.f(key);
			compartido[2] = fZ.f(key);
			compartido[3] = Math.atan2(fY.f1(key), fX.f1(key));
			compartido[4] = Math.atan2(fZ.f1(key), fY.f1(key));
			return compartido;
		}
		return valorFinal;
	}
}

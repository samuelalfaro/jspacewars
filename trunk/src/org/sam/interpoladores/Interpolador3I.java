package org.sam.interpoladores;

/**
 *
 */
final class Interpolador3I implements Trayectoria.Integer<int[]>{

	private final transient int[] keys;
	private final transient int[] valorInicial, valorFinal;
	private final transient int[] compartido;
	private final transient Funcion.Integer funcionesX[];
	private final transient Funcion.Integer funcionesY[];
	private final transient Funcion.Integer funcionesZ[];

	private static final int[] tratarDatos(int[] value) {
		int[] newVal = new int[5];
		newVal[0] = value[0];
		newVal[1] = value[1];
		newVal[2] = value[2];
		newVal[3] = 0; // No definimos la tangente para el valor inicial
		newVal[4] = 0;
		return newVal;
	}

	Interpolador3I(int keys[], int[][] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1I(keys), new ArrayExtractor.E3I(values), params );
		this.funcionesX = Funcion.toInteger(funciones[0]);
		this.funcionesY = Funcion.toInteger(funciones[1]);
		this.funcionesZ = Funcion.toInteger(funciones[2]);
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new int[]{0, 0, 0, 0, 0};
	}

	Interpolador3I(int genKey, int scale, int translation, int[][] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E3I(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = Keys.toInteger(keys);
		this.funcionesX = Funcion.toInteger(funciones[0]);
		this.funcionesY = Funcion.toInteger(funciones[1]);
		this.funcionesZ = Funcion.toInteger(funciones[2]);
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new int[]{0, 0, 0, 0, 0};
	}
	
	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Getter#get(double)
	 */
	public final int[] get(int key) {
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
	public final int[] getPosTang(int key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			Funcion.Integer fX = funcionesX[index];
			Funcion.Integer fY = funcionesY[index];
			Funcion.Integer fZ = funcionesZ[index];
			compartido[0] = fX.f(key);
			compartido[1] = fY.f(key);
			compartido[2] = fZ.f(key);
			compartido[3] = (int)Math.atan2(fY.f1(key), fX.f1(key));
			compartido[4] = (int)Math.atan2(fZ.f1(key), fY.f1(key));
			return compartido;
		}
		return valorFinal;
	}
}

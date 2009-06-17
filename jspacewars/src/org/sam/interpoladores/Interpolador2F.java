package org.sam.interpoladores;

/**
 *
 */
final class Interpolador2F  implements Trayectoria.Float<float[]>{

	private final transient float[] keys;
	private final transient float[] valorInicial, valorFinal;
	private final transient float[] compartido;
	private final transient Funcion.Float funcionesX[];
	private final transient Funcion.Float funcionesY[];

	private static final float[] tratarDatos(float[] value) {
		float[] newVal = new float[3];
		newVal[0] = value[0];
		newVal[1] = value[1];
		newVal[2] = 0; // No definimos la tangente para el valor inicial
		return newVal;
	}

	Interpolador2F(float keys[], float[][] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.E2F(values), params );
		this.funcionesX = Funcion.toFloatFunctions( funciones[0]);
		this.funcionesY = Funcion.toFloatFunctions( funciones[1]);
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new float[]{0, 0, 0};
	}

	Interpolador2F(int genKey, float scale, float translation, float[][] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E2F(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = Keys.toFloat(keys);
		this.funcionesX = Funcion.toFloatFunctions( funciones[0]);
		this.funcionesY = Funcion.toFloatFunctions( funciones[1]);
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new float[]{0, 0, 0};
	}

	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Getter.Float#get(float)
	 */
	public final float[] get(float key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			compartido[0] = funcionesX[index].f(key);
			compartido[1] = funcionesY[index].f(key);
			return compartido;
		}
		return valorFinal;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Trayectoria#getPosTang(double)
	 */
	public final float[] getPosTang(float key) {
		int index = Keys.findIndexKey(key,keys);
		if (index < 0)
			return valorInicial;
		if(index < funcionesX.length){
			Funcion.Float fX = funcionesX[index];
			Funcion.Float fY = funcionesY[index];
			compartido[0] = fX.f(key);
			compartido[1] = fY.f(key);
			compartido[2] = (float)Math.atan2(fY.f1(key), fX.f1(key));
			return compartido;
		}
		return valorFinal;
	}
}
package org.sam.interpoladores;

/**
 *
 */
final class Interpolador2F  implements Trayectoria.Float<float[]>{

	private final transient float[] keys;
	private final transient float[][] valorInicial, valorFinal;
	private final transient float[][] compartido;
	private final transient Funcion.Float funcionesX[];
	private final transient Funcion.Float funcionesY[];

	private static final float[][] tratarDatos(float[] value) {
		float[][] newVal = new float[2][2];
		newVal[0][0] = value[0];
		newVal[0][1] = value[1];
		newVal[1][0] = 0; // No definimos la tangente para el valor inicial
		newVal[1][1] = 0;
//		return newVal;
		return new float[][]{value.clone(),{0,0}};
	}

	Interpolador2F(float keys[], float[][] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.E2F(values), params );
		this.funcionesX = Funcion.toFloatFunctions( funciones[0] );
		this.funcionesY = Funcion.toFloatFunctions( funciones[1] );
		this.valorInicial = tratarDatos(values[0]);
		this.valorFinal = tratarDatos(values[values.length-1]);
		this.compartido = new float[][]{ {0, 0}, {0, 0} };
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
		this.compartido = new float[][]{ {0, 0}, {0, 0} };
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
			compartido[0][0] = fX.f(key);
			compartido[0][1] = fY.f(key);
			compartido[1][0] = fX.f1(key);
			compartido[1][1] = fY.f1(key);
			return compartido;
		}
		return valorFinal;
	}
}
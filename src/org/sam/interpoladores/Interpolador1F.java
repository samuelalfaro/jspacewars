package org.sam.interpoladores;

/**
 * 
 */
final class Interpolador1F implements Getter.Float<Float> {

	private final transient float keys[];
	private final transient Float valorInicial, valorFinal;
	private final transient Funcion.Float[] funciones;

	Interpolador1F(float keys[], Float[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = Funcion.toFloatFunctions( mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.Numerico<Float>(values), params )[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1F(int genKey, float scale, float translation, Float[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Numerico<Float>(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = Keys.toFloat(keys);
		this.funciones = Funcion.toFloatFunctions( funciones[0]);
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1F(float keys[], float[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = Funcion.toFloatFunctions( mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.E1F(values), params )[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1F(int genKey, float scale, float translation, float[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

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
}

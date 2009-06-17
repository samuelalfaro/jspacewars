package org.sam.interpoladores;

/**
 * @author Samuel
 *
 */
final class Interpolador1D implements Getter.Double<Double>{

	private final transient double keys[];
	private final transient Double valorInicial, valorFinal;
	private final transient Funcion.Double[] funciones;

	Interpolador1D(double keys[], Double[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.Numerico<Double>(values), params )[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1D(int genKey, double scale, double translation, Double[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Numerico<Double>(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = keys;
		this.funciones = funciones[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1D(double keys[], double[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.E1D(values), params )[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1D(int genKey, double scale, double translation, double[] values, MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1D(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = keys;
		this.funciones = funciones[0];
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Interpolador#get(double)
	 */
	public final Double get(double key) {
		int index = Keys.findIndexKey(key,keys);
		return (index < 0) ?
			valorInicial :
			(index < funciones.length) ?
				funciones [index].f(key) :
				valorFinal;
	}
}
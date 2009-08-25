package org.sam.interpoladores;

/**
 * @author Samuel
 *
 */
final class Interpolador1I implements Getter.Integer<Integer> {

	private final transient int keys[];
	private final transient Integer valorInicial, valorFinal;
	private final transient Funcion.Integer[] funciones;

	Interpolador1I(int keys[], Integer[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = Funcion.toIntegerFunctions( mdi.generarFunciones( new ArrayExtractor.E1I(keys), new ArrayExtractor.Numerico<Integer>(values), params )[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1I(int genKey, int scale, int translation, Integer[] values,	MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Numerico<Integer>(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = Keys.toInteger(keys);
		this.funciones = Funcion.toIntegerFunctions( funciones[0]);
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1I(int keys[], int[] values, MetodoDeInterpolacion mdi, Object... params) {
		this.keys = keys;
		this.funciones = Funcion.toIntegerFunctions( mdi.generarFunciones( new ArrayExtractor.E1I(keys), new ArrayExtractor.E1I(values), params )[0] );
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	Interpolador1I(int genKey, int scale, int translation, int[] values,	MetodoDeInterpolacion mdi, Object... params) {

		Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1I(values), params );
		double keys[];
		if(genKey == Keys.PROPORCIONALES)
			keys = Keys.generateKeys(funciones);
		else
			keys = Keys.generateKeys(funciones[0].length +1);
		Keys.scale(keys, scale);
		Keys.translate(keys, translation);
		Keys.ajustarFunciones(keys, funciones);

		this.keys = Keys.toInteger(keys);
		this.funciones = Funcion.toIntegerFunctions( funciones[0]);
		this.valorInicial = values[0];
		this.valorFinal = values[values.length-1];
	}

	/* (non-Javadoc)
	 * @see org.sam.interpoladores.Interpolador#get(double)
	 */
	public final Integer get(int key) {
		int index = Keys.findIndexKey(key,keys);
		return (index < 0) ?
			valorInicial :
			(index < funciones.length) ?
				funciones [index].f(key) :
				valorFinal;
	}
}

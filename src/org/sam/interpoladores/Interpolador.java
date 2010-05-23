package org.sam.interpoladores;

/**
 * @author Samuel
 *
 * @param <T>
 */
final class Interpolador{

	private Interpolador(){}

	static final class Double<T> implements Getter.Double<T>{

		private transient final double keys[];
		private transient final Introductor.Double<T> introductor;
		private transient final double compartido[];
		private transient final T valorInicial, valorFinal, valorCompartido;
		private transient final Funcion.Double[][] funciones;

		Double (double keys[], T valorCompartido, Introductor.Double<T> introductor, T[] values, Extractor<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.Generico<T>(values, extractor), params );

			this.keys = keys;
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new double[extractor.length(values[0])];
			this.valorCompartido = valorCompartido;
		}

		Double(int genKey, double scale, double translation, T valorCompartido, Introductor.Double<T> introductor, 
				T[] values,	Extractor<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			this.funciones = mdi.generarFunciones( new ArrayExtractor.Generico<T>(values, extractor), params );
			double keys[];
			if(genKey == Keys.PROPORCIONALES)
				keys = Keys.generateKeys(funciones);
			else
				keys = Keys.generateKeys(funciones[0].length +1);
			Keys.scale(keys, scale);
			Keys.translate(keys, translation);
			Keys.ajustarFunciones(keys, funciones);

			this.keys = keys;
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new double[extractor.length(values[0])];
			this.valorCompartido = valorCompartido;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter#get(double)
		 */
		public final T get(double key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.setValues(valorCompartido, compartido);
			}
			return valorFinal;
		}
	}

	static final class Float<T> implements Getter.Float<T>{

		private transient final float keys[];
		private transient final Introductor.Float<T> introductor;
		private transient final float compartido[];
		private transient final T valorInicial, valorFinal, valorCompartido;
		private transient final Funcion.Float[][] funciones;

		Float (float keys[], T valorCompartido, Introductor.Float<T> introductor, T[] values, Extractor<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.Generico<T>(values, extractor), params );

			this.keys = keys;
			this.funciones = new Funcion.Float[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toFloatFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new float[extractor.length(values[0])];
			this.valorCompartido = valorCompartido;
		}

		Float(int genKey, float scale, float translation, T valorCompartido, Introductor.Float<T> introductor, T[] values,
				Extractor<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Generico<T>(values, extractor), params );
			double keys[];
			if(genKey == Keys.PROPORCIONALES)
				keys = Keys.generateKeys(funciones);
			else
				keys = Keys.generateKeys(funciones[0].length +1);
			Keys.scale(keys, scale);
			Keys.translate(keys, translation);
			Keys.ajustarFunciones(keys, funciones);

			this.keys = Keys.toFloat(keys);
			this.funciones = new Funcion.Float[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toFloatFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new float[extractor.length(values[0])];
			this.valorCompartido = valorCompartido;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter#get(double)
		 */
		public final T get(float key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.setValues(valorCompartido, compartido);
			}
			return valorFinal;
		}
	}

	static final class Integer<T> implements Getter.Integer<T>{

		private transient final int keys[];
		private transient final Introductor.Integer<T> introductor;
		private transient final int compartido[];
		private transient final T valorInicial, valorFinal, valorCompartido;
		private transient final Funcion.Integer[][] funciones;

		Integer (int keys[], T valorCompartido, Introductor.Integer<T> introductor, T[] values, Extractor<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1I(keys), new ArrayExtractor.Generico<T>(values, extractor), params );

			this.keys = keys;
			this.funciones = new Funcion.Integer[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toIntegerFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new int[extractor.length(values[0])];
			this.valorCompartido = valorCompartido;
		}

		Integer(int genKey, int scale, int translation, T valorCompartido, Introductor.Integer<T> introductor, T[] values,
				Extractor<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.Generico<T>(values, extractor), params );
			double keys[];
			if(genKey == Keys.PROPORCIONALES)
				keys = Keys.generateKeys(funciones);
			else
				keys = Keys.generateKeys(funciones[0].length +1);
			Keys.scale(keys, scale);
			Keys.translate(keys, translation);
			Keys.ajustarFunciones(keys, funciones);

			this.keys = Keys.toInteger(keys);
			this.funciones = new Funcion.Integer[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toIntegerFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new int[extractor.length(values[0])];
			this.valorCompartido = valorCompartido;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter#get(double)
		 */
		public final T get(int key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.setValues(valorCompartido, compartido);
			}
			return valorFinal;
		}
	}
}

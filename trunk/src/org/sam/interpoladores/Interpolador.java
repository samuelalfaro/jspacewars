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
		private transient final T valorInicial, valorFinal;
		private transient final Funcion.Double[][] funciones;

		Double (double keys[], Introductor.Double<T> introductor, T[] values, Extractor.Double<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			this.funciones = mdi.generarFunciones( new ArrayExtractor.E1D(keys), new ArrayExtractor.GenericoDouble<T>(values, extractor), params );

			this.keys = keys;
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new double[extractor.get(values[0]).length];
		}

		Double(int genKey, double scale, double translation, Introductor.Double<T> introductor, 
				T[] values,	Extractor.Double<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			this.funciones = mdi.generarFunciones( new ArrayExtractor.GenericoDouble<T>(values, extractor), params );
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
			this.compartido = new double[extractor.get(values[0]).length];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final T get(double key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.get(compartido);
			}
			return valorFinal;
		}
	}

	static final class Float<T> implements Getter.Float<T>{

		private transient final float keys[];
		private transient final Introductor.Float<T> introductor;
		private transient final float compartido[];
		private transient final T valorInicial, valorFinal;
		private transient final Funcion.Float[][] funciones;

		Float (float keys[], Introductor.Float<T> introductor, T[] values, Extractor.Float<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.E1F(keys), new ArrayExtractor.GenericoFloat<T>(values, extractor), params );

			this.keys = keys;
			this.funciones = new Funcion.Float[funciones.length][];
			for(int i = 0; i < funciones.length; i++)
				this.funciones[i]  = Funcion.toFloatFunctions(funciones[i]);
			this.valorInicial = values[0];
			this.valorFinal = values[values.length-1];
			this.introductor = introductor;
			this.compartido = new float[extractor.get(values[0]).length];
		}

		Float(int genKey, float scale, float translation, Introductor.Float<T> introductor, T[] values,
				Extractor.Float<? super T> extractor, MetodoDeInterpolacion mdi, Object... params) {

			Funcion.Double[][] funciones = mdi.generarFunciones( new ArrayExtractor.GenericoFloat<T>(values, extractor), params );
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
			this.compartido = new float[extractor.get(values[0]).length];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final T get(float key) {
			int index = Keys.findIndexKey(key,keys);
			if (index < 0)
				return valorInicial;
			if(index < funciones[0].length){
				for(int i=0,len = compartido.length;i<len;i++)
					compartido[i] = funciones[i][index].f(key);
				return introductor.get(compartido);
			}
			return valorFinal;
		}
	}
}

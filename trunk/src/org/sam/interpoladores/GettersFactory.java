package org.sam.interpoladores;

/**
 * @author Samuel
 *
 */
public final class GettersFactory {

	private GettersFactory(){}

	public static class Double{
		Double(){};

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Double<java.lang.Double> create(double keys[], java.lang.Double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new KeySelector.Double<java.lang.Double>( keys, values);
			return new Interpolador1D(keys,values,mdi,params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Double<java.lang.Double> create(int genKey, double scale, double translation, java.lang.Double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new ArraySelector.Double<java.lang.Double>( scale, translation, values);
			return new Interpolador1D(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Double<java.lang.Double> create(double keys[], double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON){
				java.lang.Double[] valuesDouble = new java.lang.Double[values.length];
				for(int i=0; i< values.length; i++)
					valuesDouble[i] = values[i];
				return new KeySelector.Double<java.lang.Double>( keys, valuesDouble);
			}
			return new Interpolador1D(keys,values,mdi,params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Double<java.lang.Double> create(int genKey, double scale, double translation, double values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON){
				java.lang.Double[] valuesDouble = new java.lang.Double[values.length];
				for(int i=0; i< values.length; i++)
					valuesDouble[i] = values[i];
				return new ArraySelector.Double<java.lang.Double>( scale, translation, valuesDouble);
			}
			return new Interpolador1D(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Double<double[]> create(double keys[], double[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2D(keys,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3D(keys,values,mdi,params);
			return new Interpolador.Double<double[]>(keys, null, Introductor.Double.DOUBLE_ARRAY, values, Extractor.DOUBLE_ARRAY, mdi, params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Double<double[]> create(int genKey, double scale, double translation, double[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2D(genKey,scale,translation,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3D(genKey,scale,translation,values,mdi,params);
			return new Interpolador.Double<double[]>(genKey,scale,translation, null, Introductor.Double.DOUBLE_ARRAY, values, Extractor.DOUBLE_ARRAY, mdi, params);
		}

		/**
		 * @param <T>
		 * @param keys
		 * @param shared
		 * @param in
		 * @param values
		 * @param ex
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static <T> Getter.Double<T> create(double keys[], T shared, Introductor.Double<T> in, T values[], Extractor<? super T> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new KeySelector.Double<T>(keys, values);
			return new Interpolador.Double<T>(keys, shared, in, values, ex, mdi,params);
		}

		/**
		 * @param <T>
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param shared
		 * @param in
		 * @param values
		 * @param ex
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static <T> Getter.Double<T> create(int genKey, double scale, double translation, T shared, Introductor.Double<T> in, T values[], Extractor<? super T> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new ArraySelector.Double<T>( scale, translation, values);
			return new Interpolador.Double<T>(genKey,scale,translation, shared, in, values, ex, mdi,params);
		}
	}

	public static class Float{
		Float(){};

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Float<java.lang.Float> create(float keys[], java.lang.Float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null)
				return new KeySelector.Float<java.lang.Float>( keys, values);
			return new Interpolador1F(keys,values,mdi,params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Float<java.lang.Float> create(int genKey, float scale, float translation, java.lang.Float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null)
				return new ArraySelector.Float<java.lang.Float>( scale, translation, values);
			return new Interpolador1F(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Float<java.lang.Float> create(float keys[], float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null){
				java.lang.Float[] valuesFloat = new java.lang.Float[values.length];
				for(int i=0; i< values.length; i++)
					valuesFloat[i] = values[i];
				return new KeySelector.Float<java.lang.Float>( keys, valuesFloat);
			}
			return new Interpolador1F(keys,values,mdi,params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Float<java.lang.Float> create(int genKey, float scale, float translation, float values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null){
				java.lang.Float[] valuesFloat = new java.lang.Float[values.length];
				for(int i=0; i< values.length; i++)
					valuesFloat[i] = values[i];
				return new ArraySelector.Float<java.lang.Float>( scale, translation, valuesFloat);
			}
			return new Interpolador1F(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Float<float[]> create(float keys[], float[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2F(keys,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3F(keys,values,mdi,params);
			return new Interpolador.Float<float[]>(keys, null, Introductor.Float.FLOAT_ARRAY, values, Extractor.FLOAT_ARRAY, mdi, params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Float<float[]> create(int genKey, float scale, float translation, float[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2F(genKey,scale,translation,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3F(genKey,scale,translation,values,mdi,params);
			return new Interpolador.Float<float[]>(genKey,scale,translation, null, Introductor.Float.FLOAT_ARRAY, values, Extractor.FLOAT_ARRAY, mdi, params);
		}

		/**
		 * @param <T>
		 * @param keys
		 * @param shared
		 * @param in
		 * @param values
		 * @param ex
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static <T> Getter.Float<T> create(float keys[], T shared, Introductor.Float<T> in, T values[], Extractor<? super T> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new KeySelector.Float<T>(keys, values);
			return new Interpolador.Float<T>(keys, shared, in, values, ex, mdi,params);
		}

		/**
		 * @param <T>
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param shared
		 * @param in
		 * @param values
		 * @param ex
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static <T> Getter.Float<T> create(int genKey, float scale, float translation, T shared, Introductor.Float<T> in, T values[], Extractor<? super T> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new ArraySelector.Float<T>( scale, translation, values);
			return new Interpolador.Float<T>(genKey,scale,translation, shared, in, values, ex, mdi,params);
		}
	}

	public static class Integer{
		Integer(){};

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Integer<java.lang.Integer> create(int keys[], java.lang.Integer values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null)
				return new KeySelector.Integer<java.lang.Integer>( keys, values);
			return new Interpolador1I(keys,values,mdi,params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Integer<java.lang.Integer> create(int genKey, int scale, int translation, java.lang.Integer values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null)
				return new ArraySelector.Integer<java.lang.Integer>( scale, translation, values);
			return new Interpolador1I(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Integer<java.lang.Integer> create(int keys[], int values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null){
				java.lang.Integer[] valuesInteger = new java.lang.Integer[values.length];
				for(int i=0; i< values.length; i++)
					valuesInteger[i] = values[i];
				return new KeySelector.Integer<java.lang.Integer>( keys, valuesInteger);
			}
			return new Interpolador1I(keys,values,mdi,params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Integer<java.lang.Integer> create(int genKey, int scale, int translation, int values[], MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null){
				java.lang.Integer[] valuesInteger = new java.lang.Integer[values.length];
				for(int i=0; i< values.length; i++)
					valuesInteger[i] = values[i];
				return new ArraySelector.Integer<java.lang.Integer>( scale, translation, valuesInteger);
			}
			return new Interpolador1I(genKey,scale,translation,values,mdi,params);
		}

		/**
		 * @param keys
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Integer<int[]> create(int keys[], int[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2I(keys,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3I(keys,values,mdi,params);
			return new Interpolador.Integer<int[]>(keys, null, Introductor.Integer.INT_ARRAY, values, Extractor.INT_ARRAY, mdi, params);
		}

		/**
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param values
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static Getter.Integer<int[]> create(int genKey, int scale, int translation, int[] values[], MetodoDeInterpolacion mdi, Object... params ){
			if( values[0].length == 2)
				return new Interpolador2I(genKey,scale,translation,values,mdi,params);
			else if( values[0].length == 3)
				return new Interpolador3I(genKey,scale,translation,values,mdi,params);
			return new Interpolador.Integer<int[]>(genKey,scale,translation, null, Introductor.Integer.INT_ARRAY, values, Extractor.INT_ARRAY, mdi, params);
		}

		/**
		 * @param <T>
		 * @param keys
		 * @param shared
		 * @param in
		 * @param values
		 * @param ex
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static <T> Getter.Integer<T> create(int keys[], T shared, Introductor.Integer<T> in, T values[], Extractor<? super T> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new KeySelector.Integer<T>(keys, values);
			return new Interpolador.Integer<T>(keys, shared, in, values, ex, mdi,params);
		}

		/**
		 * @param <T>
		 * @param genKey
		 * @param scale
		 * @param translation
		 * @param shared
		 * @param in
		 * @param values
		 * @param ex
		 * @param mdi
		 * @param params
		 * @return
		 */
		public static <T> Getter.Integer<T> create(int genKey, int scale, int translation, T shared, Introductor.Integer<T> in, T values[], Extractor<? super T> ex, MetodoDeInterpolacion mdi, Object... params ){
			if (mdi == null || mdi == MetodoDeInterpolacion.Predefinido.ESCALON)
				return new ArraySelector.Integer<T>( scale, translation, values);
			return new Interpolador.Integer<T>(genKey,scale,translation, shared, in, values, ex, mdi,params);
		}
	}
}
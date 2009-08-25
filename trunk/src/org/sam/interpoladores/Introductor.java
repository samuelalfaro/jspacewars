package org.sam.interpoladores;

/**
 *
 * @author Samuel
 */
public interface Introductor {

    /**
     *
     * @param <T>
     */
    public static interface Double<T>{
        /**
         * 
         */
        public static final Introductor.Double<double[]> DOUBLE_ARRAY = new Introductor.Double<double[]>(){
			public double[] setValues(double[] dst, double... v){
				if( dst != null){
					System.arraycopy(v, 0, dst, 0, v.length);
					return dst;
				}
				return v;
			}
		};
		
        /**
         *
         * @param dst
         * @param v
         * @return
         */
        public T setValues(T dst, double... v);
	}

    /**
     *
     * @param <T>
     */
    public static interface Float<T>{
        /**
         *
         */
        public static final Introductor.Float<float[]> FLOAT_ARRAY = new Introductor.Float<float[]>(){
			public float[] setValues(float[] dst, float... v){
				if( dst != null){
					System.arraycopy(v, 0, dst, 0, v.length);
					return dst;
				}
				return v;
			}
		};
		
        /**
         *
         * @param dst
         * @param v
         * @return
         */
        public T setValues(T dst, float... v);
	}

    /**
     *
     * @param <T>
     */
    public static interface Integer<T>{
        /**
         *
         */
        public static final Introductor.Integer<int[]> INT_ARRAY = new Introductor.Integer<int[]>(){
			public int[] setValues(int[] dst, int... v){
				if( dst != null){
					System.arraycopy(v, 0, dst, 0, v.length);
					return dst;
				}
				return v;
			}
		};

        /**
         *
         * @param dst
         * @param v
         * @return
         */
        public T setValues(T dst, int... v);
	}
}
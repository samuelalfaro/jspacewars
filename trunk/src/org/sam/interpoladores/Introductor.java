package org.sam.interpoladores;

/**
 *
 * @param <T>
 * @param <V>
 */
public interface Introductor<T,V> {

    /**
     *
     * @param <T>
     */
    public interface Double<T> extends Introductor<T,double[]>{

        /**
         * 
         */
        public static final Introductor.Double<double[]> DOUBLE_ARRAY = new Introductor.Double<double[]>(){
			public double[] get(double[] v){
				return v;
			}
		};
	}

    /**
     *
     * @param <T>
     */
    public interface Float<T> extends Introductor<T,float[]>{
        /**
         *
         */
        public static final Introductor.Float<float[]> FLOAT_ARRAY = new Introductor.Float<float[]>(){
			public float[] get(float[] v){
				return v;
			}
		};
	}
    
    /**
     * @param v
     * @return
     */
    public T get(V v);
}
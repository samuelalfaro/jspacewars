package org.sam.interpoladores;

/**
 *
 * @param <V>
 * @param <T>
 */
public interface Extractor<V,T> {

    /**
     *
     * @param <T>
     */
    public interface Double<T> extends Extractor<double[], T>{
    	
	    /**
	     * 
	     */
	    public static final  Extractor.Double<double[]> DOUBLE_ARRAY = new Extractor.Double<double[]>(){
			/**
			 * @param v
			 * @return
			 */
			public double[] get(double[] v){
				return v;
			}
		};
    }
    
    /**
     *
     * @param <T>
     */
    public interface Float<T> extends Extractor<float[], T>{
    	
	    /**
	     * 
	     */
	    public static final Extractor.Float<float[]> FLOAT_ARRAY = new Extractor.Float<float[]>(){
			/**
			 * @param v
			 * @return
			 */
			public float[] get(float[] v){
				return v;
			}
		};
    }

    /**
     * @param t
     * @return
     */
    public V get(T t);
}

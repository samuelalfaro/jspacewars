package org.sam.interpoladores;

public interface Extractor<T> {

	public static final Extractor<double[]> DOUBLE_ARRAY = new Extractor<double[]>(){
		public int length(double[] v){
			return v.length;
		}
		
		public double at(double[] v, int index){
			return v[index];
		}
	};
	
	public static final Extractor<float[]> FLOAT_ARRAY = new Extractor<float[]>(){
		public int length(float[] v){
			return v.length;
		}
		
		public double at(float[] v, int index){
			return v[index];
		}
	};

	public static final Extractor<int[]> INT_ARRAY = new Extractor<int[]>(){
		public int length(int[] v){
			return v.length;
		}
		
		public double at(int[] v, int index){
			return v[index];
		}
	};

	public int length(T v);
	
	public double at(T v, int index);
	
}

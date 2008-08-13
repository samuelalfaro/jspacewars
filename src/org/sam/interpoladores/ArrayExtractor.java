package org.sam.interpoladores;
/**
 * @author samuel
 *
 * @param <T>
 */
public interface ArrayExtractor{ 
	
	static final class Generico<T> implements ArrayExtractor{
		private final T v[];
		private final Extractor<? super T> extractor;
		
		public Generico(T[] v, Extractor<? super T> extractor){
			this.v = v;
			this.extractor = extractor;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return extractor.length(v[0]);
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return extractor.at(v[f], c);
		}
	};
	
	static final class Numerico<T extends Number> implements ArrayExtractor{
		private final T v[];
		
		public Numerico(T v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 1;
		}
		public double at(int index){
			return v[index].doubleValue();
		}

		public double at(int f, int c){
			return v[f].doubleValue();
		}
	};
	
	static final class E1D implements ArrayExtractor{
		private final double v[];
		
		public E1D(double v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 1;
		}
		public double at(int index){
			return v[index];
		}

		public double at(int f, int c){
			return v[f];
		}
	};
	
	static final class E1F implements ArrayExtractor{
		private final float v[];
		
		public E1F(float v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 1;
		}
		public double at(int index){
			return v[index];
		}

		public double at(int f, int c){
			return v[f];
		}
	};

	static final class E1I implements ArrayExtractor{
		private final int v[];
		
		public E1I(int v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 1;
		}
		public double at(int index){
			return v[index];
		}

		public double at(int f, int c){
			return v[f];
		}
	};
	
	static final class E2D implements ArrayExtractor{
		private final double[] v[];
		
		public E2D(double[] v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 2;
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return v[f][c];
		}
	};
	
	static final class E2F implements ArrayExtractor{
		private final float[] v[];
		
		public E2F(float[] v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 2;
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return v[f][c];
		}
	};

	static final class E2I implements ArrayExtractor{
		private final int[] v[];
		
		public E2I(int[] v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 2;
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return v[f][c];
		}
	};
	
	static final class E3D implements ArrayExtractor{
		private final double[] v[];
		
		public E3D(double[] v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 3;
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return v[f][c];
		}
	};
	
	static final class E3F implements ArrayExtractor{
		private final float[] v[];
		
		public E3F(float[] v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 3;
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return v[f][c];
		}
	};

	static final class E3I implements ArrayExtractor{
		private final int[] v[];
		
		public E3I(int[] v[]){
			this.v = v;
		}
		public int length() {
			return v.length;
		}
		public int length(int index) {
			return 3;
		}
		public double at(int index){
			throw new UnsupportedOperationException();
		}

		public double at(int f, int c){
			return v[f][c];
		}
	};
	
	public int length();
	
	public int length(int index);
	
	public double at(int index);
	
	public double at(int f, int c);
}

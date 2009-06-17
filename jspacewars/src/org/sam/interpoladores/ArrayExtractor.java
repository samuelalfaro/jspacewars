package org.sam.interpoladores;
/**
 * @author samuel
 *
 */
abstract class ArrayExtractor{ 
	
    /**
     *
     * @param <T>
     */
    static final class Generico<T> extends ArrayExtractor{
		private final T v[];
		private final Extractor<? super T> extractor;
		
        /**
         *
         * @param v
         * @param extractor
         */
        public Generico(T[] v, Extractor<? super T> extractor){
			this.v = v;
			this.extractor = extractor;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return extractor.length(v[0]);
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return extractor.at(v[f], c);
		}
	};
	
    /**
     *
     * @param <T>
     */
    static final class Numerico<T extends Number> extends ArrayExtractor{
		private final T v[];
		
        /**
         *
         * @param v
         */
        public Numerico(T v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 1;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			return v[index].doubleValue();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f].doubleValue();
		}
	};
	
    /**
     *
     */
    static final class E1D extends ArrayExtractor{
		private final double v[];
		
        /**
         *
         * @param v
         */
        public E1D(double v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 1;
		}
        
        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			return v[index];
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f];
		}
	};
	
    /**
     *
     */
    static final class E1F extends ArrayExtractor{
		private final float v[];
		
        /**
         *
         * @param v
         */
        public E1F(float v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 1;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			return v[index];
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f];
		}
	};

    /**
     *
     */
    static final class E1I extends ArrayExtractor{
		private final int v[];
		
        /**
         *
         * @param v
         */
        public E1I(int v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 1;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			return v[index];
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f];
		}
	};
	
    /**
     *
     */
    static final class E2D extends ArrayExtractor{
		private final double[] v[];
		
        /**
         *
         * @param v
         */
        public E2D(double[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 2;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f][c];
		}
	};
	
    /**
     *
     */
    static final class E2F extends ArrayExtractor{
		private final float[] v[];
		
        /**
         *
         * @param v
         */
        public E2F(float[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 2;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f][c];
		}
	};

    /**
     *
     */
    static final class E2I extends ArrayExtractor{
		private final int[] v[];
		
        /**
         *
         * @param v
         */
        public E2I(int[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 2;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f][c];
		}
	};
	
    /**
     *
     */
    static final class E3D extends ArrayExtractor{
		private final double[] v[];
		
        /**
         *
         * @param v
         */
        public E3D(double[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 3;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f][c];
		}
	};
	
    /**
     *
     */
    static final class E3F extends ArrayExtractor{
		private final float[] v[];
		
        /**
         *
         * @param v
         */
        public E3F(float[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 3;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f][c];
		}
	};

    /**
     *
     */
    static final class E3I extends ArrayExtractor{
		private final int[] v[];
		
        /**
         *
         * @param v
         */
        public E3I(int[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int length() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int length(int index) {
			return 3;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int)
         */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return v[f][c];
		}
	};
	
    /**
     *
     * @return
     */
    public abstract int length();
	
    /**
     *
     * @param index
     * @return
     */
    public abstract int length(int index);
	
    /**
     *
     * @param index
     * @return
     */
    public abstract double at(int index);
	
    /**
     *
     * @param f
     * @param c
     * @return
     */
    public abstract double at(int f, int c);
}

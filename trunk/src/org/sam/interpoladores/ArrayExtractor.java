package org.sam.interpoladores;

/**
 *
 */
interface ArrayExtractor{ 
	
    /**
     *
     * @param <T>
     */
    static final class GenericoDouble<T> implements ArrayExtractor{
		private final T v[];
		private final Extractor.Double<? super T> extractor;
		
        /**
         * @param v
         * @param extractor
         */
        public GenericoDouble(T[] v, Extractor.Double<? super T> extractor){
			this.v = v;
			this.extractor = extractor;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return extractor.get(v[0]).length;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return extractor.get(v[f])[c];
		}
	}

    /**
     *
     * @param <T>
     */
    static final class GenericoFloat<T> implements ArrayExtractor{
    	private final T v[];
		private final Extractor.Float<? super T> extractor;
		
        /**
         *
         * @param v
         * @param extractor
         */
        public GenericoFloat(T[] v, Extractor.Float<? super T> extractor){
			this.v = v;
			this.extractor = extractor;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#length(int)
         */
        public int columns() {
			return extractor.get(v[0]).length;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
        public double at(int index){
			throw new UnsupportedOperationException();
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#at(int, int)
         */
        public double at(int f, int c){
			return extractor.get(v[f])[c];
		}
	}
	
    /**
     *
     * @param <T>
     */
    static final class Numerico<T extends Number> implements ArrayExtractor{
		private final T v[];
		
        /**
         *
         * @param v
         */
        public Numerico(T v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
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
	}
	
    /**
     *
     */
    static final class E1D implements ArrayExtractor{
		private final double v[];
		
        /**
         *
         * @param v
         */
        public E1D(double v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
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
	}
	
    /**
     *
     */
    static final class E1F implements ArrayExtractor{
		private final float v[];
		
        /**
         *
         * @param v
         */
        public E1F(float v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
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
	}

    /**
     *
     */
    static final class E1I implements ArrayExtractor{
		private final int v[];
		
        /**
         *
         * @param v
         */
        public E1I(int v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
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
	}
	
    /**
     *
     */
    static final class E2D implements ArrayExtractor{
		private final double[] v[];
		
        /**
         *
         * @param v
         */
        public E2D(double[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return 2;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
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
	}
	
    /**
     *
     */
    static final class E2F implements ArrayExtractor{
		private final float[] v[];
		
        /**
         *
         * @param v
         */
        public E2F(float[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return 2;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
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
	}

    /**
     *
     */
    static final class E2I implements ArrayExtractor{
		private final int[] v[];
		
        /**
         *
         * @param v
         */
        public E2I(int[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return 2;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
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
	}
	
    /**
     *
     */
    static final class E3D implements ArrayExtractor{
		private final double[] v[];
		
        /**
         *
         * @param v
         */
        public E3D(double[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return 3;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
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
	}
	
    /**
     *
     */
    static final class E3F implements ArrayExtractor{
		private final float[] v[];
		
        /**
         *
         * @param v
         */
        public E3F(float[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return 3;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
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
	}

    static final class E3I implements ArrayExtractor{
		private final int[] v[];
		
        /**
         *
         * @param v
         */
        public E3I(int[] v[]){
			this.v = v;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#rows()
         */
        public int rows() {
			return v.length;
		}

        /* (non-Javadoc)
         * @see org.sam.interpoladores.ArrayExtractor#columns()
         */
        public int columns() {
			return 3;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
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
	}
	
    /**
     *
     * @return
     */
    public abstract int rows();
	
    /**
     * @return
     */
    public abstract int columns();
	
    /**
     * @param index
     * @return
     * @throws UnsupportedOperationException cuando la operación no está soportada.
     * Puesto que este método es una optimización para los valores de una única dimensión.
     */
    public abstract double at(int index) throws UnsupportedOperationException;
	
    /**
     *
     * @param f
     * @param c
     * @return
     */
    public abstract double at(int f, int c);
}

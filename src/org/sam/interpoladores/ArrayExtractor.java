package org.sam.interpoladores;

/**
 *
 */
interface ArrayExtractor{ 
	
    /**
     *
     * @param <T> Tipo genérico de datos empleados.
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return extractor.get(v[0]).length;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return extractor.get(v[index])[dim];
		}
	}

    /**
     *
     * @param <T> Tipo genérico de datos empleados.
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return extractor.get(v[0]).length;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
    	public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return extractor.get(v[index])[dim];
		}
	}
	
    /**
     *
     * @param <T> Tipo numérico genérico de datos empleados.
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index].doubleValue();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index].doubleValue();
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}
        
    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index];
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index];
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 1;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index){
			return v[index];
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index][dim];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 2;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index][dim];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 2;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index][dim];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 3;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
        @Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index][dim];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 3;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index][dim];
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

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int length() {
			return v.length;
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public int dimensions() {
			return 3;
		}

    	/**
    	 * Operación no soportada.
    	 * @throws UnsupportedOperationException
    	 * puesto que este método es una optimización para los valores de una única dimensión.
    	 */
    	@Override
        public double at(int index){
			throw new UnsupportedOperationException();
		}

    	/**
    	 * {@inheritDoc}
    	 */
    	@Override
        public double at(int index, int dim){
			return v[index][dim];
		}
	}
	
    /**
     *
     * @return longitud del vector encapsulado.
     */
    public int length();
	
    /**
     * @return dimensiones de los elementos que contiene el vector.
     */
    public int dimensions();
	
    /**
     * @param index índice dentro del vector encapusulado.
     * @return el valor de esa posición.
     * @throws UnsupportedOperationException cuando la operación no está soportada.
     * Puesto que este método es una optimización para los valores de una única dimensión.
     */
    public double at(int index) throws UnsupportedOperationException;
	
    /**
     *
     * @param index índice dentro del vector encapusulado.
     * @param dim índice dentro de las dimensiónes del elemento correspondiente.
     * @return el valor de esa posición.
     */
    public double at(int index, int dim);
}

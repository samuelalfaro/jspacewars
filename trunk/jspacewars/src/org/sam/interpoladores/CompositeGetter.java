package org.sam.interpoladores;

/**
 * @author samuel
 *
 */
public final class CompositeGetter{
	private CompositeGetter(){}
	
    /**
     *
     * @param <T>
     */
    public static final class Double<T> implements Getter.Double<T>{

		private final Getter.Double<java.lang.Double> key;
		private final Getter.Double<T> getter;

        /**
         *
         * @param key
         * @param getter
         */
        public Double(Getter.Double<java.lang.Double> key, Getter.Double<T> getter) {
			this.key = key;
			this.getter = getter;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter.Double#get(double)
		 */
		public T get(double key) {
			return this.getter.get(this.key.get(key));
		}
	}

    /**
     * 
     * @param <T>
     */
    public static final class Float<T> implements Getter.Float<T>{

		private final Getter.Float<java.lang.Float> key;
		private final Getter.Float<T> getter;

        /**
         *
         * @param key
         * @param getter
         */
        public Float(Getter.Float<java.lang.Float> key, Getter.Float<T> getter) {
			this.key = key;
			this.getter = getter;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter.Double#get(double)
		 */
		public T get(float key) {
			return this.getter.get(this.key.get(key));
		}
	}

    /**
     *
     * @param <T>
     */
    public static final class Integer<T> implements Getter.Integer<T>{

		private final Getter.Integer<java.lang.Integer> key;
		private final Getter.Integer<T> getter;

        /**
         *
         * @param key
         * @param getter
         */
        public Integer(Getter.Integer<java.lang.Integer> key, Getter.Integer<T> getter) {
			this.key = key;
			this.getter = getter;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter.Double#get(double)
		 */
		public T get(int key) {
			return this.getter.get(this.key.get(key));
		}
	}
}

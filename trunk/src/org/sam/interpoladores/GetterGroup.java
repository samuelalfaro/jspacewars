package org.sam.interpoladores;

/**
 * @author samuel
 *
 */
public final class GetterGroup{
	private GetterGroup(){}
	
    /**
     *
     * @param <T>
     */
    public static final class Double<T> implements Getter.Double<T>{

		private final double[] keys;
		private final Getter.Double<T> getters[];

        /**
         *
         * @param keys
         * @param getters
         */
        public Double(double keys[], Getter.Double<T>[] getters) {
			assert(keys.length == getters.length);
			this.keys = keys;
			this.getters = getters;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(double key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return getters[0].get(key);
			if ((index == getters.length -1) || (key - keys[index] < keys[index + 1] - key ))
				getters[index].get(key);
			return getters[index+1].get(key);
		}
	}

    /**
     *
     * @param <T>
     */
    public static final class Float<T> implements Getter.Float<T>{

		private final float[] keys;
		private final Getter.Float<T> getters[];

        /**
         *
         * @param keys
         * @param getters
         */
        public Float(float keys[], Getter.Float<T>[] getters) {
			assert(keys.length == getters.length);
			this.keys = keys;
			this.getters = getters;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(float key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return getters[0].get(key);
			if ((index == getters.length -1) || (key - keys[index] < keys[index + 1] - key ))
				getters[index].get(key);
			return getters[index+1].get(key);
		}
	}

    /**
     *
     * @param <T>
     */
    public static final class Integer<T> implements Getter.Integer<T>{

		private final int[] keys;
		private final Getter.Integer<T> getters[];

        /**
         *
         * @param keys
         * @param getters
         */
        public Integer(int keys[], Getter.Integer<T>[] getters) {
			assert(keys.length == getters.length);
			this.keys = keys;
			this.getters = getters;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(int key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return getters[0].get(key);
			if ((index == getters.length -1) || (key - keys[index] < keys[index + 1] - key ))
				getters[index].get(key);
			return getters[index+1].get(key);
		}
	}
}

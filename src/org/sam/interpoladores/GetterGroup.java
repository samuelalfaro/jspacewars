package org.sam.interpoladores;

/**
 * @author samuel
 *
 * @param <T>
 */
public final class GetterGroup{
	private GetterGroup(){}
	
	public static final class Double<T> implements Getter.Double<T>{

		private final double[] keys;
		private final Getter.Double<T> getters[];

		public Double(double keys[], Getter.Double<T>[] getters) {
			assert(keys.length == getters.length);
			this.keys = keys;
			this.getters = getters;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter.Double#get(double)
		 */
		public T get(double key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return getters[0].get(key);
			if ((index == getters.length -1) || (key - keys[index] < keys[index + 1] - key ))
				getters[index].get(key);
			return getters[index+1].get(key);
		}
	}

	public static final class Float<T> implements Getter.Float<T>{

		private final float[] keys;
		private final Getter.Float<T> getters[];

		public Float(float keys[], Getter.Float<T>[] getters) {
			assert(keys.length == getters.length);
			this.keys = keys;
			this.getters = getters;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter.Float#get(float)
		 */
		public T get(float key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return getters[0].get(key);
			if ((index == getters.length -1) || (key - keys[index] < keys[index + 1] - key ))
				getters[index].get(key);
			return getters[index+1].get(key);
		}
	}

	public static final class Integer<T> implements Getter.Integer<T>{

		private final int[] keys;
		private final Getter.Integer<T> getters[];

		public Integer(int keys[], Getter.Integer<T>[] getters) {
			assert(keys.length == getters.length);
			this.keys = keys;
			this.getters = getters;
		}

		/* (non-Javadoc)
		 * @see org.sam.interpoladores.Getter.Integer#get(int)
		 */
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

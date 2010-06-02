package org.sam.interpoladores;

final class KeySelector{
	private KeySelector(){}
	
	static class Double<T> implements Getter.Double<T>{

		private final double[] keys;
		private final T values[];

		Double(double keys[], T[] values) {
			assert(keys.length == values.length);
			this.keys = keys;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(double key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return values[0];
			if ((index == values.length -1) || (key - keys[index] < keys[index + 1] - key ))
				return values[index];
			return values[index+1];
		}
	}
	
	static class Float<T> implements Getter.Float<T>{

		private final float[] keys;
		private final T values[];

		Float(float keys[], T[] values) {
			assert(keys.length == values.length);
			this.keys = keys;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(float key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return values[0];
			if ((index == values.length -1) || (key - keys[index] < keys[index + 1] - key ))
				return values[index];
			return values[index+1];
		}
	}
	
	static class Integer<T> implements Getter.Integer<T>{

		private final int[] keys;
		private final T values[];

		Integer(int keys[], T[] values) {
			assert(keys.length == values.length);
			this.keys = keys;
			this.values = values;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public T get(int key) {
			int index = Keys.findIndexKey(key, keys);
			if (index < 0)
				return values[0];
			if ((index == values.length -1) || (key - keys[index] < keys[index + 1] - key ))
				return values[index];
			return values[index+1];
		}
	}
}

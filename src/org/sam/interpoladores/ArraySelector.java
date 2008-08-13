package org.sam.interpoladores;

/**
 * @author samuel
 *
 * @param <T>
 */
final class ArraySelector{
	private ArraySelector(){}
	
	static class Double<T> implements Getter.Double<T>{
		private final double scale;
		private final double translation;
		private final T values[];

		Double(T[] values) {
			this(1.0, 0.0, values);
		}

		Double(double scale, double translation, T[] values) {
			this.scale = (double)(values.length-1)/ scale;
			this.translation = -translation;
			this.values = values;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sam.interpoladores.Getter#get(double)
		 */
		public T get(double key) {
			int index = (int) ((key + translation) * scale + 0.5);
			return values[(index < 0) ? 0 : index >= values.length ? values.length - 1 : index];
		}
	}
	
	static class Float<T> implements Getter.Float<T>{
		private final float scale;
		private final float translation;
		private final T values[];

		Float(T[] values) {
			this(1.0f, 0.0f, values);
		}

		Float(float scale, float translation, T[] values) {
			this.scale = (float)(values.length-1)/ scale;
			this.translation = -translation;
			this.values = values;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sam.interpoladores.Getter#get(double)
		 */
		public T get(float key) {
			int index = (int) ((key + translation) * scale + 0.5);
			return values[(index < 0) ? 0 : index >= values.length ? values.length - 1 : index];
		}
	}
	
	static class Integer<T> implements Getter.Integer<T>{
		private final int scale;
		private final int translation;
		private final T values[];

		Integer(T[] values) {
			this(1, 0, values);
		}

		Integer(int scale, int translation, T[] values) {
			this.scale = (values.length-1)/ scale;
			this.translation = -translation;
			this.values = values;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sam.interpoladores.Getter#get(double)
		 */
		public T get(int key) {
			int index = ((key + translation) * scale);
			return values[(index < 0) ? 0 : index >= values.length ? values.length - 1 : index];
		}
	}
}

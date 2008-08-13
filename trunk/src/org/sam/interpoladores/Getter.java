package org.sam.interpoladores;

/**
 * @author Samuel
 *
 * @param <T>
 */
public final class Getter{
	private Getter(){};

	public interface Double<T>{
		/**
		 * @param key
		 * @return
		 */
		public T get(double key);
	}

	public interface Float<T>{
		/**
		 * @param key
		 * @return
		 */
		public T get(float key);
	}

	public interface Integer<T>{
		/**
		 * @param key
		 * @return
		 */
		public T get(int key);
	}
}
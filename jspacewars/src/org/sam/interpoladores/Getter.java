package org.sam.interpoladores;

/**
 * @author Samuel
 *
 */
public final class Getter{
	private Getter(){};

    /**
     *
     * @param <T>
     */
    public interface Double<T>{
		/**
		 * @param key
		 * @return
		 */
		public T get(double key);
	}

    /**
     *
     * @param <T>
     */
    public interface Float<T>{
		/**
		 * @param key
		 * @return
		 */
		public T get(float key);
	}

    /**
     *
     * @param <T>
     */
    public interface Integer<T>{
		/**
		 * @param key
		 * @return
		 */
		public T get(int key);
	}
}
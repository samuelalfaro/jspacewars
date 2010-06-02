package org.sam.interpoladores;

/**
 *
 * @author Samuel
 */
public final class Trayectoria{
	private Trayectoria(){}
	
    /**
     * 
     * @param <T>
     */
    public interface Integer<T> extends Getter.Integer<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getTan(int key);
		/**
		 * @param key
		 * @return
		 */
		public T[] getPosTan(int key);
	}

    /**
     *
     * @param <T>
     */
    public interface Float<T> extends Getter.Float<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getTan(float key);
		/**
		 * @param key
		 * @return
		 */
		public T[] getPosTan(float key);
	}

    /**
     *
     * @param <T>
     */
    public interface Double<T> extends Getter.Double<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getTan(double key);
		/**
		 * @param key
		 * @return
		 */
		public T[] getPosTan(double key);
	}
}

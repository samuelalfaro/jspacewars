package org.sam.interpoladores;

/**
 *
 * @author Samuel
 */
public final class Trayectoria{
	private Trayectoria(){};
	
    /**
     * 
     * @param <T>
     */
    public interface Integer<T> extends Getter.Integer<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getPosTang(int key);
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
		public T getPosTang(float key);
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
		public T getPosTang(double key);
	}
}

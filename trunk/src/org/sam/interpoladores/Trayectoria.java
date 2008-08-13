package org.sam.interpoladores;

public final class Trayectoria{
	private Trayectoria(){};
	
	public interface Integer<T> extends Getter.Integer<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getPosTang(int key);
	}

	public interface Float<T> extends Getter.Float<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getPosTang(float key);
	}

	public interface Double<T> extends Getter.Double<T>{
		/**
		 * @param key
		 * @return
		 */
		public T getPosTang(double key);
	}
}

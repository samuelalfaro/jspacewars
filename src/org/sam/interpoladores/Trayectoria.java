package org.sam.interpoladores;

/**
 * 
 */
public final class Trayectoria{
	private Trayectoria(){}
	
    /**
     * @param <T> Tipo genérico de datos empleados.
     */
    public interface Double<T> extends Getter.Double<T>{
		/**
		 * Método que devuelve la tangencia correspondiente a la clave pasada como parámetro.
		 * 
		 * @param key clave empleada para obtener el tangencia.
		 * @return la tangencia correspondiente.
		 */
		public T getTan(double key);
		/**
		 * Método que devuelve el dato y la tangencia correspondientes a la clave pasada como parámetro.
		 * 
		 * @param key clave empleada para obtener el dato.
		 * @return el dato y la tangencia correspondiente.
		 */
		public T[] getPosTan(double key);
	}
	
    /**
     * @param <T> Tipo genérico de datos empleados.
     */
    public interface Float<T> extends Getter.Float<T>{
		/**
		 * Método que devuelve la tangencia correspondiente a la clave pasada como parámetro.
		 * 
		 * @param key clave empleada para obtener el tangencia.
		 * @return la tangencia correspondiente.
		 */
		public T getTan(float key);
		/**
		 * Método que devuelve el dato y la tangencia correspondientes a la clave pasada como parámetro.
		 *  
		 * @param key clave empleada para obtener el dato.
		 * @return el dato y la tangencia correspondiente.
		 */
		public T[] getPosTan(float key);
	}

}

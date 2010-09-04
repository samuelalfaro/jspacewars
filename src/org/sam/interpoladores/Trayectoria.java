package org.sam.interpoladores;

/**
 * Clase contenedora que provee los interfaces que representan una <i>trayectoria</i>, tanto en precisión
 * {@code double}, {@code float}.<br/>
 * Se considera una <i>trayectoria</i> a un <i>Getter</i> interpolado que a parte de poseer:
 * <ul>
 * <li>un método <i>T get(I clave)</i> que devuelve un valor {@code T} partir de una clave {@code I}.</li>
 * </ul>
 * posee los métodos para poder obetener tanto la tangecia, como el valor junto a su tangencia:
 * <ul>
 * <li><i>T getTan(I clave)</i> que devuelve la tangencia {@code T} partir de una clave {@code I}.</li>
 * <li><i>T[] getPosTan(I clave)</i> que devuelve tanto el valor como la tangencia {@code T} partir de una clave
 * {@code I}.</li>
 * </ul>
 */
public final class Trayectoria{
	private Trayectoria(){}
	
    /**
     * Interface que representa una {@code Trayectoria} con precisión {@code double}.
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
     * Interface que representa una {@code Trayectoria} con precisión {@code float}.
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
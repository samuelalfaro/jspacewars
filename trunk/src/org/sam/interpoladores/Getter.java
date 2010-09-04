package org.sam.interpoladores;

/**
 * Clase contenedora que provee los interfaces que representan un <i>Getter</i>,
 * tanto en precisión {@code double}, {@code float}, como {@code int}.<br/>
 * Se considera <i>Getter</i> a una clase que posee un método <i>T get(I clave)</i>
 * que devuelve un dato de tipo {@code T} partir de una clave de tipo {@code I}.
 */
public final class Getter{
	private Getter(){}

    /**
     * Interface que representa un {@code Getter} con precisión {@code double}.
     * @param <T> Tipo genérico de datos empleados.
     */
    public interface Double<T>{
		/**
		 * Método que devuelve el dato correspondiente a la clave pasada como parámetro.
		 * 
		 * @param key clave empleada para obtener el dato.
		 * @return el dato correspondiente.
		 */
		public T get(double key);
	}

    /**
     * Interface que representa un {@code Getter} con precisión {@code float}.
     * @param <T> Tipo genérico de datos empleados.
     */
    public interface Float<T>{
		/**
		 * Método que devuelve el dato correspondiente a la clave pasada como parámetro.
		 * 
		 * @param key clave empleada para obtener el dato.
		 * @return el dato correspondiente.
		 */
		public T get(float key);
	}

    /**
     * Interface que representa un {@code Getter} con precisión {@code int}.
     * @param <T> Tipo genérico de datos empleados.
     */
    public interface Integer<T>{
		/**
		 * Método que devuelve el dato correspondiente a la clave pasada como parámetro.
		 * 
		 * @param key clave empleada para obtener el dato.
		 * @return el dato correspondiente.
		 */
		public T get(int key);
	}
}
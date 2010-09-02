package org.sam.interpoladores;

/**
 * 
 */
public final class Getter{
	private Getter(){}

    /**
     *
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
     *
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
     *
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
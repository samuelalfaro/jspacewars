package org.sam.colisiones;

/**
 * Interface que proporciona los métodos para tratar los límites de los objetos.
 */
public interface Limites {

	/**
	 * @return El valor mínimo de X para cualquier punto contenido dentro de los límites.
	 */
	public float getXMin();
	/**
	 * @return El valor máximo de X para cualquier punto contenido dentro de los límites.
	 */
	public float getXMax();
	/**
	 * @return El valor máximo de Y para cualquier punto contenido dentro de los límites.
	 */
	public float getYMin();
	/**
	 * @return El valor máximo de Y para cualquier punto contenido dentro de los límites.
	 */
	public float getYMax();
	
}

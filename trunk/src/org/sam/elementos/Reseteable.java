package org.sam.elementos;

/**
 * Interface que poporciona el método, a los objetos de las clases que lo implementan,
 * para reasignar a sus miembros unos valores iniciales preestablecidos.
 */
public interface Reseteable{
	/**
	 * Método, que reasigna los miembros, del objeto que lo invoca,
	 * a sus valores iniciales preestablecidos.
	 */
	public void reset();
}

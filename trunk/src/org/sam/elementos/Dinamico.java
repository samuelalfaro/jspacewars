package org.sam.elementos;

/**
 * Interface que proporciona el método para que un objecto actúe a lo largo del tiempo.
 */
public interface Dinamico {
	/**
	 * Método que indica que la clase que lo implemente realizará una determinada
	 * acción durante un determinado periodo de tiempo.
	 * @param nanos periodo, definido en nanosegundos, durante el que actua el objeto.
	 */
	public void actua(long nanos);
}

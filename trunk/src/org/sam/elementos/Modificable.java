package org.sam.elementos;

/**
 * Interface indica que un determinado objeto puede ser modificado
 * y proporciona el método para realizar dicha modificación.
 */
public interface Modificable {
	/**
	 * Método que devuelve el {@code Modificador} que modificará este objeto.
	 * @return el {@code Modificador} que modificará este objeto.
	 */
	public Modificador getModificador();
}

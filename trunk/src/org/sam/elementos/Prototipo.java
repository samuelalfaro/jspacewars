package org.sam.elementos;

/**
 * Interface que define que los objetos de las clases que lo implementan, pueden clonarse,
 * generando nuevas instancias del tipo genérico {@code T}.
 * @param <T> Tipo genérico de los objetos devueltos por el método {@link #clone()}.
 */
public interface Prototipo<T> extends Cloneable{
	/**
	 * Método que devuelve una nueva instancia de {@code T}, clonada a partir del {@code Prototipo<T>} que lo invoca.
	 * @return la nueva instancia clonada.
	 */
	public T clone();
}

package org.sam.elementos;

/**
 * Interface que propociona los métodos a las clases que lo implementan, para que los objetos
 * de dichas clases, puedan almacenarse y recuperarse de una {@code Cache}.
 * @param <T> Tipo genérico de los objetos devueltos por el método {@link Prototipo#clone()}.
 */
public interface PrototipoCacheable<T> extends Prototipo<T>, Reseteable{
	/**
	 * Devuelve un valor de un código hash para este objeto.
	 * @return el valor del código hash correspondiente.
	 */
	@Override
	public int hashCode();
}

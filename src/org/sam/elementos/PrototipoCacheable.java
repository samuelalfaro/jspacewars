package org.sam.elementos;

/**
 * @author Samuel
 *
 */
public interface PrototipoCacheable<T> extends Prototipo<T>, Reseteable{
	public int hashCode();
}

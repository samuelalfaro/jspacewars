package org.sam.util;

/**
 * @author Samuel
 *
 */
public interface Prototipo<T> extends Cloneable{
	public int hashCode();
	public T clone();
}

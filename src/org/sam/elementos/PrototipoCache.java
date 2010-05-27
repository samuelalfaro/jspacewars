package org.sam.elementos;

/**
 * @author Samuel
 *
 */
public interface PrototipoCache<T extends Cacheable> extends Prototipo<T>{
	public int hashCode();
}

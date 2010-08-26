package org.sam.elementos;

public interface PrototipoCacheable<T> extends Prototipo<T>, Reseteable{
	public int hashCode();
}

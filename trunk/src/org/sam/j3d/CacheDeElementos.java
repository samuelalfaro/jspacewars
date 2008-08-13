package org.sam.j3d;

import org.sam.util.Cache;

public class CacheDeElementos {
	private static final Cache<Elemento> cache;
	static{
		cache = new Cache<Elemento>(500);
	}
	
	public static void addPrototipo(Elemento prototipo){
		cache.addPrototipo(prototipo);
	}
	
	public static Elemento newObject(int tipo) throws Cache.PrototipoDesconocidoException{
		return cache.newObject(tipo);
	}
	
	public static void cached(Elemento elemento) throws Cache.PrototipoDesconocidoException{
		cache.cached(elemento);
	}
}

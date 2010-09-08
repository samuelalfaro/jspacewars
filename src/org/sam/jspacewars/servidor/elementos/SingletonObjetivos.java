package org.sam.jspacewars.servidor.elementos;

public final class SingletonObjetivos {
	
	private SingletonObjetivos() {
	}

	private static NaveEnemiga objetivoProtas;
	private static NaveUsuario objetivoEnemigos;

	public static Elemento getObjetivo(Nave me) {
		return me instanceof NaveUsuario ? objetivoProtas : objetivoEnemigos;
	}

	@Deprecated
	public static void setObjetivoUsuario(NaveEnemiga objetivo) {
		SingletonObjetivos.objetivoProtas = objetivo;
	}
	
	@Deprecated
	public static void setObjetivoEnemigos(NaveUsuario objetivo) {
		SingletonObjetivos.objetivoEnemigos = objetivo;
	}
}

package org.sam.jspacewars.servidor.elementos;

public final class SingletonObjetivos {
	
	private SingletonObjetivos() {
	}

	private static NaveEnemiga objetivoProtas;
	private static NaveUsuario objetivoEnemigos;

	public static Elemento getObjetivo(Nave me) {
		return me instanceof NaveUsuario ? objetivoProtas : objetivoEnemigos;
	}

	public static void setObjetivoProtas(NaveEnemiga objetivo) {
		SingletonObjetivos.objetivoProtas = objetivo;
	}
	
	public static void setObjetivoEnemigos(NaveUsuario objetivo) {
		SingletonObjetivos.objetivoEnemigos = objetivo;
	}
}

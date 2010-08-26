/**
 * 
 */
package org.sam.jspacewars.servidor.elementos;

/**
 * @author Samuel
 * 
 */
public final class SingletonEnemigos {

	private static NaveEnemiga objetivo;

	public static NaveEnemiga getObjetivo() {
		return objetivo;
	}

	public static void setObjetivo(NaveEnemiga objetivo) {
		SingletonEnemigos.objetivo = objetivo;
	}

	private SingletonEnemigos() {
	}
}

package org.sam.jspacewars.elementos;

/**
 * @author samuel
 * 
 */
public class CanionData {
	/**
	 * Referencia a los distintos disparos que puede lanzar para los distintos
	 * grados del cañon.
	 */
	final int[] vIdDisparos;

	/**
	 * Vector que contiene los tiempos de recarga, en nanosegundos, para los
	 * distintos grados del cañon.
	 */
	final int[] vTRecarga;

	/**
	 * Vector que contiene los desfases del disparo, en nanosegundos, para los
	 * distintos grados del cañon. Por ejemplo, una nave puede tener varios
	 * cañones del mismo grado, con el mismo tiempo de recarga, y que no
	 * disparen a la vez al estar desfasados.
	 */
	final int[] vDesfases;

	/**
	 * Vector que contiene las velocidades del disparo, en unidades por
	 * nanosegundos, para los distintos grados del cañon.
	 */
	final float[] vVelocidades;

	public CanionData(int[] vIdDisparos, int[] vTRecarga, int[] vDesfases, float[] vVelocidades) {
		if( (vIdDisparos.length != vTRecarga.length) || (vTRecarga.length != vDesfases.length)
				|| (vDesfases.length != vVelocidades.length) )
			throw new IllegalArgumentException("Los vectores no tienen el mismo tamaño");
		this.vIdDisparos = vIdDisparos;
		this.vTRecarga = vTRecarga;
		this.vDesfases = vDesfases;
		this.vVelocidades = vVelocidades;
	}
}

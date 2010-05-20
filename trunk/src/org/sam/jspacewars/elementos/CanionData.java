package org.sam.jspacewars.elementos;

/**
 * @author samuel
 * 
 */
public class CanionData {
	/**
	 * Referencia a los distintos {@code Disparo} que puede lanzar para los distintos
	 * grados del {@code Canion}.
	 */
	private final int[] vIdDisparos;

	/**
	 * Vector que contiene los tiempos de recarga, en nanosegundos, para los
	 * distintos grados del {@code Canion}.
	 */
	private final int[] vTRecarga;

//	/**
//	 * Vector que contiene los desfases del disparo, en nanosegundos, para los
//	 * distintos grados del cañon. Por ejemplo, una nave puede tener varios
//	 * cañones del mismo grado, con el mismo tiempo de recarga, y que no
//	 * disparen a la vez al estar desfasados.
//	 */
//	private final int[] vDesfases;

	/**
	 * Vector que contiene las velocidades del disparo, en unidades por
	 * nanosegundo, para los distintos grados del {@code Canion}.
	 */
	private final float[] vVelocidades;

	public CanionData(int[] vIdDisparos, int[] vTRecarga, float[] vVelocidades) {
		if( (vIdDisparos.length != vTRecarga.length) || (vTRecarga.length != vVelocidades.length) )
			throw new IllegalArgumentException("Los vectores no tienen el mismo tamaño");
		this.vIdDisparos = vIdDisparos;
		this.vTRecarga = vTRecarga;
//		this.vDesfases = vDesfases;
		this.vVelocidades = vVelocidades;
	}

	/**
	 * Devuelve el Id del {@code Disparo} que lanzara el {@code Canion} con el {@code grado} actual.
	 * @param grado indice del vector {@code IdDisparos}.
	 * @return Id del {@code Disparo}.
	 */
	public int getIdDisparo(int grado) {
		try{
			return vIdDisparos[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			if(grado < 0)
				return vIdDisparos[0];
			return vIdDisparos[vIdDisparos.length -1];
		}
	}

	/**
	 * Devuelve el tiempo de recarga del {@code Canion} con el {@code grado} actual.
	 * @param grado indice del vector {@code vTRecarga}.
	 * @return tiempo de recarga.
	 */
	public int getTRecarga(int grado) {
		try{
			return vTRecarga[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			if(grado < 0)
				return vTRecarga[0];
			return vTRecarga[vTRecarga.length -1];
		}
	}

//	/**
//	 * Devuelve el desfase del tiempo de recarga del {@code Canion} con el grado actual.
//	 * @param grado indice del vector {@code vDesfases}.
//	 * @return desfase del tiempo de recarga.
//	 */
//	public int getDesfase(int grado) {
//		try{
//			return vDesfases[grado];
//		}catch(ArrayIndexOutOfBoundsException e){
//			e.printStackTrace();
//			if(grado < 0)
//				return vDesfases[0];
//			return vDesfases[vDesfases.length -1];
//		}
//	}

	/**
	 * Devuelve la velocidad a la que sale el {@code Disparo} del {@code Canion} con el {@code grado} actual.
	 * @param grado indice del vector {@code vVelocidades}.
	 * @return velocidad a la que sale el {@code Disparo} del {@code Canion}.
	 */
	public float getVelocidad(int grado) {
		try{
			return vVelocidades[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			if(grado < 0)
				return vVelocidades[0];
			return vVelocidades[vVelocidades.length -1];
		}
	}
}

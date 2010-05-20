package org.sam.jspacewars.elementos;

/**
 * @author samuel
 * 
 */
public class CanionData {
	/**
	 * Referencias al codigo de los distintos {@code Disparo}, que puede lanzar el {@code Canion}, segun su grado.
	 */
	private final int[] vIdDisparos;

	/**
	 * Vector que contiene los tiempos de recarga, en nanosegundos, para los
	 * distintos grados del {@code Canion}.
	 */
	private final long[] vTRecarga;

	/**
	 * Vector que contiene las velocidades del disparo, en unidades por
	 * nanosegundo, para los distintos grados del {@code Canion}.
	 */
	private final float[] vVelocidades;

	/**
	 * Constructor con los valores correspondientes.
	 * @param vIdDisparos {@link org.sam.jspacewars.elementos.CanionData#vIdDisparos vIdDisparos} a asignar.
	 * @param vTRecarga {@link org.sam.jspacewars.elementos.CanionData#vTRecarga vTRecarga} a asignar.
	 * @param vVelocidades {@link org.sam.jspacewars.elementos.CanionData#vVelocidades vVelocidades} a asignar.
	 */
	public CanionData(int[] vIdDisparos, long[] vTRecarga, float[] vVelocidades) {
		if( vIdDisparos  == null || vTRecarga == null || vVelocidades == null )
			throw new IllegalArgumentException("Vector nulo");
		if( vIdDisparos.length == 0 || vTRecarga.length == 0 || vVelocidades.length == 0 )
			throw new IllegalArgumentException("Vector vacio");
		if( (vIdDisparos.length != vTRecarga.length) || (vTRecarga.length != vVelocidades.length) )
			throw new IllegalArgumentException("Los vectores no tienen el mismo tamaño");
		this.vIdDisparos = vIdDisparos;
		this.vTRecarga = vTRecarga;
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
	public long getTRecarga(int grado) {
		try{
			return vTRecarga[grado];
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
			if(grado < 0)
				return vTRecarga[0];
			return vTRecarga[vTRecarga.length -1];
		}
	}

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

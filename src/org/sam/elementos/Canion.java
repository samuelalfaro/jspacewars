package org.sam.elementos;

import org.sam.util.Cache;
import org.sam.util.Prototipo;

public abstract class Canion implements Prototipo<Canion>{
	
	private static short id = 0;
	protected static short getId(){
		if (++id == Short.MAX_VALUE)
			id = 1;
		return id;
	}
	
	protected static Cache<Elemento> cache;

	public final static void setCache(Cache<Elemento> unaCache){
		if (cache == null)
			cache = unaCache;
	}
	
	/**
	 * Referencia a los distintos disparos que puede lanzar para los distintos grados
	 * del cañon.
	 */
	protected final int[] vIdDisparos;
	
	/**
	 * Vector que contiene los tiempos de recarga, en milisegundos, para los distintos
	 * grados del cañon.
	 */
	protected final int[] vTRecarga;
	
	/**
	 * Vector que contiene los desfases del disparo, en milisegundos, para los distintos
	 * grados del cañon.
	 * Por ejemplo, una nave puede tener varios cañones del mismo grado, con el mismo
	 * tiempo de recarga, y que no disparen a la vez al estar desfasados.
	 */
	protected final int[] vDesfases;
	
	/**
	 * Vector que contiene las velocidades del disparo, en unidades por milisegundo,
	 * para los distintos grados del cañon.
	 */
	protected final float[] vVelocidades;

	/**
	 * Grado del cañon, corresponde al indice de los vectores anteriores.
	 */
	protected int grado;
	
	/**
	 * Posicion relativa del cañon respecto al elemento donde esta armado.
	 */
	protected float posX, posY;

	/**
	 * Contador para acumular el tiempo trancurrido.
	 */
	protected transient long tTranscurrido;
	
	protected Canion(
			int[] vIdDisparos,
			int[] vTRecarga,
			int[] vDesfases,
			float[] vVelocidades,
			int grado){

		try{
			if( 	(vIdDisparos.length == vTRecarga.length) &&
					(vTRecarga.length == vDesfases.length) &&
					(vDesfases.length == vVelocidades.length)){

				this.vIdDisparos = vIdDisparos;
				this.vTRecarga = vTRecarga;
				this.vDesfases = vDesfases;
				this.vVelocidades = vVelocidades;
				this.grado = grado;
				tTranscurrido = vTRecarga[grado]-vDesfases[grado];
			}else
				throw new IllegalArgumentException("Los vectores no tienen el mismo tamaño");
		}catch(NullPointerException e){
			throw new IllegalArgumentException("Alguno de los vectores es nulo");
		}
	}

	protected Canion(Canion prototipo){
		this.vIdDisparos = prototipo.vIdDisparos;
		this.vTRecarga = prototipo.vTRecarga;
		this.vDesfases = prototipo.vDesfases;
		this.vVelocidades = prototipo.vVelocidades;
		this.grado = prototipo.grado;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
		tTranscurrido = vTRecarga[grado]-vDesfases[grado];
	}

	public abstract Canion clone();
	
	public final void setPosicion(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
	}
	
	public final void setGrado(int grado) {
		this.grado = grado;
		tTranscurrido = vTRecarga[grado]-vDesfases[grado];
	}
	
	public final void aumentarGrado(int incGrado){
		grado += incGrado;
		tTranscurrido = vTRecarga[grado]-vDesfases[grado];
	}
	
	public final void cargar(){
		tTranscurrido = vTRecarga[grado]-vDesfases[grado];
	}
	
	public abstract void dispara(Nave contenedor, long milis);
}
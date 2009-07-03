package org.sam.elementos;

import java.util.Collection;

import org.sam.util.Cache;

public abstract class Canion implements Cloneable{
	
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
	
	protected final CanionData data;

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
	
	protected Canion(CanionData data){
		this.data = data;
		this.grado = 0;
	}

	protected Canion(Canion prototipo){
		this.data = prototipo.data;
		this.grado = prototipo.grado;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
		tTranscurrido = data.vTRecarga[grado]- data.vDesfases[grado];
	}

	public abstract Canion clone();
	
	public final void setPosicion(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
	}
	
	public final void setGrado(int grado) {
		this.grado = grado;
		tTranscurrido = data.vTRecarga[grado] - data.vDesfases[grado];
	}
	
//	public final void aumentarGrado(int incGrado){
//		grado += incGrado;
//		tTranscurrido = vTRecarga[grado]-vDesfases[grado];
//	}
	
	public final void cargar(){
		tTranscurrido = data.vTRecarga[grado] - data.vDesfases[grado];
	}
	
	public abstract void dispara( float mX, float nX, float mY, float nY, long nanos, Collection <Disparo> dst );
}
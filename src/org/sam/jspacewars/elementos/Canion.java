package org.sam.jspacewars.elementos;

import java.util.Collection;

import org.sam.elementos.Cache;

public abstract class Canion implements Cloneable {

	private static short id = 0;

	protected static short getId() {
		if( ++id == Short.MAX_VALUE )
			id = 1;
		return id;
	}

	protected static Cache<Elemento> cache;

	public final static void setCache(Cache<Elemento> unaCache) {
		if( cache == null )
			cache = unaCache;
	}

	protected final CanionData data;

	/**
	 * Id del {@code Disparo} que lanzara el {@code Canion}, con el {@code grado} actual.
	 */
	protected int idDisparo;
	
	/**
	 * Tiempo de recarga, en nanosegundos, del {@code Canion}, con el {@code grado} actual.
	 */
	protected int tRecarga;
	
	/**
	 * Desfase, en nanosegundos, del {@code Canion}, con el {@code grado} actual.<br/>
	 * Por ejemplo: una {@code Nave} puede tener varios {@code Canion}, de las mismas caracteristicas,
	 * con el mismo grado, con el mismo tiempo de recarga, y que no disparen a la vez al estar desfasados.
	 */
	protected int tDesfase;
	
	/**
	 *  Velocidad, en unidades por nanosegundo, a la que sale el {@code Disparo} del {@code Canion},
	 *  con el {@code grado} actual.
	 */
	protected float velocidad;

	/**
	 * Posicion relativa del cañon respecto al elemento donde esta armado.
	 */
	protected float posX, posY;
	
	/**
	 * Desfase, un valor fracional [0..1], que multiplicado por {@code tRecarga}, sirve para calcular {@code tDesfase}.
	 */
	protected float desfase;
	
	/**
	 * Contador para acumular el tiempo trancurrido.
	 */
	protected transient long tTranscurrido;

	protected Canion(CanionData data) {
		this.data = data;
		this.idDisparo = data.getIdDisparo(0);
		this.tRecarga = data.getTRecarga(0);
		this.tDesfase = 0;
		this.velocidad = data.getVelocidad(0);
		
		this.posX = 0;
		this.posY = 0;
		this.desfase = 0;
	}

	protected Canion(Canion prototipo) {
		this.data = prototipo.data;
		this.idDisparo = prototipo.idDisparo;
		this.tRecarga = prototipo.tRecarga;
		this.tDesfase = prototipo.tDesfase;
		this.velocidad = prototipo.velocidad;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
		this.desfase = prototipo.desfase;
		tTranscurrido = tRecarga - tDesfase;
	}

	public abstract Canion clone();

	public final void setPosicion(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public final void setDesfase(float desfase) {
		this.desfase = desfase;
		this.tDesfase = (int)(desfase * tRecarga);
	}

	public final void setGrado(int grado) {
		this.idDisparo = data.getIdDisparo(grado);
		this.tRecarga = data.getTRecarga(grado);
		this.tDesfase = (int)(desfase * tRecarga);
		this.velocidad = data.getVelocidad(grado);
		
		tTranscurrido = tRecarga - tDesfase;
	}

	public final void cargar() {
		tTranscurrido = tRecarga - tDesfase;
	}

	public abstract void dispara(float mX, float nX, float mY, float nY, long nanos, Collection<? super Disparo> dst);
}
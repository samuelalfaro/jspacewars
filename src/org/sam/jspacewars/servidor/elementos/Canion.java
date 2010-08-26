package org.sam.jspacewars.servidor.elementos;

import java.util.Collection;

import org.sam.elementos.Cache;
import org.sam.elementos.Prototipo;

public abstract class Canion implements Prototipo<Canion> {

	protected static Cache<Elemento> cache;

	public final static void setCache(Cache<Elemento> unaCache) {
		if( cache == null )
			cache = unaCache;
	}

	protected final CanionData data;

	/**
	 * Posicion relativa del cañon respecto al elemento donde esta armado.
	 */
	protected float posX, posY;
	
	/**
	 * Desfase, un valor fracional [0..1], que multiplicado por {@code tRecarga}, sirve para calcular {@code tDesfase}.
	 */
	protected float desfase;
	
	/**
	 * Id del {@code Disparo} que lanzara el {@code Canion}, con el {@code grado} actual.
	 */
	protected transient int idDisparo;
	
	/**
	 * Tiempo de recarga, en nanosegundos, del {@code Canion}, con el {@code grado} actual.
	 */
	protected transient long tRecarga;
	
	/**
	 * Desfase, en nanosegundos, del {@code Canion}, con el {@code grado} actual.<br/>
	 * Por ejemplo: una {@code Nave} puede tener varios {@code Canion}, de las mismas caracteristicas,
	 * con el mismo grado, con el mismo tiempo de recarga, y que no disparen a la vez al estar desfasados.
	 */
	protected transient long tDesfase;
	
	/**
	 *  Velocidad, en unidades por nanosegundo, a la que sale el {@code Disparo} del {@code Canion},
	 *  con el {@code grado} actual.
	 */
	protected transient float velocidad;

	/**
	 * Contador para acumular el tiempo trancurrido.
	 */
	protected transient long tTranscurrido;

	protected Canion(CanionData data) {
		this.data = data;
		this.posX = 0;
		this.posY = 0;
		this.desfase = 0;
		
		this.idDisparo = data.getIdDisparo(0);
		this.tRecarga = data.getTRecarga(0);
		this.tDesfase = 0;
		this.velocidad = data.getVelocidad(0);
	}

	protected Canion(Canion prototipo) {
		this.data = prototipo.data;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
		this.desfase = prototipo.desfase;
		
		this.idDisparo = prototipo.idDisparo;
		this.tRecarga = prototipo.tRecarga;
		this.tDesfase = prototipo.tDesfase;
		this.velocidad = prototipo.velocidad;

		tTranscurrido = tRecarga - tDesfase;
	}

	public abstract Canion clone();

	public final void setPosicion(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public final void setDesfase(float desfase) {
		this.desfase = desfase;
		this.tDesfase = (long)(desfase * tRecarga);
	}

	public final void setGrado(int grado) {
		this.idDisparo = data.getIdDisparo(grado);
		this.tRecarga = data.getTRecarga(grado);
		this.tDesfase = (long)(desfase * tRecarga);
		this.velocidad = data.getVelocidad(grado);
		
		tTranscurrido = tRecarga - tDesfase;
	}

	public final void cargar() {
		tTranscurrido = tRecarga - tDesfase;
	}

	public abstract void dispara(float mX, float nX, float mY, float nY, long nanos, long stopTime, Collection<? super Disparo> dst);
}
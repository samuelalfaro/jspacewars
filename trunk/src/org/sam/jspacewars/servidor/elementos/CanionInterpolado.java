package org.sam.jspacewars.servidor.elementos;

import java.util.Collection;

import org.sam.interpoladores.Trayectoria;

public class CanionInterpolado extends Canion {
	private Trayectoria.Float<float[]> trayectoria;
	/**
	 * Escala de la trayectoria del disparo
	 */
	private float scaleX, scaleY;
	
	public CanionInterpolado(CanionData data) {
		super(data);
	}

	private CanionInterpolado(CanionInterpolado prototipo) {
		super(prototipo);
		this.trayectoria = prototipo.trayectoria;
		this.scaleX = prototipo.scaleX;
		this.scaleY = prototipo.scaleY;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canion clone() {
		return new CanionInterpolado(this);
	}

	public void setScales(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispara(float mX, float nX, float mY, float nY, long nanos, long stopTime, Collection<? super Disparo> dst) {
		long t = tRecarga - tTranscurrido;

		tTranscurrido += nanos;
		while( tTranscurrido >= tRecarga ){
			DisparoInterpolado disparo = (DisparoInterpolado) cache.newObject(idDisparo);

			disparo.setValues( t * mX + nX + posX, t * mY + nY + posY, trayectoria, scaleX, scaleY );
			disparo.actua(nanos - t);
			// disparo.setTime(t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}
package org.sam.jspacewars.elementos;

import java.util.Collection;

public class CanionLineal extends Canion {
	/**
	 * Angulo de disparo del cañon
	 */
	private float angulo;

	public CanionLineal(CanionData data) {
		super(data);
	}

	private CanionLineal(CanionLineal prototipo) {
		super(prototipo);
		this.angulo = prototipo.angulo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canion clone() {
		return new CanionLineal(this);
	}

	public void setAngulo(float angulo) {
		this.angulo = (float) (angulo * Math.PI / 180.0 );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispara(float mX, float nX, float mY, float nY, long nanos, long stopTime, Collection<? super Disparo> dst){

		long t = tRecarga - tTranscurrido;

		tTranscurrido += nanos;
		while( tTranscurrido >= tRecarga ){
			DisparoLineal disparo = (DisparoLineal) cache.newObject(idDisparo);

			disparo.setValues(t * mX + nX + posX, t * mY + nY + posY, angulo, velocidad);
			disparo.actua(nanos - t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}
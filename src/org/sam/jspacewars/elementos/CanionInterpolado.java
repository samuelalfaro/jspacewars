package org.sam.jspacewars.elementos;

import java.util.Collection;

public class CanionInterpolado extends Canion {
	/**
	 * Escala de la trayectoria del disparo
	 */
	private float scaleX, scaleY;

	public CanionInterpolado(CanionData data) {
		super(data);
	}

	private CanionInterpolado(CanionInterpolado prototipo) {
		super(prototipo);
		this.scaleX = prototipo.scaleX;
		this.scaleY = prototipo.scaleY;
	}

	public Canion clone() {
		return new CanionInterpolado(this);
	}

	public void setScales(float scaleX, float scaleY) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Canion#dispara(float, float, float, float, long,
	 * java.util.Collection)
	 */
	public void dispara(float mX, float nX, float mY, float nY, long nanos, Collection<? super Disparo> dst) {
		long t = tRecarga - tTranscurrido;

		tTranscurrido += nanos;
		while( tTranscurrido >= tRecarga ){
			DisparoInterpolado disparo = (DisparoInterpolado) cache.newObject(idDisparo);
			disparo.setId(Canion.getId());

			disparo.setValues(t * mX + nX + posX, t * mY + nY + posY, scaleX, scaleY);
			disparo.actua(nanos - t);
			// disparo.setTime(t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}
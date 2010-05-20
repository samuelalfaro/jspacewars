package org.sam.jspacewars.elementos;

import java.util.Collection;

public class LanzaMisiles extends Canion {
	/**
	 * Angulo de disparo del cañon
	 */
	private float angulo;

	public LanzaMisiles(CanionData data) {
		super(data);
	}

	private LanzaMisiles(LanzaMisiles prototipo) {
		super(prototipo);
		this.angulo = prototipo.angulo;
	}

	public Canion clone() {
		return new LanzaMisiles(this);
	}

	public void setAngulo(float angulo) {
		this.angulo = (float) (angulo / 180.0 * Math.PI);
	}

	public void dispara(float mX, float nX, float mY, float nY, long nanos, Collection<? super Disparo> dst){
//		int tRecarga = data.vTRecarga[grado];
		int t = tRecarga - (int) tTranscurrido;

		tTranscurrido += nanos;
		while( tTranscurrido >= tRecarga ){
			Misil disparo = (Misil) cache.newObject(idDisparo);
			disparo.setId(Canion.getId());

			disparo.setValues(t * mX + nX + posX, t * mY + nY + posY, angulo, velocidad);
			// TODO cambiar esta ñapa
			disparo.setObjetivo(SingletonEnemigos.getObjetivo());
			disparo.actua(nanos - t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}
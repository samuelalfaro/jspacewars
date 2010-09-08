package org.sam.jspacewars.servidor.elementos;

import java.util.Collection;

/**
 * Implementacion de un {@link Canion} que dispara {@link Misil misiles}.
 */
public class LanzaMisiles extends Canion {
	/**
	 * Ángulo inicial en radianes de los misiles disparados por este {@code LanzaMisiles}.
	 */
	private float angulo;

	/**
	 * Constructor que crea un {@code LanzaMisiles} y asigna los valores correspondientes.
	 * @param data {@link #data Datos del cañón} asignados.
	 */
	public LanzaMisiles(CanionData data) {
		super(data);
	}

	/**
	 * Construtor que crea un {@code LanzaMisiles} copiando los
	 * datos de otro {@code LanzaMisiles} que sirve como prototipo.
	 * @param prototipo {@code LanzaMisiles} prototipo.
	 */
	private LanzaMisiles(LanzaMisiles prototipo) {
		super(prototipo);
		this.angulo = prototipo.angulo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Canion clone() {
		return new LanzaMisiles(this);
	}

	/**
	 * <i>Setter</i> que asigna el {@link #angulo ángulo inicial} en grados de este {@code LanzaMisiles}.
	 * @param angulo Ángulo inicial asignado.
	 */
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
			Misil disparo = (Misil) cache.newObject(idDisparo);

			disparo.setValues(t * mX + nX + posX, t * mY + nY + posY, angulo, velocidad);
			// TODO cambiar esta ñapa
			disparo.setObjetivo(SingletonObjetivos.getObjetivo());
			disparo.actua(nanos - t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}
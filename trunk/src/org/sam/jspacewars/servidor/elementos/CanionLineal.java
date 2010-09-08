package org.sam.jspacewars.servidor.elementos;

import java.util.Collection;

/**
 * Implementación de un {@link Canion} que lanza {@link DisparoLineal disparos} con una trayectoria recta.
 */
public class CanionLineal extends Canion {
	/**
	 * Ángulo, en radianes, del cañón.
	 */
	private float angulo;

	/**
	 * Constructor que crea un {@code CanionLineal} y asigna los valores correspondientes.
	 * @param data {@link #data Datos del cañón} asignados.
	 */
	public CanionLineal(CanionData data) {
		super(data);
	}

	/**
	 * Construtor que crea un {@code CanionLineal} copiando los
	 * datos de otro {@code CanionLineal} que sirve como prototipo.
	 * @param prototipo {@code CanionLineal} prototipo.
	 */
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

	/**
	 * <i>Setter</i> que asigna el {@link #angulo ángulo}, en grados, de este {@code CanionLineal}.
	 * @param angulo Ángulo asignado.
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
			DisparoLineal disparo = (DisparoLineal) cache.newObject(idDisparo);

			disparo.setValues(t * mX + nX + posX, t * mY + nY + posY, angulo, velocidad);
			disparo.actua(nanos - t);
			dst.add(disparo);
			t += tRecarga;
			tTranscurrido -= tRecarga;
		}
	}
}
package org.sam.jspacewars.servidor.elementos;

import java.util.Collection;

import org.sam.interpoladores.Trayectoria;

/**
 * Implementacion de un {@link Canion} que lanza {@link DisparoInterpolado disparos}
 * con una trayectoria predefinida.
 */
public class CanionInterpolado extends Canion {
	
	private Trayectoria.Float<float[]> trayectoria;
	
	/**
	 * Escala de la trayectoria del disparo
	 */
	private float scaleX, scaleY;
	
	/**
	 * Constructor que crea un {@code CanionLineal} y asigna los valores correspondientes.
	 * @param data {@link #data Datos del cañón} asignados.
	 */
	public CanionInterpolado(CanionData data) {
		super(data);
	}

	/**
	 * Construtor que crea un {@code CanionInterpolado} copiando los
	 * datos de otro {@code CanionInterpolado} que sirve como prototipo.
	 * @param prototipo {@code CanionInterpolado} prototipo.
	 */
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

	/**
	 * <i>Setter</i> que asigna los valores que escalan la trayectoría que siguen
	 * los diparos lanzados por este {@code CanionInterpolado}.
	 * @param scaleX Valor de escalado en el eje X.
	 * @param scaleY Valor de escalado en el eje Y.
	 */
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
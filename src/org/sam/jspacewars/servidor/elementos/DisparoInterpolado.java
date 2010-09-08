package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;
import org.sam.interpoladores.Trayectoria;

/**
 * Implementación de un {@link Disparo} que sigue una trayectoria predefinida.
 */
public class DisparoInterpolado extends Disparo {

	private transient Trayectoria.Float<float[]> trayectoria;
	private transient float despX, despY;
	private transient float scaleX, scaleY;
	private transient long time;
	
	DisparoInterpolado(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code DisparoInterpolado} copiando los
	 * datos de otro {@code DisparoInterpolado} que sirve como prototipo.
	 * @param prototipo {@code DisparoInterpolado} prototipo.
	 */
	protected DisparoInterpolado(DisparoInterpolado prototipo) {
		super(prototipo);
	}

	public DisparoInterpolado clone() {
		return new DisparoInterpolado(this);
	}

	public void setValues(float posX, float posY, Trayectoria.Float<float[]> trayectoria, float scaleX, float scaleY) {
		super.setPosicion(posX, posY);
		despX = posX;
		despY = posY;
		this.trayectoria = trayectoria;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.time = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Disparo#actua(long)
	 */
	public void actua(long nanos) {
		time += nanos;
		float posTan[][] = trayectoria.getPosTan((time));
		setAngulo((float)Math.atan2(posTan[1][1] * scaleY, posTan[1][0] * scaleX));
		setPosicion( posTan[0][0] * scaleX + despX, posTan[0][1] * scaleY + despY );
	}
}
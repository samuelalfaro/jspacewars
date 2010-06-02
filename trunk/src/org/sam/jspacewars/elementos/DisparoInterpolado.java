package org.sam.jspacewars.elementos;

import org.sam.interpoladores.Trayectoria;

public class DisparoInterpolado extends Disparo {

	private transient float despX, despY;
	private transient float scaleX, scaleY;
	private transient long time;
	private final Trayectoria.Float<float[]> trayectoria;

	DisparoInterpolado(short code, Trayectoria.Float<float[]> trayectoria) {
		super(code);
		this.trayectoria = trayectoria;
	}

	protected DisparoInterpolado(DisparoInterpolado prototipo) {
		super(prototipo);
		this.trayectoria = prototipo.trayectoria;
	}

	public DisparoInterpolado clone() {
		return new DisparoInterpolado(this);
	}

	public void setValues(float posX, float posY, float scaleX, float scaleY) {
		super.setPosicion(posX, posY);
		despX = posX;
		despY = posY;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.time = 0;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Disparo#actua(long)
	 */
	public void actua(long nanos) {
		time += nanos;
		float posTan[][] = trayectoria.getPosTan((time));
		posX = posTan[0][0] * scaleX + despX;
		posY = posTan[0][1] * scaleY + despY;
		angulo = (float)Math.atan2(posTan[1][1] * scaleY, posTan[1][0] * scaleX);
	}
}
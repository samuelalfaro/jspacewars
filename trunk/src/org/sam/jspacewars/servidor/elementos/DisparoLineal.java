package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;

public class DisparoLineal extends Disparo {

	private transient float incX, incY;

	DisparoLineal(short code, Poligono forma) {
		super(code, forma);
	}

	protected DisparoLineal(Disparo prototipo) {
		super(prototipo);
	}

	public DisparoLineal clone() {
		return new DisparoLineal(this);
	}

	public void setValues(float posX, float posY, float angulo, float velocidad) {
		super.setPosicion(posX, posY);
		super.setAngulo(angulo);
		this.incX = (float) Math.cos(angulo) * velocidad;
		this.incY = (float) Math.sin(angulo) * velocidad;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sam.elementos.Disparo#actua(long)
	 */
	public void actua(long nanos) {
		setPosicion( getX() + incX * nanos, getY() + incY * nanos );
	}
}

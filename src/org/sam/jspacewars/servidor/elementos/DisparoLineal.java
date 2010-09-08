package org.sam.jspacewars.servidor.elementos;

import org.sam.colisiones.Poligono;

/**
 * Implementación de un {@link Disparo} que sigue una trayectoria recta.
 */
public class DisparoLineal extends Disparo {

	private transient float incX, incY;

	DisparoLineal(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code DisparoLineal} copiando los
	 * datos de otro {@code DisparoLineal} que sirve como prototipo.
	 * @param prototipo {@code DisparoLineal} prototipo.
	 */
	protected DisparoLineal(DisparoLineal prototipo) {
		super(prototipo);
	}

	public DisparoLineal clone() {
		return new DisparoLineal(this);
	}

	/**
	 * @param posX
	 * @param posY
	 * @param angulo 
	 * @param velocidad
	 */
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

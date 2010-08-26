package org.sam.jspacewars.elementos;

import java.nio.ByteBuffer;

import org.sam.colisiones.Colisionable;

public abstract class Disparo extends Elemento {

	private transient float angulo;

	protected Disparo(short code) {
		super(code);
	}

	protected Disparo(Disparo prototipo) {
		super(prototipo);
	}

	/**
	 * @return el angulo solicitado.
	 */
	public float getAngulo() {
		return angulo;
	}

	/**
	 * @param angulo valor del angulo asignado.
	 */
	public void setAngulo(float angulo) {
		this.getForma().rotar(angulo);
		this.angulo = angulo;
	}
	
	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
	
	@Override
	public void colisionar(Colisionable otro) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void recibirImpacto(int fuerzaDeImpacto) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isDestruido() {
		return false;
	}
}
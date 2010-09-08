package org.sam.jspacewars.servidor.elementos;

import java.nio.ByteBuffer;

import org.sam.colisiones.Poligono;

public abstract class Disparo extends Elemento {

	private transient float angulo;

	protected Disparo(short code, Poligono forma) {
		super(code, forma);
	}

	/**
	 * Construtor que crea un {@code Disparo} copiando los
	 * datos de otro {@code Disparo} que sirve como prototipo.
	 * @param prototipo {@code Disparo} prototipo.
	 */
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
		this.getForma().actualizarLimiteRectangular();
		this.angulo = angulo;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
	
	boolean destruido = false;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inicializar(){
		destruido = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recibirImpacto(int fuerzaDeImpacto) {
		destruido = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDestruido() {
		return destruido;
	}
}
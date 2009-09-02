package org.sam.jspacewars.elementos;

import java.nio.ByteBuffer;

import org.sam.elementos.Dinamico;

public abstract class Disparo extends Elemento implements Dinamico {

	protected transient float angulo;

	protected Disparo(short code) {
		super(code);
	}

	protected Disparo(Disparo prototipo) {
		super(prototipo);
	}

	public boolean finalizado() {
		return false;
	}

	public abstract void actua(long milis);

	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
}
package org.sam.jspacewars.elementos;

import java.nio.ByteBuffer;

public abstract class Disparo extends ElementoDinamico {

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

	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
}
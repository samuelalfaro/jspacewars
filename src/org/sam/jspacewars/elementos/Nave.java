package org.sam.jspacewars.elementos;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.sam.colisiones.Colisionable;

public abstract class Nave extends Elemento {

	protected transient float angulo;
	protected transient Collection<Disparo> dstDisparos;

	public Nave(short tipo) {
		super(tipo);
		dstDisparos = null;
	}

	protected Nave(Nave prototipo) {
		super(prototipo);
		dstDisparos = prototipo.dstDisparos;
	}

	public final Collection<Disparo> getDstDisparos() {
		return dstDisparos;
	}

	public final void setDstDisparos(Collection<Disparo> dstDisparos) {
		this.dstDisparos = dstDisparos;
	}

	public abstract void iniciar();

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
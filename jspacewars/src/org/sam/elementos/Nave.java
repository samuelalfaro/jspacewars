package org.sam.elementos;

import java.nio.ByteBuffer;
import java.util.Collection;


public abstract class Nave extends Elemento implements Dinamico{

	protected transient float angulo;
	protected transient Collection <Disparo> dstDisparos;
	
	public Nave(short tipo){
		super(tipo);
		dstDisparos = null;
	}

	protected Nave(Nave prototipo){
		super(prototipo);
		dstDisparos = prototipo.dstDisparos;
	}
	
	public final Collection <Disparo> getDstDisparos() {
		return dstDisparos;
	}

	public final void setDstDisparos(Collection <Disparo> dstDisparos) {
		this.dstDisparos = dstDisparos;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.elementos.Elemento#clone()
	 */
	public abstract Nave clone();
	
	public abstract void iniciar();
	
	public void enviar(ByteBuffer buff) {
		super.enviar(buff);
		buff.putFloat(angulo);
	}
}
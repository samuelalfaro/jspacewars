package org.sam.jspacewars.servidor.elementos;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.sam.colisiones.Poligono;

public abstract class Nave extends Elemento {

	protected transient float angulo;
	protected transient Collection<Disparo> dstDisparos;

	public Nave(short code, Poligono forma) {
		super(code, forma);
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
	
	int resistencia = 1000;
	public void inicializar(){
		resistencia = 1000;
	}
	
	/* (non-Javadoc)
	 * @see org.sam.jspacewars.servidor.elementos.Destruible#recibirImpacto(int)
	 */
	@Override
	public void recibirImpacto(int fuerzaDeImpacto) {
		//resistencia -= fuerzaDeImpacto;
	}

	@Override
	public boolean isDestruido() {
		return resistencia <= 0;
	}
}
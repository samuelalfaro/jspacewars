package org.sam.elementos;

import java.util.Collection;


public abstract class Nave extends Elemento implements ElementoDinamico{

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
	
	public abstract Nave clone();
	
	public abstract void iniciar();
	
}
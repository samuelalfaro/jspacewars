package org.sam.elementos;

public abstract class Disparo extends Elemento implements ElementoDinamico{

	protected Disparo(short code){
		super(code);
	}

	protected Disparo(Disparo prototipo){
		super(prototipo);
	}

	public boolean finalizado(){
		return false;
	}

	public abstract void actua(long milis);

	public double getAngulo(){
		return 0;
	}
}
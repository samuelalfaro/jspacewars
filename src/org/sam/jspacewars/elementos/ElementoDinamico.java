package org.sam.jspacewars.elementos;

import org.sam.colisiones.Colisionable;
import org.sam.colisiones.Limites;
import org.sam.elementos.Dinamico;

public abstract class ElementoDinamico extends Elemento implements Dinamico, Colisionable {

	public ElementoDinamico(short tipo) {
		super(tipo);
	}

	protected ElementoDinamico(ElementoDinamico prototipo) {
		super(prototipo);
	}
	
	public Limites getLimites(){
		return null;
	}

	public boolean hayColision(Colisionable otro){
		return false;
	}

	public void colisionar(Colisionable otro){
		
	}
	
	public int getFuerzaImpacto(){
		return 0;
	}
	
	public void recibirImpacto(int fuerzaDeImpacto){
		
	}
	
	public boolean isDestruido(){
		return false;
	}
	
	public void reset(){
	}
}
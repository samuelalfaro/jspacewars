package org.sam.red.servidor;

import java.nio.ByteBuffer;
import java.util.Comparator;

import org.sam.red.Colisionable;
import org.sam.red.Enviable;
import org.sam.util.Prototipo;

abstract class Elemento implements Prototipo<Elemento>, Colisionable, Enviable{
	private int nextId;
	protected int tipo, id;
	protected int posX, posY;
	
	Elemento(int tipo){
		this.tipo = tipo;
		this.id = this.nextId = 0;
	}
	
	Elemento(Elemento prototipo){
		this.tipo = prototipo.tipo;
		this.id = prototipo.nextId();
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
	}
	
	public int hashCode(){
		return tipo;
	}
	
	private int nextId(){
		if(nextId++==256)
			nextId = 0;
		return nextId;
	}
	
	public void setPosicion(int posX, int posY){
		this.posX = posX;
		this.posY = posY;
	}
	
	public final int getX(){
		return this.posX;
	}
	
	public final int getY(){
		return this.posY;
	}
	
	public abstract void actua();
	
	public void enviar(ByteBuffer buff){
		buff.putShort((short)tipo);
		buff.putShort((short)id);
		buff.putInt(posX);
		buff.putInt(posY);
	}
	
	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#hayColision(org.sam.red.Colisionable)
	 */
	public boolean hayColision(Colisionable otro) {
		Elemento otroElemento = (Elemento)otro;
		return  Math.abs(this.posX - otroElemento.posX) <10 && Math.abs(this.posY - otroElemento.posY)<10;
	}

	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#colisionar(org.sam.red.Colisionable)
	 */
	public void colisionar(Colisionable otro) {
		this.recibirImpacto(otro.getFuerzaImpacto());
		otro.recibirImpacto(this.getFuerzaImpacto());
	}
	
	public abstract Elemento clone();
}

class ComparadorDeElementos implements Comparator<Elemento>{
	public int compare(Elemento e1, Elemento e2){
		int comparacion = e1.tipo - e2.tipo;
		if ( comparacion != 0 )
			return comparacion;
		return e1.id - e2.id;
	}
}

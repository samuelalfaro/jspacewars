package org.sam.red.cliente;

import java.awt.Polygon;
import java.nio.ByteBuffer;
import java.util.Comparator;

import org.sam.red.Recibible;
import org.sam.util.Prototipo;

abstract class Elemento implements Prototipo<Elemento>, Recibible{
	protected final int tipo;
	protected int id;
	protected float posX, posY;
	protected Polygon forma;
	
	Elemento(int tipo, Polygon forma){
		this.tipo = tipo;
		this.forma = new Polygon(forma.xpoints,forma.ypoints,forma.npoints);
	}
	
	public int hashCode(){
		return tipo;
	}
	
	public final void setId(int id){
		this.id = id;
	}
	
	public final void setPosicion(float posX, float posY){
		forma.translate((int)(posX-this.posX), (int)(posY-this.posY));
		this.posX = posX;
		this.posY = posY;
	}
	
	public final float getX(){
		return this.posX;
	}
	
	public final float getY(){
		return this.posY;
	}
	
	public final Polygon getForma(){
		return forma;
	}
	
	public void recibir(ByteBuffer buff){
		setPosicion(buff.getInt(),buff.getInt());
	}
	
	@SuppressWarnings("unchecked")
	public abstract Elemento clone();
}

class ComparadorDeElementos implements Comparator<Elemento>{
	public int compare(Elemento e1, Elemento e2){
		int comparacion;
		if ((comparacion = e2.tipo - e1.tipo )!=0)
			return comparacion;
		return e2.id - e1.id;
	}
}
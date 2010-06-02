package org.sam.jspacewars.elementos;

import java.nio.ByteBuffer;
import java.util.Comparator;

import org.sam.colisiones.Colisionable;
import org.sam.colisiones.Limites;
import org.sam.colisiones.Poligono;
import org.sam.elementos.Dinamico;
import org.sam.elementos.Enviable;
import org.sam.elementos.PrototipoCacheable;

public abstract class Elemento implements PrototipoCacheable<Elemento>, Enviable, Dinamico, Colisionable, Destruible {

	public static Comparator<Elemento> COMPARADOR = new Comparator<Elemento>() {
		public int compare(Elemento e1, Elemento e2) {
			int comparacion = e1.type - e2.type;
			return comparacion != 0 ? comparacion: e1.id - e2.id;
		}
	};

	private final short type;
	private short id;
	protected transient float posX, posY;
	protected Poligono forma;

	Elemento(short type) {
		this.type = type;
		this.posX = 0.0f;
		this.posY = 0.0f;
	}

	protected Elemento(Elemento prototipo) {
		this.type = prototipo.type;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
	}

	public final void setId(short id) {
		this.id = id;
	}

	public final int hashCode() {
		return type;
	}

	public void setPosicion(float posX, float posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public final float getX() {
		return this.posX;
	}

	public final float getY() {
		return this.posY;
	}

	@Override
	public final Limites getLimites(){
		return forma.getLimites();
	}

	@Override
	public final boolean hayColision(Colisionable otro){
		return forma.hayColision((Poligono)otro);
	}
	
	public void enviar(ByteBuffer buff) {
		buff.putShort(type);
		buff.putShort(id);
		buff.putFloat(posX);
		buff.putFloat(posY);
	}
	
	public abstract Elemento clone();
}
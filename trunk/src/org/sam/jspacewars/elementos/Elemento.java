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

	private transient AutoIncrementable incrementador;
	
	interface AutoIncrementable{
		short getNextId();
	}
	
	private final AutoIncrementable getAutoIncrementable(){
		if(incrementador == null)
			incrementador = new AutoIncrementable(){
			private transient short id = 0;

			public short getNextId() {
				if( ++id == Short.MAX_VALUE )
					id = 1;
				return id;
			}
		};
		return incrementador;
	}
	
	private final Elemento prototipo;
	private final short type;
	
	private short id;
	protected transient float posX, posY;
	protected Poligono forma;

	Elemento(short type) {
		this.prototipo = null;
		this.type = type;
		this.id = 0;
		this.posX = 0.0f;
		this.posY = 0.0f;
	}

	protected Elemento(Elemento prototipo) {
		this.prototipo = prototipo;
		this.type = prototipo.type;
		this.incrementador = null;
		this.id = prototipo.getAutoIncrementable().getNextId();
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
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
	
	@Override
	public final void reset() {
		this.id = prototipo.getAutoIncrementable().getNextId();
	}
}
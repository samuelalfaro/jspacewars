package org.sam.jspacewars.elementos;

import java.nio.ByteBuffer;
import java.util.Comparator;

import org.sam.elementos.*;

public class Elemento implements Prototipo<Elemento>, Enviable, Cacheable{
	
	public static Comparator<Elemento> COMPARADOR = new Comparator<Elemento>(){
		public int compare(Elemento e1, Elemento e2){
			int comparacion = e1.code - e2.code;
			if ( comparacion != 0 )
				return comparacion;
			return e1.id - e2.id;
		}
	};
	
	private final short code;
	private short id;
	protected transient float posX, posY;
	
	Elemento(short code){
		this.code = code;
		this.posX = 0.0f;
		this.posY = 0.0f;
	}
	
	protected Elemento(Elemento prototipo){
		this.code = prototipo.code;
		this.posX = prototipo.posX;
		this.posY = prototipo.posY;
	}
	
	public final void setId(short id){
		this.id = id;
	}
	
	public final int hashCode(){
		return code;
	}
	
	public Elemento clone() {
		return new Elemento(this);
	}
	
	public void setPosicion(float posX, float posY){
		this.posX = posX;
		this.posY = posY;
	}
	
	public final float getX(){
		return this.posX;
	}
	
	public final float getY(){
		return this.posY;
	}

	public void enviar(ByteBuffer buff) {
		buff.putShort(code);
		buff.putShort(id);
		buff.putFloat(posX);
		buff.putFloat(posY);
	}

	/* (non-Javadoc)
	 * @see org.sam.util.Prototipo#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		
	}
}
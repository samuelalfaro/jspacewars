package org.sam.red.servidor;

import java.util.Collection;

import org.sam.red.KeysState;
import org.sam.util.Cache;

class Nave extends Elemento{
	private static Cache<Elemento> cache;
	
	static void setCache(Cache<Elemento> cache){
		if (Nave.cache!=null)
			return;
		Nave.cache = cache;
	}

	private int keysState;
	private int velocidad;
	private Collection<Disparo> disparos;
	private int tiempoRecarga = 130;
	
	public Nave(int tipo){
		super(tipo);
	}
	
	private Nave(Nave prototipo){
		super(prototipo);
		this.velocidad = prototipo.velocidad;
		this.disparos =  prototipo.disparos;
		this.tiempoRecarga =  prototipo.tiempoRecarga;
	}
	
	public void setVelodidad(int velocidad){
		this.velocidad = velocidad;
	}
	
	public void setPropiedadesDisparos(Collection<Disparo> disparos, int tiempoRecarga){
		this.disparos = disparos;
		this.tiempoRecarga = tiempoRecarga;
	}
	
	public Nave clone(){
		return new Nave(this);
	}
	
	public void setKeyState(int keysState){
		this.keysState = keysState;
	}
	
	private long tUltimoDisparo = 0;
	
	private Disparo nuevoDisparo(int tipo, int posX, int posY, int incX, int incY){
		Disparo d = (Disparo)cache.newObject(tipo);
		d.setPosicion(posX,posY);
		d.setVelocidad(incX,incY);
		d.setDestruido(false);
		return d;
	}
	
	public void actua(){
		int incX = 0, incY = 0;
		
		if((keysState & KeysState.SUBE) != 0){
			if (posY > 0) incY = -velocidad;
		}else if ((keysState & KeysState.BAJA) != 0){
			if (posY < ServidorJuego.ALTO-55) incY = velocidad;
		}
		if((keysState & KeysState.ACELERA) != 0){
			if (posX < ServidorJuego.ANCHO-60) incX = velocidad;
		}else if ((keysState & KeysState.FRENA) != 0){
			if (posX > 0) incX = -velocidad;
		}
		if( incX!=0 || incY!=0 ){
			posX += incX;
			posY += incY;
		}
		posX += incX;
		posY += incY;
		
		//TODO Igonorar esta linea para que la nave pueda disparar.
		//if(true) return;
		
		long tActual = System.currentTimeMillis();
		if ( (keysState & KeysState.DISPARO) != 0 &&  tActual - tUltimoDisparo > tiempoRecarga ){
			tUltimoDisparo = System.currentTimeMillis();
			disparos.add(nuevoDisparo(1,posX+55,posY+5,18,-2));
			disparos.add(nuevoDisparo(2,posX+58,posY+10,19,-1));
			disparos.add(nuevoDisparo(3,posX+60,posY+15,20,0));
			disparos.add(nuevoDisparo(2,posX+58,posY+20,19,1));
			disparos.add(nuevoDisparo(1,posX+55,posY+25,18,2));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#getFuerzaImpacto()
	 */
	public int getFuerzaImpacto() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#recibirImpacto(int)
	 */
	public void recibirImpacto(int fuerzaDeImpacto) {
	}
	
	/* (non-Javadoc)
	 * @see org.sam.red.Colisionable#getDestruido()
	 */
	public boolean isDestruido() {
		return false;
	}
}
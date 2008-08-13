package org.sam.elementos;


public class DisparoLineal extends Disparo{
	
	private transient float velocidad;
	private transient float angulo;
	private transient float incX, incY;
	
	DisparoLineal(short code){
		super(code);
	}
	
	protected DisparoLineal(Disparo prototipo){
		super(prototipo);
	}
	
	public DisparoLineal clone() {
		return new DisparoLineal(this);
	}
	
	public void setValues(float posX, float posY, float angulo, float velocidad){
		super.setPosicion(posX, posY);
		this.velocidad = velocidad/1000.0f;
		this.angulo = angulo;
		this.incX = (float)Math.cos(angulo)*this.velocidad;
		this.incY = (float)Math.sin(angulo)*this.velocidad;
	}
	
	public void actua(long milis){
		posX += incX*milis;
		posY += incY*milis;
	}
	
	public double getAngulo(){
		return angulo;
	}
}
